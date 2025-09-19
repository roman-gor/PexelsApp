package com.gorman.testapp_innowise.data.mapper

import com.gorman.testapp_innowise.data.models.CollectionsResponse
import com.gorman.testapp_innowise.domain.models.Collection

fun CollectionsResponse.toDomain(): List<Collection> = collections.map { Collection(
        id = it.id,
        title = it.title,
        description = it.description,
        private = it.private,
        mediaCount = it.media_count,
        photosCount = it.photos_count,
        videosCount = it.videos_count
    )
}