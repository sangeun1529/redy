package io.foreshore.redy.service.code

import com.obvioustraverse.miska.lang.EEnabled
import com.obvioustraverse.miska.util.nullable
import com.querydsl.jpa.impl.JPAQueryFactory
import io.foreshore.redy.service.code.dto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames = ["commonCodeCache"])
class CommonCodeService(
    @Autowired private val commonCodeRepository: CommonCodeRepository,
    @Autowired private val query: JPAQueryFactory
) {

    @Cacheable(key = "{#root.targetClass.name, #root.methodName}")
    fun findAllEnabledGroups() = query.selectFrom(QCommonCode.commonCode)
        .where(QCommonCode.commonCode.parent.isNull.and(QCommonCode.commonCode.enabled.eq(EEnabled.YES))).fetch().map { it.dto }.toList()

    @Cacheable(key = "{#root.targetClass.name, #root.methodName}")
    fun findAllGroups(): List<CommonCodeDto> = query.selectFrom(QCommonCode.commonCode)
        .where(QCommonCode.commonCode.parent.isNull).fetch().map { it.dto }.toList()

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #parent}")
    fun findAllEnabledByParentCode(parent: String) = query.selectFrom(QCommonCode.commonCode)
        .where(QCommonCode.commonCode.parent.code.eq(parent).and(QCommonCode.commonCode.enabled.eq(EEnabled.YES))).fetch().map { it.dto }.toList()

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #parent}")
    fun findAllByParentCode(parent: String) = query.selectFrom(QCommonCode.commonCode)
        .where(QCommonCode.commonCode.parent.code.eq(parent)).fetch().map { it.dto }.toList()

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #key}")
    fun findByIdOrNull(key: String) = commonCodeRepository.findById(key).nullable()
    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #key}")
    fun findByIdOrElseThrow(key: String): CommonCode = commonCodeRepository.findById(key).orElseThrow()

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #search, #pageable}")
    fun findAll(search: String?, pageable: Pageable): Page<CommonCodeDto> =
        search?.let {
            val expression = QCommonCode.commonCode.name.containsIgnoreCase(search)
                .or(QCommonCode.commonCode.description.containsIgnoreCase(search)
                .or(QCommonCode.commonCode.code.containsIgnoreCase(search)))
            commonCodeRepository.findAll(expression, pageable).map { it.dto }
        } ?: commonCodeRepository.findAll(pageable).map { it.dto }
    @CacheEvict(allEntries = true)
    @Transactional(readOnly = false)
    fun create(request: CommonCodeDto) = commonCodeRepository.saveAndFlush(
        request.newEntityWithoutParent().apply {
            request.parentCode?.let {
                parent = findByIdOrElseThrow(request.parentCode)
            }
        }
    )

    @CacheEvict(cacheNames = ["commonCodeCache"], allEntries = true)
    @Transactional(readOnly = false)
    fun update(code: String, request: CommonCodeDto) =
        findByIdOrElseThrow(code).updateBy(request).let {
            commonCodeRepository.saveAndFlush(it)
        }

    @CacheEvict(cacheNames = ["commonCodeCache"], allEntries = true)
    @Transactional(readOnly = false)
    fun deleteById(code: String) = commonCodeRepository.deleteById(code)
}

interface CommonCodeRepository : JpaRepository<CommonCode, String>, QuerydslPredicateExecutor<CommonCode>
