package io.foreshore.redy.service.login

import com.obvioustraverse.miska.lang.ESuccess
import io.foreshore.redy.service.login.LoginHistory
import java.time.LocalDateTime
data class LoginHistoryDto(
    val historyId: Long,
    val username: String,
    val loginDate: LocalDateTime?,
    val success: ESuccess = ESuccess.YES
) {
    var clientAddress: String? = null
    var message: String? = null

    constructor(history: LoginHistory) : this(
        history.historyId ?: -1, history.username, history.loginDate, history.success
    ) {
        clientAddress = history.clientAddress
        message = history.message
    }
}
