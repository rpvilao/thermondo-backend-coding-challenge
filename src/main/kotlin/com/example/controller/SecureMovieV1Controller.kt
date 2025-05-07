package com.example.controller

import com.example.auth.AuthService
import com.example.presentation.MovieTransformation
import com.example.presentation.dto.AppUserMovieRatingV1Dto
import com.example.presentation.dto.MovieV1Dto
import com.example.service.MovieService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/secure/movies")
class SecureMovieV1Controller(
    private val movieService: MovieService,
    private val movieTransformation: MovieTransformation
) {
    @GetMapping
    fun list(@AuthenticationPrincipal user: OAuth2AuthenticatedPrincipal, pageable: Pageable): Page<MovieV1Dto> {
        return movieService.list(pageable)
            .let { movieTransformation.movies2Dto(user, it) }
    }

    @GetMapping("/{id}")
    fun get(
        @AuthenticationPrincipal user: OAuth2AuthenticatedPrincipal,
        @PathVariable("id") id: Long
    ): MovieV1Dto {
        return movieTransformation.movie2Dto(movieService.get(id))
    }

    @Secured(AuthService.ROLE_ADMIN)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody movieV1Dto: MovieV1Dto): MovieV1Dto {
        return movieService.createMovie(movieV1Dto)
            .let(movieTransformation::movie2Dto)
    }

    @PostMapping("/{id}/_rate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun rate(
        @AuthenticationPrincipal user: OAuth2AuthenticatedPrincipal,
        @PathVariable("id") id: Long,
        @RequestBody appUserMovieRatingV1Dto: AppUserMovieRatingV1Dto
    ) {
        movieService.rate(user.name, id, appUserMovieRatingV1Dto)
    }
}