package io.foreshore.redy.service.user

import io.foreshore.redy.support.ManuallyAssignedIdEntity
import java.io.Serializable
import javax.persistence.*
@Entity
@Table(name = "role_group")
class RoleGroup : Serializable {
    @Id
    @Column(name = "role_group_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqRoleGroupId")
    @SequenceGenerator(name = "seqRoleGroupId", sequenceName = "SEQ_ROLE_GROUP_ID", allocationSize = 1)
    var roleGroupId: Long? = null

    @Column(length = 50)
    var roleGroupName: String = ""

    @Column(length = 2048)
    var roleGroupDescription: String? = ""

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "group_roles",
        joinColumns = [JoinColumn(name = "role_group_id")],
        inverseJoinColumns = [JoinColumn(name = "role_name")]
    )
    val userRoles: MutableSet<UserRole> = HashSet()
    companion object {
        private const val serialVersionUID: Long = 1
    }
}

@Entity
@Table(name = "user_role")
class UserRole(
    @Id
    @Column(length = 50)
    var roleName: String = ""
) : ManuallyAssignedIdEntity<String>() {
    @Column(length = 2048)
    var roleDescription: String = ""
    override fun getId(): String? = roleName
}
