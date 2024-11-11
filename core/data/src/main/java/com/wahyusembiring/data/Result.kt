package com.wahyusembiring.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

sealed class Result<T> {
    class Loading<T> : Result<T>()
    data class Success<T>(val data: T) : Result<T>()
    class Error<T>(val throwable: Throwable) : Result<T>()
}