package com.gorman.testapp_innowise.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.testapp_innowise.data.models.BookmarkImage
import com.gorman.testapp_innowise.data.repository.BookmarksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: BookmarksRepository
) : ViewModel() {
    private val _bookmarks = MutableStateFlow<List<BookmarkImage>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkImage>> = _bookmarks.asStateFlow()

    fun loadBookmarks() {
        viewModelScope.launch {
            repository.getAllImages().collect {
                _bookmarks.value = it
            }
        }
    }

    fun addBookmark(imageUrl: String, name: String) {
        viewModelScope.launch {
            repository.insertImage(imageUrl, name)
        }
    }

}