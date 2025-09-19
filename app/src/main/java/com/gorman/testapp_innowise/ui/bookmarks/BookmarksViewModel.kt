package com.gorman.testapp_innowise.ui.bookmarks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.testapp_innowise.data.models.BookmarkImage
import com.gorman.testapp_innowise.data.repository.BookmarksRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: BookmarksRepositoryImpl
) : ViewModel() {
    private val _bookmarks = MutableStateFlow<List<BookmarkImage>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkImage>> = _bookmarks.asStateFlow()

    private val _bookmarksProgress = MutableStateFlow<Int>(0)
    val bookmarksProgress = _bookmarksProgress.asStateFlow()

    fun loadBookmarks() {
        viewModelScope.launch {
            try {
                _bookmarksProgress.value = 0
                repository.getAllImages().collect { list ->
                    val total = list.size
                    if (total == 0)
                    {
                        _bookmarks.value = emptyList()
                        _bookmarksProgress.value = 100
                        return@collect
                    }
                    val loaded = mutableListOf<BookmarkImage>()

                    for ((index, bookmark) in list.withIndex()){
                        delay(30L)
                        loaded.add(bookmark)
                        _bookmarks.value = loaded.toList()
                        _bookmarksProgress.value = ((index + 1) * 100 / total)
                    }
                }
            } catch (e: Exception) {
                Log.e("BookmarksVM", "Ошибка при загрузке: ${e.message}", e)
            } finally {
                _bookmarksProgress.value = 100
            }
        }
    }

    fun addBookmark(imageUrl: String, name: String) {
        viewModelScope.launch {
            repository.insertImage(imageUrl, name)
        }
    }

}