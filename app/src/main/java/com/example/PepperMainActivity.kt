package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.Core.SummarizeUiState
import com.example.Screen.ChatScreen
import com.example.Screen.SelectContextScreen
import com.example.Screen.WelcomeScreen
import com.example.empathymap.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


/*
fun main() {
    val audioUrl = "https://github.com/AssemblyAI-Examples/audio-examples/raw/main/20230607_me_canadian_wildfires.mp3"
    val url = "http://10.0.2.2:5000/transcribe"

    val client = OkHttpClient()

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("audio_url", audioUrl)
        .build()

    Log.d("speech: ", "build resuest body success ")


    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    Log.d("speech: ", "builder request body ")


    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("speech: ", "client call ")
            e.printStackTrace()
        }


        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            } else {
                Log.d("speech: ", "resposne : ${response.body?.string()}")

            }
        }
    })
}

 */


class PepperMainActivity : AppCompatActivity() {
    private lateinit var voice_button: Button

    /*
    private val client = OkHttpClient()

    fun run(postBody: String) {

        postBody.trimMargin()
        val request = Request.Builder()
            .url("http://10.0.2.2:3000/api/v1/prediction/4db24f95-4bd3-4189-b44f-3a3ea72d9c37")
            .post(postBody.toRequestBody(JSON))
            .build()

        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("TheVar", ": ${e.localizedMessage}")
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
        }catch (e: Exception) {

        }

    }

    companion object {
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    }

     */








    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            requestWindowFeature(Window.FEATURE_NO_TITLE)
            this.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
            supportActionBar?.hide()
            setContentView(R.layout.activity_pepper_main)





















        /*
        try {

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = "{\"question\": \"$prompt\"}".toRequestBody(mediaType)
            val request = Request.Builder()
                .url("http://10.0.2.2:3000/api/v1/prediction/4db24f95-4bd3-4189-b44f-3a3ea72d9c37")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .post(requestBody)
                .build()

            // This will run the network operation on an IO-optimized thread
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")


                Log.d("TheVar", ": ${response.header("Server")}")
                Log.d("TheVar",  ": ${response.header("Date")}")
                Log.d("TheVar", ": ${response.headers("Vary")}")

                Log.d("TheVar", ": ${response.body?.string()}")
            }



        } catch (e: IOException) {
            Log.d("TheVar", ": ${e.localizedMessage}")

        }


         */








            /*
            Handler().postDelayed({
                val intent = Intent(this, SelectContextScreen::class.java)
                startActivity(intent)
            }, 3000)

             */





        }














            /*

            val outputDir = Environment.getExternalStorageDirectory().resolve("recordings")
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            Log.d("YourTag", "Output directory path: ${outputDir.absolutePath}")
            val voiceRecorder = VoiceRecorder(outputDir)

            var permission: Boolean =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> permission = granted }

            if (!permission) {
                launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            voice_button.setOnClickListener {

                voiceRecorder.startRecording()

                voice_button.isEnabled = false

                while (voiceRecorder.isRecording) {}

                voice_button.isEnabled = true
                Log.d("YourTag", "Recording DONE")

                val recordedFilePath = voiceRecorder.getRecordedFilePath()


            }


             */






// Create an output directory for the recorded files
// Create an instance of VoiceRecorder




// Delete the recorded file
            //voiceRecorder.deleteRecordedFile()
            //Log.d("YourTag", "Recorded file deleted.")






    }
