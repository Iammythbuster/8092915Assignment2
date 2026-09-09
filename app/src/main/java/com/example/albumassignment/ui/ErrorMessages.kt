package com.example.albumassignment.ui

import com.example.albumassignment.data.ApiDataException
import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

// Plain Kotlin messages keep error handling independent of Android views.
fun errorMessage(error: Exception, duringLogin: Boolean = false): String {
    return when (error) {
        is HttpException -> when (error.code()) {
            400 -> if (duringLogin) "Check your student ID and first name."
            else "The request was rejected. Please try again."
            401, 403 -> if (duringLogin)
                "Login failed. Check your student ID and first name, including capital letters."
            else "Access was denied. Close and reopen the app, then log in again."
            in 500..599 -> "The server is unavailable. Please try again shortly."
            else -> "Request failed (HTTP ${error.code()}). Please try again."
        }
        is SocketTimeoutException -> "The server took too long. Please try again."
        is IOException -> "Cannot connect. Check your internet connection and try again."
        is ApiDataException -> "The server returned an empty keypass. Please try again."
        else -> "Something went wrong. Please try again."
    }
}