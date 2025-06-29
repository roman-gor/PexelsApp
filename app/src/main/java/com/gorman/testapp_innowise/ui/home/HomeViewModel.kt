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

sealed class LoadResult {
    object Success : LoadResult()
    object Empty : LoadResult()
    data class Error(val exception: Throwable) : LoadResult()
}

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

    private val _loadResult = MutableStateFlow<LoadResult>(LoadResult.Success)
    val loadResult: StateFlow<LoadResult> = _loadResult.asStateFlow()

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
                _loadResult.value = LoadResult.Success
                if (total == 0) {
                    _isEmpty.value = true
                    _loadResult.value = LoadResult.Empty
                    return@launch
                }
                val loaded = mutableListOf<Photo>()
                for ((index, photo) in result.photos.withIndex()) {
                    delay(30L)
                    loaded.add(photo)
                    _photos.value = loaded.toList()
                    _progress.value = ((index + 1) * 100 / total)
                }
                _photos.value = result.photos
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка при получении: ${e.message}", e)
                if (_photos.value.isEmpty()) {
                    _loadResult.value = LoadResult.Error(e)
                }
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
                _loadResult.value = LoadResult.Success
                Log.d("HomeViewModel", "Запрос отправлен")
                val result = repository.searchCurated()
                Log.d("HomeViewModel", "Результат: ${result.total_results} коллекций")
                _photos.value = result.photos
                val total = result.photos.size
                val loaded = mutableListOf<Photo>()
                if (total == 0) {
                    _isEmpty.value = true
                    _loadResult.value = LoadResult.Empty
                    return@launch
                }
                for ((index, photo) in result.photos.withIndex()) {
                    delay(30L)
                    loaded.add(photo)
                    _photos.value = loaded.toList()
                    _progress.value = ((index + 1) * 100 / total)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка при получении: ${e.message}", e)
                if (_photos.value.isEmpty()) {
                    _loadResult.value = LoadResult.Error(e)
                }
            }
            finally {
                _progress.value = 100
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