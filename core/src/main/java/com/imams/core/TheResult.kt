package com.imams.core

sealed class TheResult<T> {
    data class Success<T>(val data: T): TheResult<T>()
    data class Error<T>(val code: String, val message: String): TheResult<T>()
}

fun <T> Exception.toError(): TheResult<T> = TheResult.Error(
    code = this.message.orEmpty(), message = this.cause?.message.orEmpty()
)