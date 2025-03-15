package ru.netology.listapp.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.netology.listapp.R

sealed interface ApiError {
    data object NetworkError : ApiError
    data object UnknownError : ApiError
}

@Composable
fun getApiErrorTexts(): (ApiError) -> String {
    val unknownError = stringResource(R.string.unknown_error)
    val networkError = stringResource(R.string.network_error)
    return {
        when (it) {
            ApiError.NetworkError -> networkError
            else -> unknownError
        }
    }
}
