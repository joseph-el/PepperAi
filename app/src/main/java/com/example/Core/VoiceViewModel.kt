package com.example.Core

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.configManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

class AppViewModel(private val stt: VoiceRecorder) : ViewModel() {
    private var _state = MutableStateFlow(AppState())
    private var client = OkHttpClient()
    val state = _state.asStateFlow()

    fun send(action: AppAction) {
        when (action) {

            AppAction.StartRecord -> {
                stt.startRecording()
            }
            is AppAction.SetUpHttpRequest -> {
                Log.d("voice_start", ": yes the path is : ${action.VoicePath}")

                viewModelScope.launch {
                    val file = File(action.VoicePath)

                    val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart(
                            "file", file.getName(),
                            RequestBody.create("audio/mp3".toMediaTypeOrNull(), file)
                        )
                        .addFormDataPart("some-field", "some-value")
                        .build()

                    val request = Request.Builder()
                        .url("http://${configManager.getValueByKey("ip-address")}:${configManager.getValueByKey("transcribe-port")}/${configManager.getValueByKey("transcribe-route")}")
                        .post(requestBody)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            // no recording voice should do nothing
                            try {
                                throw IOException("Request failed: ${e.localizedMessage}")
                            } catch (e: Exception) {
                                Log.d("EXECPTION:", " in ${e.localizedMessage}")
                                _state.value = _state.value.copy(
                                    display = null
                                )
                            }
                        }
                        override fun onResponse(call: Call, response: Response) {
                            try {
                                val jsonObject = JSONObject(response.body?.string())
                                var ret = jsonObject.getString("transcript")
                                _state.value = _state.value.copy(
                                    display = ret
                                )
                            } catch(e: Exception) {
                                _state.value = _state.value.copy(
                                    display = ""
                                )
                            }
                        }
                    })
                }
            }
        }
    }
}

data class AppState(
    val display: String? = ""
)

sealed class AppAction {
    object StartRecord : AppAction()

    data class SetUpHttpRequest(
        val VoicePath: String
    ): AppAction()

}