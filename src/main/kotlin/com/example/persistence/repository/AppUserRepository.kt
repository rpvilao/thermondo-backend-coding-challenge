package com.example.persistence.repository

import com.example.persistence.entity.AppUser
import com.example.persistence.entity.Movie
import org.springframework.data.jpa.repository.JpaRepository

interface AppUserRepository : JpaRepository<AppUser, Long> {
    fun findByUsername(username: String): AppUser?
}