package com.gorman.testapp_innowise.data.mapper

import com.gorman.testapp_innowise.data.models.BookmarkImage
import com.gorman.testapp_innowise.domain.models.Bookmark

fun BookmarkImage.toDomain(): Bookmark = Bookmark(
    id = id,
    imageUrl = imageUrl,
    phName = phName
)