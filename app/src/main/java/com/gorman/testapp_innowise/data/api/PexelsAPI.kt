package com.gorman.testapp_innowise.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Headers

interface PexelsAPI {

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