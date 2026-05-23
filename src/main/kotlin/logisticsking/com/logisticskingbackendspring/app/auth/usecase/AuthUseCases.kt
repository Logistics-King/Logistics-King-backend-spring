package logisticsking.com.logisticskingbackendspring.app.auth.usecase

import logisticsking.com.logisticskingbackendspring.app.auth.command.LoginCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.LogoutCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RefreshTokenCommand
import logisticsking.com.logisticskingbackendspring.app.auth.result.LoginResult
import logisticsking.com.logisticskingbackendspring.app.auth.result.LogoutResult
import logisticsking.com.logisticskingbackendspring.app.auth.result.RefreshTokenResult

interface LoginUseCase {
    fun login(command: LoginCommand): LoginResult
}

interface RefreshTokenUseCase {
    fun refresh(command: RefreshTokenCommand): RefreshTokenResult
}

interface LogoutUseCase {
    fun logout(command: LogoutCommand): LogoutResult
}
