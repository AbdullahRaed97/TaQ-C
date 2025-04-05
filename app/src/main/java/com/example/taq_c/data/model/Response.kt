package com.example.taq_c.data.model

sealed class Response<out T> {
    object Loading : Response<Nothing>()
    data class Success<out T>(val data: T) : Response<T>()
    data class Failure(val exception: Throwable) : Response<Nothing>()
}