package com.example.albumassignment.data

// Property names match the JSON supplied by the API.
data class Album(
    val artistName: String,
    val albumTitle: String,
    val releaseYear: Int,
    val genre: String,
    val trackCount: Int,
    val description: String,
    val popularTrack: String
)

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val keypass: String?)
data class DashboardResponse(val entities: List<Album>, val entityTotal: Int)