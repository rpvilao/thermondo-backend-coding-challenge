package com.example.persistence.entity

import jakarta.persistence.*

@Entity
class AppUserMovieRating(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne
    var appUser: AppUser? = null,
    @ManyToOne
    var movie: Movie? = null,
    var rating: Int = 0
)