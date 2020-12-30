package me.mking.currentconditions.domain.usecases

sealed class DataResult<T> {
    data class Error<T>(val reason: String) : DataResult<T>()
    data class Success<T>(val data: T) : DataResult<T>()
}