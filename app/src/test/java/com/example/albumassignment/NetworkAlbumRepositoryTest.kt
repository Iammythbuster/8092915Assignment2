package com.example.albumassignment

import com.example.albumassignment.data.*
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NetworkAlbumRepositoryTest {
    @Test
    fun sendsCredentialsUnchangedAndReturnsApiKeypass() = runTest {
        val api = mock<ApiService>()
        val request = LoginRequest("8092915", "TeStName")
        whenever(api.login(request)).thenReturn(LoginResponse("topic-from-api"))
        val repo = NetworkAlbumRepository(api)

        assertEquals("topic-from-api", repo.login("8092915", "TeStName"))
        verify(api).login(request)
    }

    @Test
    fun rejectsBlankAndMissingKeypass() = runTest {
        for (badValue in listOf(null, "", " ")) {
            val api = mock<ApiService>()
            val request = LoginRequest("8092915", "TestName")
            whenever(api.login(request)).thenReturn(LoginResponse(badValue))
            val repo = NetworkAlbumRepository(api)
            try {
                repo.login("8092915", "TestName")
                fail("An empty keypass must not count as a successful login.")
            } catch (_: ApiDataException) {
                // The repository correctly rejects this response.
            }
            verify(api).login(request)
        }
    }

    @Test
    fun passesDashboardKeypassToApi() = runTest {
        val api = mock<ApiService>()
        val expected = sampleDashboard()
        whenever(api.getDashboard("actual-keypass")).thenReturn(expected)

        val response = NetworkAlbumRepository(api).getDashboard("actual-keypass")

        assertEquals(expected, response)
        verify(api).getDashboard("actual-keypass")
    }

    @Test
    fun letsViewModelHandleNetworkErrors() = runTest {
        val api = mock<ApiService>()
        val request = LoginRequest("8092915", "TestName")
        val failure = IOException("offline")
        whenever(api.login(request)).thenAnswer { throw failure }
        val repo = NetworkAlbumRepository(api)
        try {
            repo.login("8092915", "TestName")
            fail("Network failure should be passed to the caller.")
        } catch (error: IOException) {
            assertSame(failure, error)
        }
        verify(api).login(request)
    }
}
