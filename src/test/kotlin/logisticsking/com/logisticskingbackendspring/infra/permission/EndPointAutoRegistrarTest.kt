package logisticsking.com.logisticskingbackendspring.infra.permission

import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPoint
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPointRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.security.EndPointAuthorizationCache
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

class EndPointAutoRegistrarTest {

    @Test
    fun `기존 endpoint roles는 annotation roles로 덮어쓰지 않는다`() {
        val repository = MutableEndPointRepository(
            listOf(
                EndPoint.restore(
                    id = 1,
                    url = "/api/v1/test",
                    method = RequestMethod.GET.name,
                    roles = setOf(UserRole.AGENCY),
                    description = "운영에서 변경된 권한",
                )
            )
        )
        val handlerMapping = RequestMappingHandlerMapping()
        val controller = TestController()
        handlerMapping.registerMapping(
            RequestMappingInfo
                .paths("/api/v1/test")
                .methods(RequestMethod.GET)
                .build(),
            controller,
            TestController::class.java.getDeclaredMethod("get"),
        )
        val registrar = EndPointAutoRegistrar(
            requestMappingHandlerMapping = handlerMapping,
            endPointRepository = repository,
            endPointAuthorizationCache = EndPointAuthorizationCache(repository),
        )

        registrar.run(DefaultApplicationArguments())

        val saved = repository.findByUrlAndMethod("/api/v1/test", RequestMethod.GET.name)

        assertEquals(setOf(UserRole.AGENCY), saved?.roles)
    }

    private class TestController {

        @EndpointAccess(roles = [UserRole.VENDOR])
        @GetMapping("/api/v1/test")
        fun get() {
        }
    }

    private class MutableEndPointRepository(
        private var endPoints: List<EndPoint>,
    ) : EndPointRepository {

        override fun findAll(): List<EndPoint> {
            return endPoints
        }

        override fun findByUrlAndMethod(url: String, method: String): EndPoint? {
            return endPoints.firstOrNull { endPoint -> endPoint.url == url && endPoint.method == method }
        }

        override fun save(endPoint: EndPoint): EndPoint {
            endPoints = endPoints
                .filterNot { current -> current.url == endPoint.url && current.method == endPoint.method } + endPoint

            return endPoint
        }
    }
}
