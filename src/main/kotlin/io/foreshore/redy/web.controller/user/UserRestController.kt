package io.foreshore.redy.controller.user

import com.obvioustraverse.miska.util.api.ApiReturn
import io.foreshore.redy.service.user.UserRequestDto
import io.foreshore.redy.service.user.UserResponseDto
import io.foreshore.redy.service.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.ValidationException
import javax.validation.constraints.Size
@RestController
@RequestMapping("/ajax/user")
@PreAuthorize("hasAuthority('SYS_ACCOUNT')")
class UserRestController(@Autowired private val userService: UserService) {
    @GetMapping("/")
    fun list(@Valid @Size(max = 200) @RequestParam("search") search: String?, pageable: Pageable) =
        ApiReturn.of(userService.findAll(search, pageable))

    @GetMapping("/{username}")
    fun get(@PathVariable("username") username: String) = ApiReturn.of(UserResponseDto(userService.findByIdOrElseThrow(username)))

    @PostMapping("/")
    fun create(@RequestBody @Valid request: UserRequestDto): ApiReturn<UserResponseDto> {
        if (request.password.isNullOrBlank()) throw ValidationException("password is null")
        return ApiReturn.of(userService.create(request))
    }

    @PutMapping("/{username}")
    fun update(@PathVariable("username") username: String, @RequestBody @Valid request: UserRequestDto) =
        ApiReturn.of(userService.update(request.apply { this.username = username }))

    @DeleteMapping("/{username}")
    fun delete(@PathVariable("username") username: String) = username.run { userService.delete(username); ApiReturn.OK }
}
