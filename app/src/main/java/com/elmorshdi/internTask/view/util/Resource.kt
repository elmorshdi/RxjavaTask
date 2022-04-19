package com.elmorshdi.internTask.view.util

sealed class Resource<out T> {
     data class  Success<out T>(val data: T) : Resource<T>()
    data class  Error(val error : Failure) : Resource<Nothing>()
    object  Loading: Resource<Nothing>()
}
sealed class Failure {
    object NetworkConnectionError : Failure()
    sealed class ServerError : Failure(){
        object NotFound : ServerError()
        object ServiceUnavailable : ServerError()
        object AccessDenied : ServerError()
    }
    object UnknownError : Failure()
}