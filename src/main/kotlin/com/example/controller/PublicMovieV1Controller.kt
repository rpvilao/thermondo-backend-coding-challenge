package com.example.controller

import com.example.presentation.MovieTransformation
import com.example.presentation.dto.MovieV1Dto
import com.example.service.MovieService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/public/movies")
class PublicMovieV1Controller(
    private val transformation: MovieTransformation,
    private val movieService: MovieService,
) {
    @GetMapping
    fun list(pageable: Pageable): Page<MovieV1Dto> {
        return movieService.list(pageable)
            .map(transformation::movie2Dto)
    }
}