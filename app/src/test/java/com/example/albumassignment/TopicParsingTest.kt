package com.example.albumassignment

import com.example.albumassignment.data.DashboardResponse
import com.example.albumassignment.data.displayDescription
import com.example.albumassignment.data.displaySummary
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TopicParsingTest {

    @Test
    fun differentTopicFieldsArePreserved() {
        // Synthetic examples for testing, not real API records.
        val json = """
            {
              "entities": [
                {
                  "albumTitle": "Test Album",
                  "trackCount": 12,
                  "description": "Music description"
                },
                {
                  "artworkTitle": "Test Painting",
                  "medium": "Oil",
                  "description": "Art description"
                },
                {
                  "planetName": "Test Planet",
                  "moonCount": 3,
                  "description": "Space description"
                }
              ],
              "entityTotal": 3
            }
        """.trimIndent()

        val response = Gson().fromJson(
            json,
            DashboardResponse::class.java
        )

        assertEquals(3, response.entities.size)
        assertTrue(response.entities[0].displaySummary().contains("Track Count: 12"))
        assertTrue(response.entities[1].displaySummary().contains("Medium: Oil"))
        assertTrue(response.entities[2].displaySummary().contains("Moon Count: 3"))

        assertFalse(response.entities[1].displaySummary().contains("Art description"))
        assertEquals("Art description", response.entities[1].displayDescription())
    }
}