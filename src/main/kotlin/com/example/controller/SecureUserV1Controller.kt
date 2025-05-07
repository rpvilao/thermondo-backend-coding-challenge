package com.example.controller

import com.example.presentation.MovieTransformation
import com.example.presentation.UserTransformation
import com.example.presentation.dto.MovieV1Dto
import com.example.presentation.dto.UserV1Dto
import com.example.service.AppUserService
import com.example.service.MovieService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/secure/users")
class SecureUserV1Controller(
    private val movieService: MovieService,
    private val movieTransformation: MovieTransformation,
    private val userTransformation: UserTransformation,
    private val appUserService: AppUserService
) {
    @GetMapping("/_me")
    fun me(@AuthenticationPrincipal user: DefaultOAuth2AuthenticatedPrincipal): UserV1Dto {
        return appUserService.get(user.name)
            .let(userTransformation::user2Dto)

    }

    @GetMapping("/movies/_rated")
    fun userRatedMovies(
        @AuthenticationPrincipal user: DefaultOAuth2AuthenticatedPrincipal,
        pageable: Pageable
    ): Page<MovieV1Dto> {
        return movieService.listUserMovies(user.name, pageable)
            .let {
                movieTransformation.movies2Dto(user, it)
            }
    }

}