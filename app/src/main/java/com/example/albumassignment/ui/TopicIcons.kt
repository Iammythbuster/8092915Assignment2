package com.example.albumassignment.ui

import com.example.albumassignment.R
import com.example.albumassignment.data.Album

fun topicIcon(item: Album): Int {
    val fields = item.keys.map { it.lowercase() }

    return when {
        "albumtitle" in fields || "trackcount" in fields ->
            R.drawable.ic_music_note

        "artworktitle" in fields ||
                ("medium" in fields && "artist" in fields) ->
            R.drawable.ic_art

        "booktitle" in fields || "isbn" in fields ->
            R.drawable.ic_book

        else -> R.drawable.ic_catalogue
    }
}