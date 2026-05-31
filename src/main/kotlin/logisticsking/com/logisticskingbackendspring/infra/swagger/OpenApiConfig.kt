package logisticsking.com.logisticskingbackendspring.infra.swagger

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "택배왕 API",
        version = "v1",
        description = "택배왕 백엔드 API 문서",
    ),
)
@SecurityScheme(
    name = "accessTokenCookie",
    type = SecuritySchemeType.APIKEY,
    `in` = SecuritySchemeIn.COOKIE,
    paramName = "accessToken",
)
@SecurityScheme(
    name = "refreshTokenCookie",
    type = SecuritySchemeType.APIKEY,
    `in` = SecuritySchemeIn.COOKIE,
    paramName = "refreshToken",
)
class OpenApiConfig
