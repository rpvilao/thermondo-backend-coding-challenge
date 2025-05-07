package com.example

import com.example.auth.AuthService
import com.example.persistence.entity.AppUser
import com.example.presentation.dto.MovieV1Dto
import com.example.presentation.dto.UserV1Dto
import com.example.service.AppUserService
import com.example.service.MovieService
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest(classes = [Main::class])
@ContextConfiguration(classes = [ApplicationTest.TestConfig::class])
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestExecutionListeners(
    DependencyInjectionTestExecutionListener::class,
    DirtiesContextTestExecutionListener::class,
    TransactionalTestExecutionListener::class
)
@Transactional
@Rollback
class ApplicationTest {
    @Autowired
    private lateinit var appUserService: AppUserService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var movieService: MovieService

    @Autowired
    private lateinit var authService: AuthService

    private lateinit var admin: AppUser
    private lateinit var adminToken: String
    private lateinit var user: AppUser
    private lateinit var user2: AppUser
    private lateinit var userToken: String
    private lateinit var userToken2: String

    @BeforeEach
    fun setup() {
        this.admin = appUserService.createAppUser(
            UserV1Dto(username = "testadmin@example.com", name = "admin")
        )
        this.adminToken = authService.createToken(admin.username, AuthService.ROLE_ADMIN)

        this.user = appUserService.createAppUser(
            UserV1Dto(username = "testuser@example.com", name = "user")
        )
        this.userToken = authService.createToken(user.username, AuthService.ROLE_USER)

        this.user2 = appUserService.createAppUser(
            UserV1Dto(username = "testuser2@example.com", name = "user")
        )
        this.userToken2 = authService.createToken(admin.username, AuthService.ROLE_USER)
    }

    @Test
    fun `try to add movies with an admin user and it works`() {
        mockMvc.perform(
            post("/api/v1/secure/movies")
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer $adminToken")
                .content("""{"name":"movie1"}""")
        ).andDo(print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("movie1"))

    }

    @Test
    fun `try to add movies with a regular user fails`() {
        mockMvc.perform(
            post("/api/v1/secure/movies")
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer $userToken")
                .content("""{"name":"movie1"}""")
        ).andDo(print())
            .andExpect(status().isForbidden)
    }

    @Test
    fun `rate movies and only mine come`() {
        val movie1 = addRandomMovie()
        val movie2 = addRandomMovie()

        mockMvc.perform(
            post("/api/v1/secure/movies/$movie1/_rate")
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer $userToken")
                .content("""{"rating":5}""")
        ).andDo(print()).andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/v1/secure/users/movies/_rated")
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer $userToken")
        ).andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].userRating.rating").value(5))
    }

    @Test
    fun `average rating is properly calculated`() {
        val movie1 = addRandomMovie()

        mockMvc.perform(
            post("/api/v1/secure/movies/$movie1/_rate")
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer $userToken")
                .content("""{"rating":5}""")
        ).andDo(print()).andExpect(status().isNoContent)

        mockMvc.perform(
            post("/api/v1/secure/movies/$movie1/_rate")
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer $userToken2")
                .content("""{"rating":3}""")
        ).andDo(print()).andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/v1/secure/movies/${movie1}")
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer $userToken")
        ).andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.rating").value(4F))

        // Changing it calculates the new one.

        mockMvc.perform(
            post("/api/v1/secure/movies/$movie1/_rate")
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer $userToken2")
                .content("""{"rating":2}""")
        ).andDo(print()).andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/v1/secure/movies/${movie1}")
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer $userToken")
        ).andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.rating").value(3.5F))
    }

    @Test
    fun `use a rating that doesn't validate`() {
        val movie1 = addRandomMovie()

        mockMvc.perform(
            post("/api/v1/secure/movies/$movie1/_rate")
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer $userToken")
                .content("""{"rating":6}""")
        ).andDo(print()).andExpect(status().isPreconditionFailed)
    }

    @Test
    fun `add a user twice`() {
        val username = "${UUID.randomUUID()}@example.com"

        mockMvc.perform(
            post("/api/v1/public/users")
                .header("Content-type", "application/json")
                .content("""{"username":"$username","name":"John Doe"}""")
        ).andDo(print()).andExpect(status().isCreated)

        mockMvc.perform(
            post("/api/v1/public/users")
                .header("Content-type", "application/json")
                .content("""{"username":"$username","name":"John Doe"}""")
        ).andDo(print()).andExpect(status().isConflict)

    }

    @TestConfiguration
    class TestConfig {
        @Bean
        fun dispatcher(): CoroutineDispatcher {
            // Run dispatcher in the same thread so we don't have issues with the transaction not being committed in tests.
            return MoreExecutors.directExecutor().asCoroutineDispatcher()
        }
    }

    private fun addRandomMovie(): Long {
        return movieService.createMovie(MovieV1Dto(name = "Move title 1")).id
    }
}