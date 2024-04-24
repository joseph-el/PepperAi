package com.example.Core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Utils.SpeechToText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(private val stt: SpeechToText) : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            stt.text.collect { result ->
                send(AppAction.Update(result))
            }
        }
    }
    fun send(action: AppAction) {
        when (action) {
            AppAction.StartRecord -> {
                stt.start()
            }
            AppAction.EndRecord -> {
                stt.stop()
                viewModelScope.launch {
                    delay(3000)
                    _state.value = _state.value.copy(
                        display = ""
                    )
                }
            }
            is AppAction.Update -> {
                _state.value = _state.value.copy(
                    display = _state.value.display + action.text
                )
            }
        }
    }
}

data class AppState(
    val display: String = ""
)

sealed class AppAction {
    object StartRecord : AppAction()
    object EndRecord : AppAction()
    data class Update(val text: String): AppAction()
}
