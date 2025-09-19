package com.gorman.testapp_innowise.domain.usecases

import com.gorman.testapp_innowise.domain.repository.BookmarkRepository
import javax.inject.Inject

class SearchInDBOnceUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(url: String): Boolean = bookmarkRepository.isImage(url)
}