package com.gorman.testapp_innowise.ui

sealed class LoadResult {
    object Loading : LoadResult()
    object Success : LoadResult()
    object Empty : LoadResult()
    data class Error(val exception: Throwable) : LoadResult()
}