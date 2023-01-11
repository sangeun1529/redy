package io.foreshore.redy.service.user

import com.obvioustraverse.miska.util.nullable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames = ["userCache"])
class RoleGroupService(
    @Autowired private val roleGroupRepository: RoleGroupRepository,
    @Autowired private val userRoleService: UserRoleService
) {
    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true)
    fun create(request: RoleGroupDto) = roleGroupRepository.saveAndFlush(request.entity).dto

    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true)
    fun update(request: RoleGroupDto) = with(roleGroupRepository) {
        request.roleGroupId?.let { id ->
            findById(id).orElseThrow().updateBy(request).let { saveAndFlush(it).dto }
        } ?: throw NoSuchElementException("role group id is null")
    }

    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true)
    fun delete(roleGroupId: Long): Unit = roleGroupRepository.deleteById(roleGroupId)

    @Cacheable(key = "{#root.targetClass.name, #root.methodName}")
    fun findAll() = roleGroupRepository.findAll().map { RoleGroupDto(it) }

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #id}")
    fun findByIdOrNull(id: Long) = roleGroupRepository.findById(id).nullable()

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #id}")
    fun findByIdOrElseThrow(id: Long): RoleGroup = roleGroupRepository.findById(id).orElseThrow()

    private val RoleGroupDto.entity get() = RoleGroup().updateBy(this)
    private val RoleGroup.dto get() = RoleGroupDto(this)
    private fun RoleGroup.updateBy(request: RoleGroupDto) = this.apply {
        roleGroupName = request.roleGroupName
        roleGroupDescription = request.roleGroupDescription
        userRoles.clear()
        userRoles.addAll(request.userRoles.map { userRoleService.findById(it).get() }.toSet())
    }
}

/** role 이 변경된다는 것은 반드시 소스의 수정이 동반된다.
 * DB 에 role 을 추가 한다고 동작되지 않으므로 evict 없는 caching 해도 상관 없다.
 */
@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames = ["userCache"])
class UserRoleService(@Autowired val userRoleRepository: UserRoleRepository) {

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #id}")
    fun findById(id: String) = userRoleRepository.findById(id)

    @Cacheable(key = "{#root.targetClass.name, #root.methodName}")
    fun findAll() = userRoleRepository.findAll().map { UserRoleDto(it) }
}

interface RoleGroupRepository : JpaRepository<RoleGroup, Long>, QuerydslPredicateExecutor<RoleGroup>
interface UserRoleRepository : JpaRepository<UserRole, String>, QuerydslPredicateExecutor<UserRole>
