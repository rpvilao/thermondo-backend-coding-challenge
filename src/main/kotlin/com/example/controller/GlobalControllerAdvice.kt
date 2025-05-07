package com.example.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // NOTE: Ideally we wouldn't expose exception messages outside not to expose the tech in use.
    //       I would also create a JSON representation for these errors.

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ex.message)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun preconditionFailed(ex: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
            .body(ex.message)
    }

    @ExceptionHandler(AlreadyExistsException::class)
    fun handleConflict(ex: AlreadyExistsException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ex.message)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun accessDenied(ex: AccessDeniedException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ex.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<String> {
        logger.error("Internal server error", ex)

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Unexpected error: ${ex.message}")
    }
}

data class NotFoundException(override val message: String) : RuntimeException(message)
data class AlreadyExistsException(override val message: String) : RuntimeException(message)
