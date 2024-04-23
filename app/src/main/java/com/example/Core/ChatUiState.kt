package com.example.Core

sealed interface SummarizeUiState {

    object Initial : SummarizeUiState
    object Loading : SummarizeUiState

    data class Success(
        val outputText: String
    ) : SummarizeUiState

    data class Prompt(
        val prompt_edit: String
    ) : SummarizeUiState

    data class Error(
        val errorMessage: String
    ) : SummarizeUiState
}