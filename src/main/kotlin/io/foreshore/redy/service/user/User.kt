package io.foreshore.redy.service.user

import com.obvioustraverse.miska.lang.EEnabled
import com.obvioustraverse.miska.support.javax.persistence.EEnabledConverter
import io.foreshore.redy.support.ManuallyAssignedIdEntity
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import javax.persistence.*
@Entity
@Table(name = "admin_user") // table name user 는 Postgres 에서 reserved.
// table name user 는 Postgres 에서 reserved.
class User() : ManuallyAssignedIdEntity<String>(), UserDetails {
    @Id
    @Column(length = 50, name = "user_name")
    private lateinit var _username: String

    @Column(length = 120, nullable = false)
    var displayName: String = ""

    @Column(length = 120, name = "user_password", nullable = false)
    private lateinit var _password: String

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    val dtCreate: LocalDateTime? = null

    @UpdateTimestamp
    val dtUpdate: LocalDateTime? = null

    var dtLastLogin: LocalDateTime? = null

    @Column(name = "login_failure_count")
    var loginFailureCount: Int = 0

    @Column(length = 1, name = "user_enable", nullable = false)
    @Convert(converter = EEnabledConverter::class)
    var enabled: EEnabled = EEnabled.YES

    @ManyToOne
    @JoinColumn(name = "role_group_id", referencedColumnName = "role_group_id")
    lateinit var roleGroup: RoleGroup

    @Transient
    private var _authorities: Collection<GrantedAuthority>? = null

    override fun getId(): String? = username

    @Transient
    override fun getAuthorities(): Collection<GrantedAuthority> {
        synchronized(this) {
            if (_authorities == null) {
                _authorities = roleGroup.userRoles.map { SimpleGrantedAuthority(it.roleName) }.toSet()
            }
            return _authorities!!
        }
    }

    override fun getPassword(): String = _password
    fun setPassword(password: String) {
        _password = password
    }

    override fun getUsername(): String = _username
    fun setUsername(username: String) {
        _username = username
    }

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    @Transient
    override fun isEnabled(): Boolean = (enabled == EEnabled.YES)

    constructor(username: String) : this() {
        _username = username
    }
}
