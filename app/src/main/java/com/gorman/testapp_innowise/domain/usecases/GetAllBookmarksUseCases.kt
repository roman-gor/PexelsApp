package com.gorman.testapp_innowise.domain.usecases

import com.gorman.testapp_innowise.domain.models.Bookmark
import com.gorman.testapp_innowise.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBookmarksUseCases @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<Bookmark>> {
        return bookmarkRepository.getAllImages()
    }
}