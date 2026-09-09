package com.example.albumassignment

import com.example.albumassignment.data.Album
import com.example.albumassignment.data.AlbumRepository
import com.example.albumassignment.data.DashboardResponse

class FakeAlbumRepository : AlbumRepository {
    var loginResult = "music"
    var loginError: Exception? = null
    var dashboardError: Exception? = null
    var loginCalls = 0
    var dashboardCalls = 0
    var lastUsername: String? = null
    var lastPassword: String? = null
    var lastKeypass: String? = null
    var dashboardResult = DashboardResponse(
        listOf(Album("Test Artist", "Test Album", 2000, "Rock", 10,
            "Description for a unit test.", "Test Song")),
        1
    )

    override suspend fun login(username: String, password: String): String {
        loginCalls++
        lastUsername = username
        lastPassword = password
        loginError?.let { throw it }
        return loginResult
    }

    override suspend fun getDashboard(keypass: String): DashboardResponse {
        dashboardCalls++
        lastKeypass = keypass
        dashboardError?.let { throw it }
        return dashboardResult
    }
}