package com.gorman.testapp_innowise.domain.usecases

import com.gorman.testapp_innowise.domain.repository.BookmarkRepository
import javax.inject.Inject

class DeleteBookmarkByUrlUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
){
    suspend operator fun invoke(url: String) {
        bookmarkRepository.deleteByUrl(url)
    }
}