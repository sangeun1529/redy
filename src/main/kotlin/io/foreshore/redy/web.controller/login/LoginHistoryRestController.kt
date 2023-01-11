package io.foreshore.redy.web.controller.login

import com.obvioustraverse.miska.util.api.ApiReturn
import io.foreshore.redy.service.login.LoginHistoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.Size

@RestController
@RequestMapping("/ajax")
class LoginHistoryRestController(@Autowired val loginHistoryService: LoginHistoryService) {
    @GetMapping("/loginHistory")
    fun list(
        @Valid @Size(max = 50) @RequestParam("search") search: String?,
        @RequestParam("from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") from: LocalDate?,
        @RequestParam("to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") to: LocalDate?,
        pageable: Pageable
    ) = ApiReturn.of(loginHistoryService.findAllBySearch(search, from, to, pageable))
}
