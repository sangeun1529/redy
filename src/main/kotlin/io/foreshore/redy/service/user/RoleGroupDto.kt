package io.foreshore.redy.service.user

import javax.validation.constraints.Size
data class RoleGroupDto(
    var roleGroupId: Long?,
    @Size(min = 1, max = 50) val roleGroupName: String,
    @Size(max = 2048) val roleGroupDescription: String?,
    val userRoles: Set<String>
) {
    constructor(roleGroup: RoleGroup) : this(roleGroup.roleGroupId, roleGroup.roleGroupName, roleGroup.roleGroupDescription,
        roleGroup.userRoles.map { it.roleName }.toSet()
    )
}

data class UserRoleDto(val roleName: String, val roleDescription: String) {
    constructor(userRole: UserRole) : this(userRole.roleName, userRole.roleDescription)
}
