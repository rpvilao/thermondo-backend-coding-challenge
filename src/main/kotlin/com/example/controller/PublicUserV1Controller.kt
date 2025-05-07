package com.example.controller

import com.example.presentation.UserTransformation
import com.example.presentation.dto.UserV1Dto
import com.example.service.AppUserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/public/users")
class PublicUserV1Controller(
    private val userTransformation: UserTransformation,
    private val appUserService: AppUserService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody userV1Dto: UserV1Dto): UserV1Dto {
        return appUserService.createAppUser(userV1Dto)
            .let(userTransformation::user2Dto)

    }


}