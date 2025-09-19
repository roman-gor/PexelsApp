package com.gorman.testapp_innowise.domain.usecases

import com.gorman.testapp_innowise.domain.models.Collection
import com.gorman.testapp_innowise.domain.repository.PhotoRepository
import javax.inject.Inject

class GetFeaturedCollectionsUseCase @Inject constructor(
    private val repository: PhotoRepository
)
{
    suspend operator fun invoke(): List<Collection>
    {
        return repository.searchFeaturedCollections()
    }
}