package com.gorman.testapp_innowise.domain.usecases

import com.gorman.testapp_innowise.domain.models.Photo
import com.gorman.testapp_innowise.domain.repository.PhotoRepository
import javax.inject.Inject

class GetPhotosByQueryUseCase @Inject constructor (
    private val repository: PhotoRepository
){
    suspend operator fun invoke(query: String, page: Int): List<Photo>
    {
        return repository.search(query, page)
    }
}