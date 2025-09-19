package com.gorman.testapp_innowise.ui.details

import androidx.lifecycle.ViewModel
import com.gorman.testapp_innowise.data.models.BookmarkImage
import com.gorman.testapp_innowise.domain.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: BookmarkRepository
) : ViewModel() {
    private val _bookmarks = MutableStateFlow<List<BookmarkImage>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkImage>> = _bookmarks.asStateFlow()

    suspend fun addBookmark(imageUrl: String, name: String) {
        repository.insertImage(imageUrl, name)
    }

    suspend fun searchInDBOnce(url: String): Boolean {
        return repository.isImage(url)
    }

    suspend fun deleteByUrl(url: String)
    {
        repository.deleteByUrl(url)
    }
}