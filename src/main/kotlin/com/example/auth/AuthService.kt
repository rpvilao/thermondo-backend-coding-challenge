package com.example.auth

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService : OpaqueTokenIntrospector {
    private val authenticatedUsers = mutableMapOf<String, DefaultOAuth2AuthenticatedPrincipal>()

    companion object {
        const val ROLE_USER = "ROLE_USER"
        const val ROLE_ADMIN = "ROLE_ADMIN"
    }

    fun createToken(username: String, role: String): String {
        val uuid = UUID.randomUUID().toString()
        authenticatedUsers[uuid] = DefaultOAuth2AuthenticatedPrincipal(
            username,
            mapOf("token" to uuid),
            listOf(SimpleGrantedAuthority(role))
        )

        return uuid
    }

    override fun introspect(token: String?): OAuth2AuthenticatedPrincipal {
        val auth = token?.let(authenticatedUsers::get)
            ?: throw RuntimeException("Invalid or no token provided.")

        // NOTE: A real implementation would check for the token validity among other possible security measures.

        return auth
    }
}