package com.gorman.testapp_innowise.data.useCase

import com.gorman.testapp_innowise.data.models.CollectionItem
import com.gorman.testapp_innowise.data.repository.PhotoRepository
import javax.inject.Inject

class GetFeaturedCollectionsUseCase @Inject constructor(
    private val repository: PhotoRepository
)
{
    suspend operator fun invoke(): List<CollectionItem>
    {
        return repository.searchFeaturedCollections()
    }
}