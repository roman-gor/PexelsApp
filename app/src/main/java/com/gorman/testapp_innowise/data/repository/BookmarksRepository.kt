package com.gorman.testapp_innowise.data.repository

import com.gorman.testapp_innowise.data.database.BookmarksImageDao
import com.gorman.testapp_innowise.data.models.BookmarkImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookmarksRepository @Inject constructor(
    private val dao: BookmarksImageDao
) {
    suspend fun insertImage(url: String, name: String) {
        dao.insert(BookmarkImage(imageUrl = url, phName = name))
    }

    fun getAllImages(): Flow<List<BookmarkImage>> = flow {
        emit(dao.getAll())
    }

    suspend fun isImage(url: String): Boolean = dao.existsByUrl(url)

    suspend fun deleteByUrl(url: String)
    {
        dao.deleteByUrl(url)
    }
}