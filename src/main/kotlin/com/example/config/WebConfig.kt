package com.example.config

import com.example.auth.AuthService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class WebConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, authService: AuthService): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/public/**").permitAll()
                    .anyRequest().authenticated() // All others require auth
            }
            .oauth2ResourceServer { oauth2ResourceServer: OAuth2ResourceServerConfigurer<HttpSecurity?> ->
                oauth2ResourceServer.opaqueToken {
                    it.introspector(authService)
                }
            }

        return http.build()
    }

}