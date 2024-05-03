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
import java.util.concurrent.TimeUnit

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
