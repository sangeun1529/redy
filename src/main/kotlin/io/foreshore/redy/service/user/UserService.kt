package io.foreshore.redy.service.user

import com.obvioustraverse.miska.lang.EEnabled
import com.obvioustraverse.miska.util.nullable
import com.querydsl.jpa.impl.JPAQueryFactory
import io.foreshore.redy.service.user.responseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames = ["userCache"])
class UserService(
    @Autowired val userRepository: UserRepository,
    @Autowired val roleGroupRepository: RoleGroupRepository,
    @Autowired val query: JPAQueryFactory,
    @Autowired val passwordEncoder: PasswordEncoder
) {
    @Cacheable(key = "{#root.targetClass.name, #root.methodName}")
    fun findAllEnabledUsers() = query.selectFrom(QUser.user).where(QUser.user.enabled.eq(EEnabled.YES)).fetch().map { it.responseDto }

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #search, #pageable}")
    fun findAll(search: String?, pageable: Pageable) = search?.let {
        userRepository.findAll(
            QUser.user.id.containsIgnoreCase(search).or(QUser.user.displayName.containsIgnoreCase(search)), pageable
        ).map { it.responseDto }
    } ?: userRepository.findAll(pageable).map { it.responseDto }

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #id}")
    fun findByIdOrNull(id: String) = userRepository.findById(id).nullable()

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #id}")
    fun findByIdOrElseThrow(id: String): User = userRepository.findById(id).orElseThrow()

    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true)
    fun create(request: UserRequestDto) = userRepository.saveAndFlush(request.entity).dto

    /* admin 이 다른 사용자 혹은 자신의 account 를 수정 */
    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true)
    fun update(request: UserRequestDto) = with(userRepository) {
        findByIdOrElseThrow(request.username).updateBy(request).let { saveAndFlush(it).dto }
    }

    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true)
    fun save(user: User) = userRepository.save(user)

    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true)
    fun updateMe(request: UserRequestDto, me: User) = with(userRepository) {
        findById(me.username).orElseThrow().apply {
            // 자기 자신의 항목중 변경 가능한 것은 별로 없다.
            displayName = request.displayName
            password = if (request.password.isNullOrBlank()) me.password else passwordEncoder.encode(request.password)
        }.let { saveAndFlush(it) }.dto
    }

    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true)
    fun delete(id: String) = userRepository.deleteById(id)

    private val UserRequestDto.entity get() = User(username).updateBy(this) // username 은 생성시에만 setting 가능하고 update 는 불가하다.
    private val User.dto get() = UserResponseDto(this)
    private fun User.updateBy(request: UserRequestDto) = this.apply {
        displayName = request.displayName
        if (!request.password.isNullOrBlank()) password = passwordEncoder.encode(request.password)
        enabled = request.enabled
        roleGroup = roleGroupRepository.findById(request.roleGroupId).orElseThrow()
        loginFailureCount = request.loginFailureCount
    }
}

interface UserRepository : JpaRepository<User, String>, QuerydslPredicateExecutor<User>
