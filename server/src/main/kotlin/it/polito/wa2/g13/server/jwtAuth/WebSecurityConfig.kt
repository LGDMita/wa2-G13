package it.polito.wa2.g13.server.jwtAuth

import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class WebSecurityConfig(private val jwtAuthConverter: JwtAuthConverter) {

    companion object {
        const val CLIENT = "client"
        const val EXPERT = "expert"
        const val MANAGER = "manager"
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable()
        http.authorizeHttpRequests().anyRequest().permitAll()
        http.oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtAuthConverter)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        return http.build()
    }
}


/*
http.authorizeHttpRequests()
.requestMatchers(HttpMethod.GET, "/API/tickets/*").hasAnyRole(MANAGER, EXPERT, CLIENT)
.requestMatchers(HttpMethod.GET, "/API/experts/*").hasAnyRole(MANAGER, EXPERT)
*/