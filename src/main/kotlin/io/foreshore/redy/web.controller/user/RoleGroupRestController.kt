package io.foreshore.redy.web.controller.user

import com.obvioustraverse.miska.util.api.ApiReturn
import io.foreshore.redy.service.user.RoleGroupDto
import io.foreshore.redy.service.user.RoleGroupService
import io.foreshore.redy.service.user.UserRoleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/ajax/roleGroup")
class RoleGroupRestController(
    @Autowired private val roleGroupService: RoleGroupService,
    @Autowired private val userRoleService: UserRoleService
) {

    @GetMapping("/")
    fun list() = ApiReturn.of(roleGroupService.findAll())

    @PreAuthorize("hasAuthority('SYS_ROLE')")
    @GetMapping("/{roleGroupId}")
    fun get(@PathVariable("roleGroupId") id: Long) = ApiReturn.of(RoleGroupDto(roleGroupService.findByIdOrElseThrow(id)))

    @PreAuthorize("hasAuthority('SYS_ROLE')")
    @PostMapping("/")
    fun create(@RequestBody @Valid request: RoleGroupDto) = ApiReturn.of(roleGroupService.create(request))

    @PreAuthorize("hasAuthority('SYS_ROLE')")
    @PutMapping("/{roleGroupId}")
    fun update(@PathVariable("roleGroupId") id: Long, @RequestBody @Valid request: RoleGroupDto) =
        ApiReturn.of(roleGroupService.update(request.apply { this.roleGroupId = id }))

    @PreAuthorize("hasAuthority('SYS_ROLE')")
    @DeleteMapping("/{roleGroupId}")
    fun delete(@PathVariable("roleGroupId") id: Long) = id.run { roleGroupService.delete(this); ApiReturn.OK }

    @PreAuthorize("hasAuthority('SYS_ROLE')")
    @GetMapping("/listRoles")
    fun listRoles() = ApiReturn.of(userRoleService.findAll())
}
