package io.foreshore.redy

import com.obvioustraverse.miska.support.spring.application.assertSpringProfile
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.ApplicationPidFileWriter
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@SpringBootApplication
class Application

@Suppress("SpreadOperator") // 의도된 것.
fun main(args: Array<String>) {
    runApplication<Application>(*args) {
        assertSpringProfile()
        addListeners(ApplicationPidFileWriter())
    }
}

@Configuration
@Profile("local")
@ComponentScan(lazyInit = true)
class LocalProfile // lazyInit overriding. spring bean 이 많지 않을때는 효과가 미미하다.
