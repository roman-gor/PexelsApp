package com.gorman.testapp_innowise.data.mapper

import com.gorman.testapp_innowise.data.models.PexelsResponse
import com.gorman.testapp_innowise.domain.models.Photo

fun PexelsResponse.toDomain(): List<Photo> = photos.map { Photo(
        it.id,
        it.url,
        it.photographer,
        it.photographer_url,
        it.photographer_id,
        it.src.toDomain())
}