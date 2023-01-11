package io.foreshore.redy.web.controller.support

import com.obvioustraverse.miska.support.logger.MiskaLog
import com.obvioustraverse.miska.support.spring.servlet.ServletSupport
import io.foreshore.redy.service.login.LoginHistoryService
import io.foreshore.redy.service.user.User
import io.foreshore.redy.service.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
@Component
class AuthenticationListener {
    @Autowired
    private lateinit var httpRequest: HttpServletRequest

    @Autowired
    private lateinit var loginHistoryService: LoginHistoryService

    @Autowired
    private lateinit var userService: UserService

    @EventListener(AuthenticationSuccessEvent::class)
    fun onSuccess(success: AuthenticationSuccessEvent) {
        val address = ServletSupport.getRemoteAddress(httpRequest)
        lateinit var username: String
        when (success.authentication.principal) {
            is User -> {
                val user = success.authentication.principal as User
                username = user.username
                logger.info("login success(local) - address : $address, username : ${user.username}")
            }
        }
        userService.findByIdOrNull(username)?.apply {
            dtLastLogin = LocalDateTime.now()
            loginFailureCount = 0
        }?.let {
            userService.save(it)
        }
        loginHistoryService.loginSuccess(username, address)
    }

    @EventListener(AuthenticationFailureBadCredentialsEvent::class)
    fun onFailure(failure: AuthenticationFailureBadCredentialsEvent) {
        val username = failure.authentication.principal as String
        val address = ServletSupport.getRemoteAddress(httpRequest)
        userService.findByIdOrNull(username)?.apply { loginFailureCount++ }?.let { userService.save(it) } // for cache evict
        logger.info("login failure(Bad Credential) - address : $address, username : $username")
        loginHistoryService.loginFailure(username, address, failure.exception.message)
    }

    companion object : MiskaLog()
}
