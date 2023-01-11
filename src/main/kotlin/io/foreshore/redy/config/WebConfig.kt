package io.foreshore.redy.config

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.obvioustraverse.miska.support.logger.MiskaLog
import com.obvioustraverse.miska.support.spring.context.get
import com.obvioustraverse.miska.support.spring.web.BinaryView
import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.web.servlet.view.BeanNameViewResolver
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
class ServerAppConfig {
    companion object : MiskaLog() {
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val TIME_FORMAT = "HH:mm:ss"
        const val DATE_TIME_FORMAT = "$DATE_FORMAT $TIME_FORMAT"
    }

    @Autowired lateinit var message: MessageSource

    @PostConstruct
    fun onInit() {
        logger.info("Starting App ${message.get("common.app.name")} ...")
    }

    @PreDestroy
    fun onDestroy() {
        logger.info("shutdown..")
    }

    /** json time formatter.  */
    @Bean
    fun jsonCustomizer(): Jackson2ObjectMapperBuilderCustomizer = Jackson2ObjectMapperBuilderCustomizer {
        it.simpleDateFormat(DATE_FORMAT)
            .serializers(
                LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
            )
            .deserializers(
                LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
            )
    }

    @Bean
    fun layoutDialect() = LayoutDialect()

    // session 정보를 이용하여 locale 을 결정한다.
    @Bean
    fun localeResolver() = SessionLocaleResolver()

    @Bean
    fun localeChangeInterceptor() = LocaleChangeInterceptor() // ?locale=en 등을 붙여 session 에 locale 을 지정할 수 있다.

    @Bean // Custom View 를 쓰기 위해.
    fun viewResolver() = BeanNameViewResolver().apply { order = 0 } // annotation 에 의한 order 는 적용되지 않는다.

    @Bean(name = [BinaryView.VIEW_NAME])
    fun binaryView() = BinaryView()

}

@Configuration
class ServerWebConfigurer : WebMvcConfigurer {

    // 기존의 resources/static 은 frontend 에서 빌드되어 들어가고, spring boot project 에서 별도로 서비스하는 리소스는 resources/asset, context '/asset' 으로 서비스 한다.
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/asset/**")
            .addResourceLocations("classpath:/asset/")
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
        resolvers.add(AuthenticationPrincipalArgumentResolver())
    }
}

/**
 * web security 의 공통 config 를 설정 한다.
 * httpSecurity 의 configure 의 경우 global 로 한번만 적용 되기 때문에 local 에서 공통으로 적용 되어야 하는 경우 해당 클래스의 configureHttpSecurity / configureWebSecurity 를 이용한다.
 */
@Configuration
class CommonSecurityConfig {

    @Bean
    fun securityContextRepository() = HttpSessionSecurityContextRepository()

    @Bean
    fun securityContextLogoutHandler() = SecurityContextLogoutHandler()

    companion object {
        const val URL_LOGIN = "/login"
        const val URL_AJAX_LOGIN = "/ajax/login"
        const val URL_AJAX_LOGOUT = "/ajax/logout"
        const val URL_LOGOUT = "/logout"
        const val URL_HOME = "/home"

        /**
         * 공통으로 추가되는 configure 설정
         */
        fun configureHttpSecurity(http: HttpSecurity) {
            http
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), AntPathRequestMatcher("/ajax/**"))
                .and()
                .authorizeRequests()
                .antMatchers(URL_AJAX_LOGOUT).permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutUrl(URL_LOGOUT)
                .deleteCookies("JSESSIONID")
                .and()
                .csrf().disable()
                .headers()
                .frameOptions().disable()
        }

        fun configureWebSecurity(web: WebSecurity) {
            web
                .ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                // .requestMatchers(PathRequest.toH2Console()) // H2 dependency 없는 profile 에서는 h2 관련 properties 가 없다는 에러를 낸다.
                .antMatchers(
                    "/img/**",
                    "/h2-console/**",
                    "/icons/**"
                ) // spring 의 atCommonLocations 와 vue 의 default image folder 명이 다르다.
        }
    }
}

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // Controller 에도 @PreAuthorize 가 적용되려면 있어야 한다.
class WebSecurityConfig : WebSecurityConfigurerAdapter() {
    @Bean
    @Throws(Exception::class)
    override fun authenticationManager(): AuthenticationManager? {
        return super.authenticationManager()
    }

    override fun configure(web: WebSecurity) {
        CommonSecurityConfig.configureWebSecurity(web)
    }

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers(CommonSecurityConfig.URL_LOGIN).permitAll()
            .antMatchers(CommonSecurityConfig.URL_AJAX_LOGIN).permitAll()
            .and()
            .formLogin()
            .loginPage(CommonSecurityConfig.URL_LOGIN)
            .defaultSuccessUrl(CommonSecurityConfig.URL_HOME, true)
            .and()
            .logout()
            .logoutSuccessUrl(CommonSecurityConfig.URL_LOGIN)

        // 공통 configure
        CommonSecurityConfig.configureHttpSecurity(http)
    }
}
