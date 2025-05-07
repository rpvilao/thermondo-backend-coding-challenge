package com.example.persistence.repository

import com.example.persistence.entity.Movie
import org.springframework.data.jpa.repository.JpaRepository

interface MovieRepository : JpaRepository<Movie, Long>
