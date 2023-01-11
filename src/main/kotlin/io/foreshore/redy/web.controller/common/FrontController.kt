package io.foreshore.redy.web.controller.common

import com.obvioustraverse.miska.support.logger.MiskaLog
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal

@Controller
class FrontController {
    companion object : MiskaLog()

    @Suppress("FunctionOnlyReturningConstant")
    @GetMapping("/home")
    fun home(@AuthenticationPrincipal principal: Principal): String {
        logger.debug("Home Controller : login success - ${principal.name}")
        return "home"
    }

    /* vue router 가 서비스 하는 url 은 여기 모두 등록 해서 index 로 보내야 한다. */
    @Suppress("FunctionOnlyReturningConstant")
    @GetMapping(value = ["/", "/login", "/settings/**", "/credit"])
    fun root(): String {
        return "index"
    }
}
