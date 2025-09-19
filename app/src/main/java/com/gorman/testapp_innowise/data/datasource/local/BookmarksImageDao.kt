package com.gorman.testapp_innowise.data.datasource.local

import androidx.room.*
import com.gorman.testapp_innowise.data.models.BookmarkImage
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarksImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(image: BookmarkImage)

    @Query("SELECT * FROM bookmarks_images")
    fun getAll(): Flow<List<BookmarkImage>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks_images WHERE imageUrl = :url)")
    suspend fun existsByUrl(url: String): Boolean

    @Query("DELETE FROM bookmarks_images WHERE imageUrl = :url")
    suspend fun deleteByUrl(url: String)

    @Query("SELECT * FROM bookmarks_images WHERE id = :imageId LIMIT 1")
    suspend fun findById(imageId: Int): BookmarkImage?

    @Delete
    suspend fun delete(image: BookmarkImage)
}