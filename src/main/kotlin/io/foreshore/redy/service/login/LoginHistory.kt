package io.foreshore.redy.service.login

import com.obvioustraverse.miska.lang.ESuccess
import com.obvioustraverse.miska.support.javax.persistence.ESuccessConverter
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*
@Entity
@Table(name = "login_history")
class LoginHistory {
    @Id
    @Column(name = "history_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqLoginHistoryId")
    @SequenceGenerator(name = "seqLoginHistoryId", sequenceName = "SEQ_LOGIN_HISTORY_ID", allocationSize = 1)
    var historyId: Long? = null

    @Column(name = "user_name", length = 50, nullable = false)
    var username: String = ""

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    var loginDate: LocalDateTime? = null

    @Column(length = 1, nullable = false)
    @Convert(converter = ESuccessConverter::class)
    var success: ESuccess = ESuccess.YES

    @Column(length = 255)
    var clientAddress: String? = null

    @Column(length = 1024)
    var message: String? = null
}
