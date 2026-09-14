package com.example.albumassignment

import com.example.albumassignment.data.DashboardResponse
import com.google.gson.JsonPrimitive

fun sampleDashboard() = DashboardResponse(
    entities = listOf(
        mapOf(
            "artistName" to JsonPrimitive("Test Artist"),
            "albumTitle" to JsonPrimitive("Test Album"),
            "releaseYear" to JsonPrimitive(2000),
            "genre" to JsonPrimitive("Rock"),
            "trackCount" to JsonPrimitive(10),
            "description" to JsonPrimitive("Description for a unit test."),
            "popularTrack" to JsonPrimitive("Test Song")
        )
    ),
    entityTotal = 1
)