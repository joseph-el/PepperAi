package com.example.Core

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


var category: Category? = null
class Artificial_intelligence_model() : ViewModel() {

    private val generativeMultiModal = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = "AIzaSyDrjE857ITiU4y2i1Do7KEz_50dzFITnbY"
    )

    private val generativeText = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = "AIzaSyDrjE857ITiU4y2i1Do7KEz_50dzFITnbY"
    )

    private val _uiState = MutableStateFlow<SummarizeUiState>(SummarizeUiState.Initial)
    val uiState: StateFlow<SummarizeUiState> = _uiState.asStateFlow()

    fun resetUiStateToInitial() {
        _uiState.value = SummarizeUiState.Initial
    }

    fun summarize(inputText: String, isGetShortMessage: Boolean) {
        if (isGetShortMessage) {
            val briefPrompt = prepareBriefPrompt(inputText)
            generateMessage(briefPrompt, true)
            return
        }

        val prompt = prepareFullPrompt(inputText)
        _uiState.value = SummarizeUiState.Loading
        generateMessage(prompt, false)
    }

    private fun prepareBriefPrompt(inputText: String): String =
        "As Pepper robot, based on the detailed response provided by Pepper: '[$inputText]', " +
                "please generate a concise summary that captures the essential points and do not change the speaker's form. " +
                "Please respond in plain text without using any Markdown formatting or links."
    private fun prepareFullPrompt(inputText: String): String {
        val categoryInfo = getCategoryInfo(category as Category)
        return "As Pepper robot, based on the description of the [$category] category, which includes [$categoryInfo]," +
                "please provide an answer to the following user query: [$inputText]. " +
                "If the query does not align with the category description, please utilize creative reasoning to offer a smart and relevant response. " +
                "Please respond in plain text without using any Markdown formatting or links."
    }

    fun generateMessage(prompt: String, IsGetShortMessage: Boolean) {
        viewModelScope.launch {
            try {
                var fulltext = ""
                (generativeText).let { model ->
                    model.generateContentStream(prompt).collect { chuck ->
                        fulltext+= chuck.text?:""
                    }
                    if (!IsGetShortMessage)
                        _uiState.value = SummarizeUiState.Success(fulltext)
                    else {
                        _uiState.value = SummarizeUiState.Prompt(fulltext)
                    }
                }
            } catch (e: Exception) {
                if (!IsGetShortMessage)
                    _uiState.value = SummarizeUiState.Error(e.localizedMessage)
                else
                    _uiState.value = SummarizeUiState.Prompt(e.localizedMessage)
            }
        }
    }
}

