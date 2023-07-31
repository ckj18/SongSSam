package com.example.songssam.data

import android.graphics.Bitmap
import java.util.*

class SelectedItem
    (
    val songID: String, val title: String, val artist: String

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as SelectedItem

        if (songID != other.songID) return false
        if (title != other.title) return false
        if (artist != other.artist) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(songID,title,artist)
    }

    override fun toString(): String {
        return "SelectedItem(songID='$songID', title='$title', artist='$artist')"
    }


}