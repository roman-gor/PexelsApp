package com.gorman.testapp_innowise.data.mapper

import com.gorman.testapp_innowise.data.models.SrcData
import com.gorman.testapp_innowise.domain.models.Src

fun SrcData.toDomain(): Src = Src(
    original = original,
    large2x = large2x,
    large = large,
    medium = medium,
    small = small,
    portrait = portrait,
    landscape = landscape,
    tiny = tiny
)