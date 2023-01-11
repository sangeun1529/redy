@file:Suppress("unused", "unused_parameter") // do in AOP
package io.foreshore.redy.web.controller.support

import com.obvioustraverse.miska.support.logger.MiskaLog
import com.obvioustraverse.miska.util.api.ApiReturn
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.util.*
import javax.validation.ConstraintViolationException
import javax.validation.ValidationException

@ControllerAdvice
@RestController
class RestExceptionResolver {
    companion object {
        const val VALIDATION_ERROR = "Validation Error"
        const val ERROR_CODE_DUPLICATED = 9100
        const val ERROR_CODE_BAD_CREDENTIAL = 9900
    }

    @ExceptionHandler(value = [Exception::class]) // General Exception.
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(exception: Exception) = ApiReturn.errorOf(exception)

    @ExceptionHandler(value = [NoSuchElementException::class, EmptyResultDataAccessException::class]) // 못찾은 경우.
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoSuchElementException(exception: Exception) = Unit // do in AOP

    @ExceptionHandler(value = [DataIntegrityViolationException::class, ConstraintViolationException::class]) // PK 중복 등.
    fun handleDataIntegrityViolationException(exception: Exception) = ApiReturn.errorOf(
        ERROR_CODE_DUPLICATED, exception::class.simpleName
            ?: "", exception.message
    )

    @ExceptionHandler(value = [BindException::class, MethodArgumentNotValidException::class, HttpMessageNotReadableException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBindException(exception: Exception): ApiReturn<Unit> = when (exception) {
        is MethodArgumentNotValidException -> resolveBindError(exception, exception.bindingResult)
        is BindException -> resolveBindError(exception, exception.bindingResult)
        else -> ApiReturn.errorOf(exception)
    }

    private fun resolveBindError(
        @Suppress("UNUSED_PARAMETER") exception: Exception,
        bindingResult: BindingResult
    ): ApiReturn<Unit> { // NOSONAR used in AOP
        val str = bindingResult.allErrors.joinToString { (it as FieldError).field + " : " + it.defaultMessage + ", " }
        return ApiReturn.errorOf(HttpStatus.BAD_REQUEST.value(), VALIDATION_ERROR, str)
    }

    @ExceptionHandler(value = [BadCredentialsException::class]) // 기존 패스워드 불일치.
    fun handleBadCredentialsException(exception: BadCredentialsException) = ApiReturn.errorOf(ERROR_CODE_BAD_CREDENTIAL, "bad credential", exception.message)

    @ExceptionHandler(value = [MethodArgumentTypeMismatchException::class, ValidationException::class]) // Request Body validation error.
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatchException(exception: Exception) =
        ApiReturn.errorOf(HttpStatus.BAD_REQUEST.value(), VALIDATION_ERROR, exception.message)

    @ExceptionHandler(value = [AuthenticationException::class]) // 로그인, 인증 실패.
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnAuthorized(exception: AuthenticationException) = Unit // do in AOP

    @ExceptionHandler(value = [AccessDeniedException::class]) // 접근 권한 없음.
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDenied(exception: AccessDeniedException) = Unit // do in AOP
}

@Component
@Aspect
class ExceptionResolverAspect {
    companion object : MiskaLog()

    @Before("execution(* io.foreshore.redy.web.controller.support.RestExceptionResolver.*(*))")
    fun exceptionLogging(jp: JoinPoint) {
        val ex = jp.args.find { it is Exception } as Exception
        logger.error(ex.javaClass.simpleName + " [" + ex.message + "]", ex)
    }
}
