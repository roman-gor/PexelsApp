package com.gorman.testapp_innowise.domain.repository

import com.gorman.testapp_innowise.domain.models.Collection
import com.gorman.testapp_innowise.domain.models.Photo

interface PhotoRepository {
    suspend fun search(query: String, page: Int): List<Photo>
    suspend fun searchCurated(page: Int): List<Photo>
    suspend fun searchFeaturedCollections(): List<Collection>
}