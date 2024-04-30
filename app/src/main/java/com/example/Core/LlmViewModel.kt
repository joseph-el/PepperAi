package com.example.Core


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.configManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

var category: Category? = null

class Artificial_intelligence_model() : ViewModel() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val _uiState = MutableStateFlow<SummarizeUiState>(SummarizeUiState.Initial)
    val uiState: StateFlow<SummarizeUiState> = _uiState.asStateFlow()

    fun resetUiStateToInitial() {
        _uiState.value = SummarizeUiState.Initial
    }

    fun setUiStateToLoading() {
        _uiState.value = SummarizeUiState.Loading
    }

    fun summarize(inputText: String) {

        try {
            val jsonObject = JSONObject()
            jsonObject.put("question", prepareFullPrompt(inputText))
            _uiState.value = SummarizeUiState.Loading
            generateMessage(jsonObject.toString())
        } catch (e:Exception) {
            _uiState.value = SummarizeUiState.Error("${e.localizedMessage}")
        }
    }

    private fun prepareFullPrompt(inputText: String): String {
        val categoryInfo = getCategoryInfo(category as Category)
        return "As a Pepper robot, for the category [$category] with details [$categoryInfo], " +
                "please provide a concise and relevant response to: [$inputText]. " +
                "If the query does not align with the category, utilize creative reasoning. " +
                "Ensure the response is short (focus on the response should be shorted for make pepper robot say it easy), plain text, and free from formatting or links."
    }

    fun generateMessage(prompt: String) {
        viewModelScope.launch {
                try {
                    val request = Request.Builder()
                        .url("http://${configManager.getValueByKey("ip-address")}:${configManager.getValueByKey("port")}${configManager.getValueByKey("route")}")
                        .post(prompt.toRequestBody(JSON))
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            try {
                                throw IOException("Request failed: ${e.localizedMessage}")
                            } catch (e: Exception) {
                                _uiState.value = SummarizeUiState.Error(e.localizedMessage)
                            }
                        }
                        override fun onResponse(call: Call, response: Response) {
                            try {
                                val jsonObject = JSONObject(response.body?.string())
                                val ret = jsonObject.getString("text")
                                _uiState.value = SummarizeUiState.Success(ret)
                            } catch (e:Exception) {
                                _uiState.value = SummarizeUiState.Error("${e.localizedMessage}")
                            }
                        }
                    })
                }   catch (e: Exception) {
                        _uiState.value = SummarizeUiState.Error(e.localizedMessage)
                }
        }
    }
    companion object {
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    }
}




























