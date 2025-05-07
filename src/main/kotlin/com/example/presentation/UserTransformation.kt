package com.example.presentation

import com.example.persistence.entity.AppUser
import com.example.presentation.dto.UserV1Dto
import org.springframework.stereotype.Service

@Service
class UserTransformation {
    fun user2Dto(user: AppUser): UserV1Dto {
        return UserV1Dto(
            id = user.id,
            name = user.name,
            username = user.username,
        )
    }

}