package com.example.Core

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Screen.ToShortMessage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class Artificial_intelligence_model() : ViewModel() {

    // Setting Api Key
    val generativeMultiModal = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = "AIzaSyDrjE857ITiU4y2i1Do7KEz_50dzFITnbY"
    )
    val generativeText = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = "AIzaSyDrjE857ITiU4y2i1Do7KEz_50dzFITnbY"
    )
    var _uiState: MutableStateFlow<SummarizeUiState> =  MutableStateFlow(SummarizeUiState.Initial)
    var uiState: StateFlow<SummarizeUiState> = _uiState.asStateFlow()

    fun resetUiStateToInitial() {
        _uiState.value = SummarizeUiState.Initial
    }

    fun summarize(inputText: String, images: List<Bitmap>, IsGetShortMessage: Boolean) {

        if (IsGetShortMessage) {
            var Brief = "Give the Brief of Brief of this text and answer me without Markdown, here the text: "
            GeneratingMessage(Brief + inputText, true)
            Log.d("The brief: ", "${ToShortMessage}")
            return
        }
        _uiState.value = SummarizeUiState.Loading

        val context = "Education: "

        /*
        val prompt = content {

            if (images.isNotEmpty()) {
                images.forEach { bitmap -> image(bitmap) }
            }
            text(userInputWithContext)
        }

         */

        GeneratingMessage("$context: $inputText", false)
    }
        fun GeneratingMessage(prompt: String, IsGetShortMessage: Boolean) {
            viewModelScope.launch {
            try {
                var fulltext = ""
                (generativeText).let { model ->
                    model.generateContentStream(prompt).collect { chuck ->
                        fulltext+= chuck.text?:""
                    }
                    if (!IsGetShortMessage)
                        _uiState.value = SummarizeUiState.Success(fulltext)
                    else
                        ToShortMessage = fulltext
                }
            } catch (e: Exception) {
                var dd = e.localizedMessage?: " " + " pepper Execp"
                if (!IsGetShortMessage)
                    _uiState.value = SummarizeUiState.Error(dd)
                else
                    ToShortMessage = dd
            }
        }

    }
}
