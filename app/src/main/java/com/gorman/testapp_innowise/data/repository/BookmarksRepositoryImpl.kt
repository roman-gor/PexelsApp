package com.gorman.testapp_innowise.data.repository

import com.gorman.testapp_innowise.data.datasource.local.BookmarksImageDao
import com.gorman.testapp_innowise.data.mapper.toDomain
import com.gorman.testapp_innowise.data.models.BookmarkImage
import com.gorman.testapp_innowise.domain.models.Bookmark
import com.gorman.testapp_innowise.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarksRepositoryImpl @Inject constructor(
    private val dao: BookmarksImageDao
): BookmarkRepository {
    override suspend fun insertImage(url: String, name: String) {
        dao.insert(BookmarkImage(imageUrl = url, phName = name))
    }

    override fun getAllImages(): Flow<List<Bookmark>> {
        return dao.getAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun isImage(url: String): Boolean = dao.existsByUrl(url)

    override suspend fun deleteByUrl(url: String) {
        dao.deleteByUrl(url)
    }

    override suspend fun findImageById(imageId: Int): Bookmark? {
        return dao.findById(imageId)?.toDomain()
    }
}