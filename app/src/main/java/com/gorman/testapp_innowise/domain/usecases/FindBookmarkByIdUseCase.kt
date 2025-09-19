package com.gorman.testapp_innowise.domain.usecases

import com.gorman.testapp_innowise.domain.models.Bookmark
import com.gorman.testapp_innowise.domain.repository.BookmarkRepository
import javax.inject.Inject

class FindBookmarkByIdUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(imageId: Int): Bookmark = bookmarkRepository.findImageById(imageId)
}