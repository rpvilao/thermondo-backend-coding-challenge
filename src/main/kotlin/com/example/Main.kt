package com.example

import com.example.auth.AuthService
import com.example.config.PopcornTimeConfig
import com.example.presentation.dto.MovieV1Dto
import com.example.presentation.dto.UserV1Dto
import com.example.service.AppUserService
import com.example.service.MovieService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment

@SpringBootApplication
@Import(PopcornTimeConfig::class)
class Main(
    private val authService: AuthService,
    private val appUserService: AppUserService,
    private val movieService: MovieService,
    private val environment: Environment
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        if (!environment.activeProfiles.contains("test")) {
            // Create test users and expose the tokens for testing.
            val (user, userToken) = appUserService.createAppUser(
                UserV1Dto(
                    username = "user@example.com",
                    name = "Regular User"
                )
            ).let { it to authService.createToken(it.username, AuthService.ROLE_USER) }
            val (admin, adminToken) = appUserService.createAppUser(
                UserV1Dto(
                    username = "admin@example.com",
                    name = "Admin User"
                )
            ).let { it to authService.createToken(it.username, AuthService.ROLE_ADMIN) }

            logger.info("Creating regular API user '{}'. Call using token {}", user.name, userToken)
            logger.info("Creating admin API user '{}'. Call using token {}", admin.name, adminToken)

            repeat(100) {
                movieService.createMovie(MovieV1Dto(name = "Movie no $it"))
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}
