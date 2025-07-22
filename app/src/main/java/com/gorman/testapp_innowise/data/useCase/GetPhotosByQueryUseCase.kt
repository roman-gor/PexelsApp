package com.gorman.testapp_innowise.data.useCase

import com.gorman.testapp_innowise.data.models.PexelsResponse
import com.gorman.testapp_innowise.data.repository.PhotoRepository
import javax.inject.Inject

class GetPhotosByQueryUseCase @Inject constructor (
    private val repository: PhotoRepository
){
    suspend operator fun invoke(query: String, page: Int = 1): PexelsResponse
    {
        return repository.search(query, page)
    }
}