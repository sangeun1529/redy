package io.foreshore.redy.service.user

import com.obvioustraverse.miska.lang.EEnabled
import java.time.LocalDateTime
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
data class UserRequestDto(
    @field:NotNull
    @field:NotEmpty
    @field:Size(min = 4, max = 50)
    var username: String,
    @field:NotNull
    @field:NotEmpty
    @field:Size(min = 1, max = 120)
    val displayName: String,
    @field:NotNull
    val roleGroupId: Long,
    val enabled: EEnabled = EEnabled.YES
) {
    var password: String? = null
    var loginFailureCount: Int = 0
}

@Suppress("MemberVisibilityCanBePrivate")
data class UserResponseDto(
    var username: String,
    var displayName: String,
    var enabled: EEnabled = EEnabled.YES
) {
    var roleGroupId: Long = -1
    var roleGroupName: String = ""
    var dtCreate: LocalDateTime? = null
    var dtUpdate: LocalDateTime? = null
    var dtLastLogin: LocalDateTime? = null
    var loginFailureCount: Int = 0

    constructor(user: User) : this(user.username, user.displayName, user.enabled) {
        roleGroupId = user.roleGroup.roleGroupId ?: -1
        roleGroupName = user.roleGroup.roleGroupName
        dtCreate = user.dtCreate
        dtUpdate = user.dtUpdate
        dtLastLogin = user.dtLastLogin
        loginFailureCount = user.loginFailureCount
    }
}

val User.responseDto get() = UserResponseDto(this)
