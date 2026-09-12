package com.example.albumassignment

import com.example.albumassignment.data.Album
import com.example.albumassignment.data.DashboardResponse

// Sample values for tests only. Mockito supplies the repository/API behavior.
fun sampleDashboard() = DashboardResponse(
    entities = listOf(
        Album(
            artistName = "Test Artist",
            albumTitle = "Test Album",
            releaseYear = 2000,
            genre = "Rock",
            trackCount = 10,
            description = "Description for a unit test.",
            popularTrack = "Test Song"
        )
    ),
    entityTotal = 1
)