/*

          private fun prepareBriefPrompt(inputText: String): String =
        "As Pepper robot, based on the detailed response provided by Pepper: '[$inputText]', " +
                "please generate a concise summary that captures the essential points and do not change the speaker's form. " +
                "Please respond in plain text without using any Markdown formatting or links."
    private fun prepareFullPrompt_(inputText: String): String {
        val categoryInfo = getCategoryInfo(category as Category)
        return "As Pepper robot, based on the description of the [$category] category, which includes [$categoryInfo]," +
                "please provide an answer to the following user query: [$inputText]. " +
                "If the query does not align with the category description, please utilize creative reasoning to offer a smart and relevant response. " +
                "Please respond in plain text without using any Markdown formatting or links."
    }



        fun generateMessage(prompt: String, isGetShortMessage: Boolean) {

            val client = OkHttpClient()

            /*
            val url = "http://10.0.2.2:3000/api/v1/prediction/4db24f95-4bd3-4189-b44f-3a3ea72d9c37"
            val json = "{\"question\": \"$prompt\"}"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = json.toRequestBody(mediaType)


             */


            fun sendPostRequest(userName: String, password: String) {
                val json = """
        {
            "question": "Hey, how are you?"
        }
    """.trimIndent()

                val url = "http://10.0.2.2:3000/api/v1/prediction/4db24f95-4bd3-4189-b44f-3a3ea72d9c37"
                val body = RequestBody.create("application/json".toMediaTypeOrNull(), json)
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                OkHttpClient().newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        } else {
                            IOException("Unexpected code ${response.body?.string()}")
                            //println(response.body?.string())
                        }
                    }
                })
            }


            viewModelScope.launch {
                try {
                    sendPostRequest("", "")
                    // Now you can use responseBody as needed
                    // _uiState.value = SummarizeUiState.Success(responseBody)
                } catch (e: IOException) {
                    _uiState.value = SummarizeUiState.Error(e.localizedMessage)
                }

            }



        /*
        viewModelScope.launch {
            val client = OkHttpClient()

            /*
            curl http://localhost:3000/api/v1/prediction/4db24f95-4bd3-4189-b44f-3a3ea72d9c37 \
                -X POST \
                -d '{"question": "Hey, how are you?"}' \
                -H "Content-Type: application/json"
             */

            val MEDIA_TYPE = "application/json".toMediaType()

            val requestBody = "{\"question\": \"$prompt\"}".toRequestBody(MEDIA_TYPE)

            val request = Request.Builder()
                .url("http://10.32.80.93:3000/api/v1/prediction/4db24f95-4bd3-4189-b44f-3a3ea72d9c37")
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()



            try {
                withContext(Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        val responseBody = response.body!!.string()

                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }

                        if (!isGetShortMessage)
                            _uiState.value = SummarizeUiState.Success(responseBody)
                        else
                            _uiState.value = SummarizeUiState.PepperSay(responseBody)
                    }
                }
            } catch (e: Exception) {

                if (!isGetShortMessage)
                    _uiState.value = SummarizeUiState.Error(e.localizedMessage)
                else
                    _uiState.value = SummarizeUiState.PepperSay(e.localizedMessage)
            }
        }

         */
    }

    /*
    fun generateMessage(prompt: String, isGetShortMessage: Boolean) {
        viewModelScope.launch {
            // Formulate the curl command
            val curlCommand = arrayOf(
                "curl",
                "http://localhost:3000/api/v1/prediction/4db24f95-4bd3-4189-b44f-3a3ea72d9c37",
                "-X", "POST",
                "-d", "{\"question\": \"$prompt\"}",
                "-H", "Content-Type: application/json"
            )
            // Execute the curl command and handle output
            try {
                withContext(Dispatchers.IO) { // Perform network I/O in IO Dispatcher
                    val process = ProcessBuilder(*curlCommand).start()
                    val output = process.inputStream.bufferedReader().use(BufferedReader::readText)
                    val error = process.errorStream.bufferedReader().use(BufferedReader::readText)

                    if (process.waitFor() == 0 && error.isEmpty()) {
                        if (!isGetShortMessage)
                            _uiState.value = SummarizeUiState.Success(output)
                        else
                            _uiState.value = SummarizeUiState.PepperSay(output)
                    } else {
                        throw Exception(error.ifEmpty { "Unknown error during API call" })
                    }
                }
            } catch (e: Exception) {
                if (!isGetShortMessage)
                    _uiState.value = SummarizeUiState.Error(e.localizedMessage)
                else
                    _uiState.value = SummarizeUiState.PepperSay(e.localizedMessage)
            }
        }
    }


     */

    /*
    fun generateMessage(prompt: String, IsGetShortMessage: Boolean) {
        viewModelScope.launch {

            // TODO ("get by curl response ")
            try {
                var fulltext = ""
                (generativeText).let { model ->
                    model.generateContentStream(prompt).collect { chuck ->
                        fulltext+= chuck.text?:""
                    }
                    if (!IsGetShortMessage)
                        _uiState.value = SummarizeUiState.Success(fulltext)
                    else {
                        _uiState.value = SummarizeUiState.PepperSay(fulltext)
                    }
                }
            } catch (e: Exception) {
                if (!IsGetShortMessage)
                    _uiState.value = SummarizeUiState.Error(e.localizedMessage)
                else
                    _uiState.value = SummarizeUiState.PepperSay(e.localizedMessage)
            }
        }
    }

     */
 */