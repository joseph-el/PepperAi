package com.example.Screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.humanawareness.HumanAwareness
import com.example.Screen.AboutScreen.AboutScreen_1
import com.example.Utils.InactivityTimer
import com.example.empathymap.R



class HomeScreen : AppCompatActivity() , RobotLifecycleCallbacks {
    private lateinit var inactivityTimer: InactivityTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(R.layout.activity_home_screen)
        QiSDK.register(this, this)
        inactivityTimer = InactivityTimer(this, 2 * 60 * 1000L)

        val start_chat_button: Button = findViewById(R.id.start_chat_button)
        val take_picture_button: Button = findViewById(R.id.take_picture_button)

        start_chat_button.setOnClickListener {
            val intent = Intent(this, SelectContextScreen::class.java)
            startActivity(intent)
        }
        take_picture_button.setOnClickListener {
            val intent = Intent(this, TakePictureScreen::class.java)
            startActivity(intent)
        }

    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        inactivityTimer.onUserInteraction()
    }
    override fun onDestroy() {
        inactivityTimer.stop()
        super.onDestroy()
    }



    // u can choice by something
    override fun onRobotFocusGained(qiContext: QiContext?) {
        // start chat or u can take pic
    }

    override fun onRobotFocusLost() {}

    override fun onRobotFocusRefused(reason: String?) {}
}