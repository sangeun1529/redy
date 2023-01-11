package io.foreshore.redy.web.controller.user

import com.obvioustraverse.miska.util.api.ApiReturn
import io.foreshore.redy.service.user.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/ajax")
class MeRestController(@Autowired private val userService: UserService) {
    @GetMapping("/me")
    fun me(@AuthenticationPrincipal user: User) = ApiReturn.of(mapOf("user" to UserResponseDto(user), "roleGroup" to RoleGroupDto(user.roleGroup)))

    @PutMapping("/me")
    fun updateMe(@AuthenticationPrincipal user: User, @Valid @RequestBody request: UserRequestDto) = ApiReturn.of(userService.updateMe(request, user))
}
