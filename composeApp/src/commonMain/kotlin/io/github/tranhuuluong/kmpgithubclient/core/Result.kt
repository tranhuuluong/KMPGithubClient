package io.github.tranhuuluong.kmpgithubclient.core

typealias DataState<T> = Result.DataState<T>
typealias DataStateSuccess<T> = Result.DataState.Success<T>
typealias DataStateError = Result.DataState.Error
typealias StateLoading = Result.State.Loading
typealias StateIdle = Result.State.Idle

sealed interface Result<out T> {

    sealed interface State : Result<Nothing> {
        data object Loading : State
        data object Idle : State
    }

    sealed interface DataState<out T> : Result<T> {
        data class Error(val exception: Throwable) : DataState<Nothing>
        data class Success<T>(val data: T) : DataState<T>
    }
}