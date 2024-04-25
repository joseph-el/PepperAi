package com.example.Core

sealed interface SummarizeUiState {

    object Initial : SummarizeUiState
    object Loading : SummarizeUiState

    data class Success(
        val outputText: String
    ) : SummarizeUiState

    data class PepperSay(
        val string_to_say: String
    ) : SummarizeUiState

    data class Error(
        val errorMessage: String
    ) : SummarizeUiState
}