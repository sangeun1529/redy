package io.foreshore.redy.service.login

import com.obvioustraverse.miska.lang.ESuccess
import com.obvioustraverse.miska.support.logger.MiskaLog
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPADeleteClause
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import javax.annotation.PostConstruct
import javax.persistence.EntityManager
@Service
@Transactional(readOnly = true)
class LoginHistoryService(
    @Autowired private val loginHistoryRepository: LoginHistoryRepository,
    @Autowired private val entityManager: EntityManager
) {
    @Value("\${app.period.delete.loginHistory}")
    var days: Long = 90 //NOSONAR 경험치에 의한 의도.

    @PostConstruct
    fun init() {
        logger.info("login histories before $days days will be deleted automatically.")
    }

    fun findAllBySearch(username: String?, from: LocalDate?, to: LocalDate?, pageable: Pageable) =
        if (username.isNullOrBlank() && from == null && to == null) {
            loginHistoryRepository.findAll(pageable).map(::LoginHistoryDto)
        } else {
            var expr = Expressions.asBoolean(true).isTrue
            if (!username.isNullOrBlank()) expr = expr.and(QLoginHistory.loginHistory.username.contains(username))
            if (from != null) expr = expr.and(QLoginHistory.loginHistory.loginDate.after(LocalDateTime.from(from.atStartOfDay())))
            if (to != null) expr = expr.and(QLoginHistory.loginHistory.loginDate.before(LocalDateTime.from(to.plusDays(1).atStartOfDay())))
            loginHistoryRepository.findAll(expr, pageable).map(::LoginHistoryDto)
        }

    @Transactional(readOnly = false)
    fun loginSuccess(username: String, clientAddress: String?) = LoginHistory().apply {
        this.username = username
        this.clientAddress = clientAddress
        this.success = ESuccess.YES
    }.let {
        loginHistoryRepository.saveAndFlush(it)
    }

    @Transactional(readOnly = false)
    fun loginFailure(username: String, clientAddress: String?, message: String?) = LoginHistory().apply {
        this.username = username
        this.clientAddress = clientAddress
        this.success = ESuccess.NO
        this.message = message
    }.let {
        loginHistoryRepository.saveAndFlush(it)
    }

    @Scheduled(cron = "\${app.period.delete.schedule.cron}")
    @Transactional(readOnly = false)
    fun deleteOldHistory() {
        val clause = JPADeleteClause(entityManager, QLoginHistory.loginHistory)
        val rows = clause.where(QLoginHistory.loginHistory.loginDate.before(LocalDateTime.now().minusDays(days))).execute()
        logger.info("login history deleting schedule executed : $rows deleted.")
    }

    companion object : MiskaLog()
}

interface LoginHistoryRepository : JpaRepository<LoginHistory, Long>, QuerydslPredicateExecutor<LoginHistory>
