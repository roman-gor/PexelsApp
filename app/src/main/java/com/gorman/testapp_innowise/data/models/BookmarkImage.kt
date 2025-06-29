package com.gorman.testapp_innowise.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "bookmarks_images")
data class BookmarkImage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUrl: String,
    val phName: String
) : Parcelable