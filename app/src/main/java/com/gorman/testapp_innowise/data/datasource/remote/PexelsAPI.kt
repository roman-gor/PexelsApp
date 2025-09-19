package com.gorman.testapp_innowise.data.datasource.remote

import com.gorman.testapp_innowise.data.models.CollectionsResponse
import com.gorman.testapp_innowise.data.models.PexelsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PexelsAPI {

    @GET("v1/curated")
    suspend fun searchCuratedPhotos(
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): PexelsResponse

    @GET("v1/search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): PexelsResponse

    @GET("v1/collections/featured")
    suspend fun searchFeaturedCollections(
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1
    ): CollectionsResponse
}