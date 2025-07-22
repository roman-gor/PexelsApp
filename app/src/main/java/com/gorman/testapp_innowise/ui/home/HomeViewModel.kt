package com.gorman.testapp_innowise.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.testapp_innowise.data.models.CollectionItem
import com.gorman.testapp_innowise.data.models.Photo
import com.gorman.testapp_innowise.data.useCase.GetCuratedPhotosUseCase
import com.gorman.testapp_innowise.data.useCase.GetFeaturedCollectionsUseCase
import com.gorman.testapp_innowise.data.useCase.GetPhotosByQueryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoadResult {
    object Loading : LoadResult()
    object Success : LoadResult()
    object Empty : LoadResult()
    data class Error(val exception: Throwable) : LoadResult()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPhotosByQueryUseCase: GetPhotosByQueryUseCase,
    private val getCuratedPhotosUseCase: GetCuratedPhotosUseCase,
    private val getFeaturedCollectionsUseCase: GetFeaturedCollectionsUseCase
) : ViewModel() {

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    private val _collections = MutableStateFlow<List<CollectionItem>>(emptyList())
    val collections: StateFlow<List<CollectionItem>> = _collections.asStateFlow()

    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _loadResult = MutableStateFlow<LoadResult>(LoadResult.Success)
    val loadResult: StateFlow<LoadResult> = _loadResult.asStateFlow()

    private var currentPage = 1
    private var isLoading = false
    private var allLoading = false
    var selectedFeaturedButton = -1

    fun loadNextPage(query: String)
    {
        if (isLoading || allLoading) return
        isLoading = true
        viewModelScope.launch {
            try {
                val result = getPhotosByQueryUseCase(query, page = currentPage)
                if (result.total_results == 0) {
                    allLoading = true
                } else {
                    currentPage++
                    _photos.value += result.photos
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка при получении: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun loadPhotos(query: String)
    {
        _loadResult.value = LoadResult.Loading
        viewModelScope.launch {
            try {
                currentPage = 1
                isLoading = false
                allLoading = false
                _photos.value = emptyList()
                loadNextPage(query)
                Log.d("HomeViewModel", "Запрос отправлен")
                val result = getPhotosByQueryUseCase(query)
                Log.d("HomeViewModel", "Результат: ${result.total_results} коллекций")
                val total = result.photos.size
                _loadResult.value = LoadResult.Success
                if (total == 0) {
                    _isEmpty.value = true
                    _loadResult.value = LoadResult.Empty
                    return@launch
                }
                else {
                    _isEmpty.value = false
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
                currentPage = 1
                isLoading = false
                allLoading = false
                _photos.value = emptyList()
                loadNextCuratedPhotos()
                _loadResult.value = LoadResult.Success
                Log.d("HomeViewModel", "Запрос отправлен")
                val result = getCuratedPhotosUseCase()
                Log.d("HomeViewModel", "Результат: ${result.total_results} коллекций")
                _photos.value = result.photos
                val total = result.photos.size
                val loaded = mutableListOf<Photo>()
                if (total == 0) {
                    _isEmpty.value = true
                    _loadResult.value = LoadResult.Empty
                    return@launch
                }
                else {
                    _isEmpty.value = false
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
                val result = getCuratedPhotosUseCase(page = currentPage)
                if (result.total_results == 0) {
                    allLoading = true
                } else {
                    currentPage++
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
                val result = getFeaturedCollectionsUseCase()
                _collections.value = result
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка при получении: ${e.message}", e)
            }
        }
    }
}