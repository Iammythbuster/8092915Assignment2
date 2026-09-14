package com.example.albumassignment.data

import com.google.gson.JsonElement

// Each item can contain different fields depending on its topic.
typealias Album = Map<String, JsonElement>

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val keypass: String?
)

data class DashboardResponse(
    val entities: List<Album>,
    val entityTotal: Int
)

// Pick a suitable heading without assuming a particular topic.
fun Album.displayTitle(): String {
    val titleKey = keys.firstOrNull {
        it.endsWith("title", ignoreCase = true)
    } ?: keys.firstOrNull {
        it.equals("name", ignoreCase = true)
    } ?: keys.firstOrNull {
        !it.equals("description", ignoreCase = true)
    }

    return titleKey?.let { getValue(it).displayValue() } ?: "Item"
}

// Dashboard shows every field except description.
fun Album.displaySummary(): String {
    return entries
        .filterNot { it.key.equals("description", ignoreCase = true) }
        .joinToString("\n") { (key, value) ->
            "${fieldLabel(key)}: ${value.displayValue()}"
        }
}

// Details also shows the description.
fun Album.displayDescription(): String {
    return entries.firstOrNull {
        it.key.equals("description", ignoreCase = true)
    }?.value?.displayValue().orEmpty()
}

private fun fieldLabel(key: String): String {
    return key
        .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
        .replace('_', ' ')
        .replaceFirstChar { it.uppercase() }
}

private fun JsonElement.displayValue(): String {
    return when {
        isJsonNull -> "Not provided"
        isJsonPrimitive -> asString
        else -> toString()
    }
}