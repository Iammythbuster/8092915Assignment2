package com.example.albumassignment.data

// ViewModels use this interface so tests can supply a fake repository.
interface AlbumRepository {
    suspend fun login(username: String, password: String): String
    suspend fun getDashboard(keypass: String): DashboardResponse
}

class ApiDataException : Exception()

class NetworkAlbumRepository(private val api: ApiService) : AlbumRepository {
    override suspend fun login(username: String, password: String): String {
        val keypass = api.login(LoginRequest(username, password)).keypass
        if (keypass.isNullOrBlank()) throw ApiDataException()
        return keypass
    }

    override suspend fun getDashboard(keypass: String): DashboardResponse {
        return api.getDashboard(keypass)
    }
}