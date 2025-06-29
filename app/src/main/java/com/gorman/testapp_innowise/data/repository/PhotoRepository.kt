package com.gorman.testapp_innowise.data.repository

import com.gorman.testapp_innowise.data.models.CollectionItem
import com.gorman.testapp_innowise.data.api.PexelsAPI
import com.gorman.testapp_innowise.data.models.PexelsResponse
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val api: PexelsAPI
) {
    suspend fun search(query: String, page: Int = 1, perPage: Int = 30): PexelsResponse {
        return api.searchPhotos(query = query, page = page, perPage = perPage)
    }

    suspend fun searchCurated(page: Int = 1, perPage: Int = 30): PexelsResponse {
        return api.searchCuratedPhotos(page = page, perPage = perPage)
    }

    suspend fun searchFeaturedCollections(): List<CollectionItem> {
        return api.searchFeaturedCollections().collections
    }
}