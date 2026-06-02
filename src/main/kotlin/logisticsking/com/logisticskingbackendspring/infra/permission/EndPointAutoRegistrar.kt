package logisticsking.com.logisticskingbackendspring.infra.permission

import logisticsking.com.logisticskingbackendspring.domain.permission.EndPoint
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPointRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@Component
class EndPointAutoRegistrar(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    private val endPointRepository: EndPointRepository,
) : ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments) {
        findApiPatterns()
            .filter { url -> shouldRegister(url) }
            .forEach { url -> syncEndPoint(url) }
    }

    @Suppress("DEPRECATION")
    private fun findApiPatterns(): Set<String> {
        return requestMappingHandlerMapping.handlerMethods.keys
            .flatMap { requestMappingInfo ->
                requestMappingInfo.pathPatternsCondition?.patternValues
                    ?: requestMappingInfo.patternsCondition?.patterns
                    ?: emptySet()
            }
            .toSet()
    }

    private fun shouldRegister(url: String): Boolean {
        return url.startsWith(API_PREFIX) &&
            !url.startsWith(PUBLIC_AUTH_PREFIX)
    }

    private fun syncEndPoint(url: String) {
        val roles = rolesFor(url)
        val description = describe(url)
        val current = endPointRepository.findByUrl(url)

        endPointRepository.save(
            current
                ?.let {
                    EndPoint.restore(
                        id = it.id,
                        url = it.url,
                        roles = roles,
                        description = description,
                    )
                }
                ?: EndPoint.create(
                    url = url,
                    roles = roles,
                    description = description,
                )
        )
    }

    private fun rolesFor(url: String): Set<UserRole> {
        val domainRoles = when {
            url.startsWith("/api/v1/vendors") -> setOf(UserRole.VENDOR)
            url.startsWith("/api/v1/agencies") -> setOf(UserRole.AGENCY)
            url.startsWith("/api/v1/delivers") -> setOf(UserRole.DRIVER)
            else -> emptySet()
        }

        return domainRoles + DEFAULT_ROLE
    }

    private fun describe(url: String): String {
        return when {
            url.startsWith("/api/v1/delivers/me") -> "배송기사 프로필 관리 API"
            url.startsWith("/api/v1/delivers") -> "배송기사 API"
            url.startsWith("/api/v1/agencies/me") -> "대리점 프로필 관리 API"
            url.startsWith("/api/v1/agencies") -> "대리점 API"
            url.startsWith("/api/v1/vendors/me/products") -> "화주 배송 품목 관리 API"
            url.startsWith("/api/v1/vendors/me") -> "화주 프로필 관리 API"
            url.startsWith("/api/v1/vendors") -> "화주 API"
            else -> "보호 API 권한 관리 URL"
        }
    }

    private companion object {
        private const val API_PREFIX = "/api/v1/"
        private const val PUBLIC_AUTH_PREFIX = "/api/v1/auth/"
        private val DEFAULT_ROLE = UserRole.ADMIN
    }
}
