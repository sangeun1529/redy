package io.foreshore.redy.web.controller.support

import io.foreshore.redy.service.user.User
import org.springframework.core.MethodParameter
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val isAuthAnnotation = parameter.getParameterAnnotation(AuthenticationPrincipal::class.java) != null
        val isUserClass = User::class.java == parameter.parameterType
        return isAuthAnnotation && isUserClass
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ) = when (SecurityContextHolder.getContext().authentication.principal) {
        is User -> SecurityContextHolder.getContext().authentication.principal
        else -> null
    }
}
