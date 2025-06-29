package com.gorman.testapp_innowise.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.testapp_innowise.data.api.CollectionItem
import com.gorman.testapp_innowise.data.api.Photo
import com.gorman.testapp_innowise.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    private val _collections = MutableStateFlow<List<CollectionItem>>(emptyList())
    val collections: StateFlow<List<CollectionItem>> = _collections.asStateFlow()

    private val _isEmpty = MutableStateFlow<Boolean>(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private var current_page = 1
    private var isLoading = false
    private var allLoading = false
    var selectedFeaturedButton = -1

    fun loadNextPage(query: String)
    {
        if (isLoading || allLoading) return
        isLoading = true
        viewModelScope.launch {
            try {
                val result = repository.search(query, page = current_page)
                if (result.total_results == 0) {
                    allLoading = true
                } else {
                    current_page++
                    _photos.value += result.photos
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка при получении: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun refresh(query: String)
    {
        current_page = 1
        allLoading = false
        _photos.value = emptyList()
        loadNextPage(query)
    }

    fun loadPhotos(query: String)
    {
        viewModelScope.launch {
            try {
                current_page = 1
                isLoading = false
                allLoading = false
                _photos.value = emptyList()
                loadNextPage(query)
                Log.d("HomeViewModel", "Запрос отправлен")
                val result = repository.search(query)
                Log.d("HomeViewModel", "Результат: ${result.total_results} коллекций")
                val total = result.photos.size
                if (total == 0) {
                    _isEmpty.value = true
                    return@launch
                }
                val loaded = mutableListOf<Photo>()
                for ((index, photo) in result.photos.withIndex()) {
                    delay(30L) // для видимости прогресса
                    loaded.add(photo)
                    _photos.value = loaded.toList()
                    _progress.value = ((index + 1) * 100 / total)
                }
                _photos.value = result.photos
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка при получении: ${e.message}", e)
            }
            finally {
                _progress.value = 100
            }
        }
    }

    fun loadCuratedPhotos()
    {
        viewModelScope.launch {
            try {
                current_page = 1
                isLoading = false
                allLoading = false
                _photos.value = emptyList()
                loadNextCuratedPhotos()
                Log.d("HomeViewModel", "Запрос отправлен")
                val result = repository.searchCurated()
                Log.d("HomeViewModel", "Результат: ${result.size} коллекций")
                _photos.value = result
                _isEmpty.value = result.isEmpty()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка при получении: ${e.message}", e)
            }
        }
    }

    fun loadNextCuratedPhotos()
    {
        if (isLoading || allLoading) return
        isLoading = true
        viewModelScope.launch {
            try {
                val result = repository.searchCurated(page = current_page)
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

    fun loadFeatureCollections()
    {
        viewModelScope.launch {
            try {
                _collections.value = emptyList()
                val result = repository.searchFeaturedCollections()
                _collections.value = result
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка при получении: ${e.message}", e)
            }
        }
    }
}