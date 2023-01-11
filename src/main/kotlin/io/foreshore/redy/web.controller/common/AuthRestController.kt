package io.foreshore.redy.web.controller.common

import com.obvioustraverse.miska.support.logger.MiskaLog
import com.obvioustraverse.miska.util.api.ApiReturn
import io.foreshore.redy.service.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import javax.validation.constraints.Size

data class LoginRequestDto(@Size(min = 4) val username: String, @Size(min = 4) val password: String)

@RestController
@RequestMapping("/ajax")
class AuthRestController(
    @Autowired private val authenticationManager: AuthenticationManager,
    @Autowired private val repository: SecurityContextRepository,
    @Autowired private val securityContextLogoutHandler: SecurityContextLogoutHandler,
) {
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequestDto,
        servletRequest: HttpServletRequest,
        servletResponse: HttpServletResponse
    ): ApiReturn<Unit> {
        val token = UsernamePasswordAuthenticationToken(request.username, request.password)
        val auth = authenticationManager.authenticate(token)
        SecurityContextHolder.getContext().authentication = auth
        repository.saveContext(SecurityContextHolder.getContext(), servletRequest, servletResponse)

        return ApiReturn.OK
    }

    @GetMapping("/logout")
    fun logout(
        @AuthenticationPrincipal user: User?,
        servletRequest: HttpServletRequest,
        servletResponse: HttpServletResponse
    ): ApiReturn<Unit> {
        user?.let {
            securityContextLogoutHandler.logout(servletRequest, servletResponse, SecurityContextHolder.getContext().authentication)
        }
        return ApiReturn.OK
    }

    companion object : MiskaLog()
}
