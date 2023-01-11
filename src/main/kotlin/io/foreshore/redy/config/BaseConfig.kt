package io.foreshore.redy.config

import com.obvioustraverse.miska.io.Date2DepthIncrementSuffixRenamePolicy
import com.obvioustraverse.miska.io.FileSystemRepository
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import java.io.File
import javax.persistence.EntityManager

@Configuration
class BaseConfig {
    /**
     * security is important.
     */
    @Bean
    fun delegatingPasswordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun jpaQueryFactory(entityManager: EntityManager) = JPAQueryFactory(entityManager)

    @Bean
    fun fileSystemRepository(@Value("\${app.upload.root}") repositoryRoot: String) =
        FileSystemRepository(File(repositoryRoot).absolutePath, Date2DepthIncrementSuffixRenamePolicy)
}

@Configuration
@ConditionalOnExpression("'\${spring.cache.type:none}' != 'none'")
@EnableCaching // spring.cache.type=none 이면 캐시를 끄게되므로 이게 있어도 캐싱이 되지 않는다.
class CacheConfig

@Configuration
@ConditionalOnExpression("'\${app.scheduling.enabled}' != 'false'")
@EnableScheduling
class SchedulingConfig
