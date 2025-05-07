package com.example.persistence.repository

import com.example.persistence.entity.AppUser
import com.example.persistence.entity.AppUserMovieRating
import com.example.persistence.entity.Movie
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AppUserMovieRatingRepository : JpaRepository<AppUserMovieRating, Long> {
    fun findByAppUserAndMovie(appUser: AppUser, movie: Movie): AppUserMovieRating?

    @Query(
        """
        SELECT aumr
        FROM AppUserMovieRating aumr
        WHERE aumr.appUser = :appUser
        AND aumr.movie.id IN :movieIds
    """
    )
    fun findByAppUserAndMovieIdIn(appUser: AppUser, movieIds: Collection<Long>): List<AppUserMovieRating>

    fun findByAppUser(appUser: AppUser, pageable: Pageable): Page<AppUserMovieRating>
}