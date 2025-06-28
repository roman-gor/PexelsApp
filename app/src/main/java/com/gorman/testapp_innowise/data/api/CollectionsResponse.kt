package com.gorman.testapp_innowise.data.api

data class CollectionsResponse(
    val page: Int,
    val per_page: Int,
    val total_results: Int,
    val collections: List<CollectionItem>,
    val next_page: String?
)

data class CollectionItem(
    val id: Int,
    val title: String,
    val description: String?,
    val private: Boolean,
    val media_count: Int,
    val photos_count: Int,
    val videos_count: Int,
    val thumbnail: Src?
)