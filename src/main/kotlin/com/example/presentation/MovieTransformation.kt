package com.example.presentation

import com.example.persistence.entity.Movie
import com.example.presentation.dto.AppUserMovieRatingV1Dto
import com.example.presentation.dto.MovieV1Dto
import com.example.service.MovieService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Page
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.stereotype.Service

@Service
class MovieTransformation(
    private val movieService: MovieService,
    private val dispatcher: CoroutineDispatcher
) {
    fun movie2Dto(movie: Movie): MovieV1Dto {
        return MovieV1Dto(
            id = movie.id,
            name = movie.name,
            rating = movie.rating,
        )
    }

    fun movies2Dto(oAuth2AuthenticatedPrincipal: OAuth2AuthenticatedPrincipal, page: Page<Movie>): Page<MovieV1Dto> {
        val movieIds = page.map { it.id }.content

        return runBlocking {
            coroutineScope {
                // Intended to add more async enrichment if needed.
                val ratingsAsync = async(dispatcher) {
                    movieService.fetchUserRating(oAuth2AuthenticatedPrincipal.name, movieIds)
                }

                val ratings = ratingsAsync.await()

                page.map(this@MovieTransformation::movie2Dto)
                    .map { movie ->
                        ratings[movie.id]?.let { movie.copy(userRating = AppUserMovieRatingV1Dto(it.rating)) }
                    }
            }
        }

    }
}