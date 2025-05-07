package com.example.service

import com.example.controller.AlreadyExistsException
import com.example.controller.NotFoundException
import com.example.persistence.entity.AppUser
import com.example.persistence.repository.AppUserRepository
import com.example.presentation.dto.UserV1Dto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AppUserService(
    private val appUserRepository: AppUserRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun get(username: String): AppUser {
        return appUserRepository.findByUsername(username)
            ?: throw NotFoundException("User not found.")
    }

    fun createAppUser(appUserV1Dto: UserV1Dto): AppUser {
        logger.debug("Creating user {}", appUserV1Dto)

        requireNotNull(appUserV1Dto.username) { "username cannot be null." }
        // NOTE: I would validate the email with a Regex.

        if (appUserRepository.findByUsername(appUserV1Dto.username) != null) {
            throw AlreadyExistsException("Username already exists")
        }

        return appUserRepository.save(
            AppUser(
                name = appUserV1Dto.name,
                username = appUserV1Dto.username
            )
        )
    }
}