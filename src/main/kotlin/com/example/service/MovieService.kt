package com.example.service

import com.example.persistence.entity.AppUserMovieRating
import com.example.persistence.entity.Movie
import com.example.persistence.repository.AppUserMovieRatingRepository
import com.example.persistence.repository.MovieRepository
import com.example.presentation.dto.AppUserMovieRatingV1Dto
import com.example.presentation.dto.MovieV1Dto
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MovieService(
    private val movieRepository: MovieRepository,
    private val appUserMovieRaringRepository: AppUserMovieRatingRepository,
    private val appUserService: AppUserService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createMovie(movieV1Dto: MovieV1Dto): Movie {
        logger.debug("Creating movie entry {}", movieV1Dto)

        requireNotNull(movieV1Dto.name) { "Movie name cannot be null." }

        return Movie(
            name = movieV1Dto.name
        ).let(movieRepository::save)
    }

    fun list(pageable: Pageable): Page<Movie> {
        return movieRepository.findAll(pageable)
    }

    fun get(movieId: Long): Movie {
        return movieRepository.findByIdOrNull(movieId)
            ?: throw RuntimeException("No movie with id $movieId found.")
    }

    fun fetchUserRating(username: String, movieIds: Collection<Long>): Map<Long, AppUserMovieRating> {
        return appUserMovieRaringRepository.findByAppUserAndMovieIdIn(
            appUserService.get(username),
            movieIds
        ).associateBy { it.id }
    }

    @Transactional
    fun rate(username: String, movieId: Long, userRating: AppUserMovieRatingV1Dto): AppUserMovieRating {
        logger.debug("Rating movie {} for user {} with {}", movieId, username, userRating)
        val appUser = appUserService.get(username)
        val movie = get(movieId)

        require(userRating.rating in 1..5) { "Rating must be comprised between 1 and 5." }

        // NOTE: The user might be changing the rating.
        val currentRating = appUserMovieRaringRepository.findByAppUserAndMovie(appUser, movie)

        if (currentRating != null) {
            movie.ratingSum -= currentRating.rating
        } else {
            movie.ratingCount++
        }

        movie.ratingSum += userRating.rating
        movie.rating = movie.ratingSum.toFloat() / movie.ratingCount

        val savedRating = appUserMovieRaringRepository.save(
            (currentRating ?: AppUserMovieRating()).apply {
                this.appUser = appUser
                this.movie = movie
                this.rating = userRating.rating
            }
        )

        movieRepository.save(movie)
        return savedRating
    }

    fun listUserMovies(username: String, pageable: Pageable): Page<Movie> {
        val user = appUserService.get(username)
        return appUserMovieRaringRepository.findByAppUser(user, pageable)
            .map { it.movie }
    }
}