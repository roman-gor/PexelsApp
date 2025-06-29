package com.gorman.testapp_innowise.data.repository

import com.gorman.testapp_innowise.data.api.CollectionItem
import com.gorman.testapp_innowise.data.api.PexelsAPI
import com.gorman.testapp_innowise.data.api.PexelsResponse
import com.gorman.testapp_innowise.data.api.Photo
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