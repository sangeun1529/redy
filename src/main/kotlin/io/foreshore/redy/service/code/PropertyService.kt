package io.foreshore.redy.service.code

import com.obvioustraverse.miska.util.nullable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames = ["dbPropertyCache"])
class PropertyService(@Autowired val propertyRepository: PropertyRepository) {
    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true)
    fun create(request: PropertyDto) = propertyRepository.saveAndFlush(request.entity).dto

    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true)
    fun update(request: PropertyDto) = with(propertyRepository) {
        findById(request.key).orElseThrow().updateBy(request).let { saveAndFlush(it).dto }
    }

    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true)
    fun delete(key: String): Unit = propertyRepository.deleteById(key)

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #key}")
    fun findByIdOrNull(key: String) = propertyRepository.findById(key).nullable()

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #key}")
    fun findByIdOrElseThrow(key: String): DbProperty = propertyRepository.findById(key).orElseThrow()

    @Cacheable(key = "{#root.targetClass.name, #root.methodName, #search, #pageable}")
    fun findAll(search: String?, pageable: Pageable) = search?.let {
        propertyRepository.findAll(
            QDbProperty.dbProperty.key.containsIgnoreCase(search)
                .or(QDbProperty.dbProperty.value.containsIgnoreCase(search).or(QDbProperty.dbProperty.description.containsIgnoreCase(search))),
            pageable
        ).map { it.dto }
    } ?: propertyRepository.findAll(pageable).map { it.dto }
}

interface PropertyRepository : JpaRepository<DbProperty, String>, QuerydslPredicateExecutor<DbProperty>
