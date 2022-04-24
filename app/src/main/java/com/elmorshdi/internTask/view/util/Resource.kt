package com.elmorshdi.internTask.view.util

import okhttp3.ResponseBody
import org.json.JSONObject
import java.net.HttpURLConnection

sealed class UiState {
    data class NetworkError(val errorMessage: String) : UiState()
    object Success : UiState()
    data class ValidationError(val error: Error) : UiState()
    object Loading : UiState()
    object Empty : UiState()
}

sealed class Error {
    object PasswordNotValid : Error()
    object EmailNotValid : Error()
    object NameNotValid : Error()
    object PriceNotValid : Error()
    object QuantityNotValid : Error()
}


fun getNetworkError(code: Int, errorBody: ResponseBody): UiState {
    return when (code) {
        // not found
        HttpURLConnection.HTTP_NOT_FOUND -> UiState.NetworkError("Not Found")
        // access denied
        HttpURLConnection.HTTP_FORBIDDEN -> UiState.NetworkError("AccessDenied")
        // unavailable service
        HttpURLConnection.HTTP_UNAVAILABLE -> UiState.NetworkError("ServiceUnavailable")
        //Server ERROR
        HttpURLConnection.HTTP_INTERNAL_ERROR -> UiState.NetworkError("Server Error try later ")
        // HTTP CLIENT TIMEOUT
        HttpURLConnection.HTTP_CLIENT_TIMEOUT -> UiState.NetworkError("Request Timeout Check your connection")
        // Validation Error
        422 -> UiState.NetworkError(getError(errorBody))
        // all the others will be treated as unknown error
        else -> UiState.NetworkError("An Error Occur")
    }
}
//To get error message from JsonBody
private fun getError(response: ResponseBody): String {
    val jObjError = JSONObject(response.string())
    return jObjError.getString("message")
}