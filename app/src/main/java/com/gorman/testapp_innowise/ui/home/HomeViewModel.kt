package com.gorman.testapp_innowise.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.testapp_innowise.data.api.Photo
import com.gorman.testapp_innowise.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PhotoRepository,
) : ViewModel() {

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    private var current_page = 1
    private var isLoading = false
    private var allLoading = false

    fun loadNextPage(query: String) {
        if (isLoading || allLoading) return
        isLoading = true
        viewModelScope.launch {
            try {
                val result = repository.search(query, page = current_page)
                if (result.isEmpty()) {
                    allLoading = true
                } else {
                    current_page++
                    _photos.value += result
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка при получении: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }
    fun refresh(query: String) {
        current_page = 1
        allLoading = false
        _photos.value = emptyList()
        loadNextPage(query)
    }


    fun loadPhotos(query: String) {
        viewModelScope.launch {
            try {
                current_page = 1
                isLoading = false
                allLoading = false
                _photos.value = emptyList()
                loadNextPage(query)
                Log.d("HomeViewModel", "Запрос отправлен")
                val result = repository.search(query)
                Log.d("HomeViewModel", "Результат: ${result.size} коллекций")
                _photos.value = result
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка при получении: ${e.message}", e)
            }
        }
    }
}