package com.example.albumassignment

import com.example.albumassignment.data.*
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class NetworkAlbumRepositoryTest {
    private class FakeApi : ApiService {
        var keypass: String? = "topic-from-api"
        var loginRequest: LoginRequest? = null
        var dashboardKeypass: String? = null
        var error: Exception? = null

        override suspend fun login(request: LoginRequest): LoginResponse {
            loginRequest = request
            error?.let { throw it }
            return LoginResponse(keypass)
        }

        override suspend fun getDashboard(keypass: String): DashboardResponse {
            dashboardKeypass = keypass
            return DashboardResponse(emptyList(), 0)
        }
    }

    @Test
    fun sendsCredentialsUnchangedAndReturnsApiKeypass() = runTest {
        val api = FakeApi()
        val repo = NetworkAlbumRepository(api)
        assertEquals("topic-from-api", repo.login("8092915", "TeStName"))
        assertEquals(LoginRequest("8092915", "TeStName"), api.loginRequest)
    }

    @Test
    fun rejectsBlankAndMissingKeypass() = runTest {
        for (badValue in listOf(null, "", " ")) {
            val repo = NetworkAlbumRepository(FakeApi().apply { keypass = badValue })
            try {
                repo.login("8092915", "TestName")
                fail("An empty keypass must not count as a successful login.")
            } catch (_: ApiDataException) {
                // The repository correctly rejects this response.
            }
        }
    }

    @Test
    fun passesDashboardKeypassToApi() = runTest {
        val api = FakeApi()
        val response = NetworkAlbumRepository(api).getDashboard("actual-keypass")
        assertEquals("actual-keypass", api.dashboardKeypass)
        assertEquals(0, response.entityTotal)
    }

    @Test
    fun letsViewModelHandleNetworkErrors() = runTest {
        val failure = IOException("offline")
        val repo = NetworkAlbumRepository(FakeApi().apply { error = failure })
        try {
            repo.login("8092915", "TestName")
            fail("Network failure should be passed to the caller.")
        } catch (error: IOException) {
            assertSame(failure, error)
        }
    }
}