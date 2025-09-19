package com.gorman.testapp_innowise.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.testapp_innowise.domain.models.Bookmark
import com.gorman.testapp_innowise.domain.usecases.AddBookmarkUseCase
import com.gorman.testapp_innowise.domain.usecases.DeleteBookmarkByUrlUseCase
import com.gorman.testapp_innowise.domain.usecases.FindBookmarkByIdUseCase
import com.gorman.testapp_innowise.domain.usecases.SearchInDBOnceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val searchInDBOnceUseCase: SearchInDBOnceUseCase,
    private val deleteBookmarkByUrlUseCase: DeleteBookmarkByUrlUseCase,
    private val findBookmarkByIdUseCase: FindBookmarkByIdUseCase
) : ViewModel() {

    private val _bookmark = MutableStateFlow<Bookmark?>(null)
    val bookmark: StateFlow<Bookmark?> = _bookmark.asStateFlow()

    fun addBookmark(imageUrl: String, name: String) {
        viewModelScope.launch {
            addBookmarkUseCase(imageUrl, name)
        }
    }

    fun findBookmarkById(imageId: Int) {
        viewModelScope.launch {
            _bookmark.value = findBookmarkByIdUseCase(imageId)
        }
    }

    fun searchInDBOnce(url: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = searchInDBOnceUseCase(url)
            onResult(result)
        }
    }

    fun deleteByUrl(url: String) {
        viewModelScope.launch {
            deleteBookmarkByUrlUseCase(url)
        }
    }
}