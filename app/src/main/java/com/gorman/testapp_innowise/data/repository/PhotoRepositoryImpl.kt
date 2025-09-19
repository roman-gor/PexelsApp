package com.gorman.testapp_innowise.data.repository

import com.gorman.testapp_innowise.data.datasource.remote.PexelsAPI
import com.gorman.testapp_innowise.data.mapper.toDomain
import com.gorman.testapp_innowise.domain.models.Collection
import com.gorman.testapp_innowise.domain.models.Photo
import com.gorman.testapp_innowise.domain.repository.PhotoRepository
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val api: PexelsAPI
): PhotoRepository {
    override suspend fun search(query: String, page: Int): List<Photo> {
        return api.searchPhotos(query = query, page = page).toDomain()
    }

    override suspend fun searchCurated(page: Int): List<Photo> {
        return api.searchCuratedPhotos(page = page).toDomain()
    }

    override suspend fun searchFeaturedCollections(): List<Collection> {
        return api.searchFeaturedCollections().toDomain()
    }
}

