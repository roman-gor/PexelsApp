package com.gorman.testapp_innowise.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gorman.testapp_innowise.data.models.BookmarkImage

@Database(entities = [BookmarkImage::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkImageDao(): BookmarksImageDao
}