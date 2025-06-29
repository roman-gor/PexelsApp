package com.gorman.testapp_innowise.data.database

import androidx.room.*
import com.gorman.testapp_innowise.data.models.BookmarkImage
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarksImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(image: BookmarkImage)

    @Query("SELECT * FROM bookmarks_images")
    suspend fun getAll(): List<BookmarkImage>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks_images WHERE imageUrl = :url)")
    suspend fun existsByUrl(url: String): Boolean

    @Query("DELETE FROM bookmarks_images WHERE imageUrl = :url")
    suspend fun deleteByUrl(url: String)

    @Delete
    suspend fun delete(image: BookmarkImage)
}