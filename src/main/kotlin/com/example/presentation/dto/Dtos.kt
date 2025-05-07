package com.example.presentation.dto

data class MovieV1Dto(
    val id: Long? = null,
    val name: String,
    val rating: Float? = null,
    val userRating: AppUserMovieRatingV1Dto? = null,
)

data class AppUserMovieRatingV1Dto(val rating: Int)

data class UserV1Dto(val id: Long? = null, val name: String, val username: String)