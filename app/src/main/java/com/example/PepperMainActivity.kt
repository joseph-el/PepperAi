package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.Core.ConfigManager
import com.example.empathymap.R
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.Screen.WelcomeScreen

lateinit var configManager: ConfigManager

class PepperMainActivity : AppCompatActivity() {

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

            launchPermissionRequest()
            configManager = ConfigManager("config.json")
            if (configManager.isFileFound()) {
                Handler().postDelayed({
                    val intent = Intent(this, WelcomeScreen::class.java)
                    startActivity(intent)
                }, 3000)
            }else {
                Toast.makeText(this, "PepperAi: Failed To Load Config.json", Toast.LENGTH_LONG).show()
            }
        }
    private fun launchPermissionRequest() {
        var recordPermission: Boolean = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        var storagePermission: Boolean = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        val launcher_1 = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> recordPermission = granted }
        val launcher_2 = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> storagePermission = granted }

        if (!recordPermission)  launcher_1.launch(Manifest.permission.RECORD_AUDIO)
        if (!storagePermission) launcher_2.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}

/*
Example of config.json:
        {
          "ip-address": "10.32.80.93",
          "port":"3000",
          "url": "/api/v1/prediction/64439968-436e-4ad5-9b39-c1b3f016b977"
          "transcribe-port": "5000"
          "transcribe-url": "/transcribe"
        }
 */











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







// Create an output directory for the recorded files
// Create an instance of VoiceRecorder




// Delete the recorded file
//voiceRecorder.deleteRecordedFile()
//Log.d("YourTag", "Recorded file deleted.")






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
