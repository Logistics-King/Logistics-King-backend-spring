package logisticsking.com.logisticskingbackendspring.infra.permission

import io.swagger.v3.oas.annotations.Operation
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPoint
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPointRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@Component
class EndPointAutoRegistrar(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    private val endPointRepository: EndPointRepository,
) : ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments) {
        findApiMappings()
            .filter { mapping -> shouldRegister(mapping.url) }
            .forEach { mapping -> syncEndPoint(mapping) }
    }

    @Suppress("DEPRECATION")
    private fun findApiMappings(): List<ApiMapping> {
        return requestMappingHandlerMapping.handlerMethods.keys
            .flatMap { requestMappingInfo ->
                val urls = requestMappingInfo.pathPatternsCondition?.patternValues
                    ?: requestMappingInfo.patternsCondition?.patterns
                    ?: emptySet()
                val methods = requestMappingInfo.methodsCondition.methods.ifEmpty { DEFAULT_METHODS }
                val handlerMethod = requestMappingHandlerMapping.handlerMethods[requestMappingInfo]
                    ?: return@flatMap emptyList()

                urls.flatMap { url ->
                    methods.map { method ->
                        ApiMapping(
                            url = url,
                            method = method.name,
                            handlerMethod = handlerMethod,
                        )
                    }
                }
            }
    }

    private fun shouldRegister(url: String): Boolean {
        return url.startsWith(API_PREFIX) &&
            !url.startsWith(PUBLIC_AUTH_PREFIX)
    }

    private fun syncEndPoint(mapping: ApiMapping) {
        val roles = rolesFor(mapping.handlerMethod)
        val description = describe(mapping.handlerMethod)
        val current = endPointRepository.findByUrlAndMethod(mapping.url, mapping.method)

        endPointRepository.save(
            current
                ?.let {
                    EndPoint.restore(
                        id = it.id,
                        url = mapping.url,
                        method = mapping.method,
                        roles = roles,
                        description = description,
                    )
                }
                ?: EndPoint.create(
                    url = mapping.url,
                    method = mapping.method,
                    roles = roles,
                    description = description,
                )
        )
    }

    private fun rolesFor(handlerMethod: HandlerMethod): Set<UserRole> {
        val access = handlerMethod.getMethodAnnotation(EndpointAccess::class.java)
            ?: handlerMethod.beanType.getAnnotation(EndpointAccess::class.java)

        val accessRoles = access
            ?.roles
            ?.toSet()
            ?.takeIf { it.isNotEmpty() }

        return accessRoles.orEmpty() + DEFAULT_ROLE
    }

    private fun describe(handlerMethod: HandlerMethod): String {
        val accessDescription = handlerMethod.getMethodAnnotation(EndpointAccess::class.java)?.description
            ?: handlerMethod.beanType.getAnnotation(EndpointAccess::class.java)?.description
            ?: ""
        if (accessDescription.isNotBlank()) {
            return accessDescription
        }

        val operation = handlerMethod.getMethodAnnotation(Operation::class.java)
        return operation?.description?.takeIf { it.isNotBlank() }
            ?: operation?.summary?.takeIf { it.isNotBlank() }
            ?: "보호 API 권한 관리 URL"
    }

    private data class ApiMapping(
        val url: String,
        val method: String,
        val handlerMethod: HandlerMethod,
    )

    private companion object {
        private const val API_PREFIX = "/api/v1/"
        private const val PUBLIC_AUTH_PREFIX = "/api/v1/auth/"
        private val DEFAULT_ROLE = UserRole.ADMIN
        private val DEFAULT_METHODS = setOf(
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.PATCH,
            RequestMethod.DELETE,
        )
    }
}
