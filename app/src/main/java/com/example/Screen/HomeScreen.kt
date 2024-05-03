package com.example.Screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.example.Utils.InactivityTimer
import com.example.empathymap.R

class HomeScreen : AppCompatActivity() , RobotLifecycleCallbacks {
    private lateinit var inactivityTimer: InactivityTimer
    private lateinit var start_chat_button: Button
    private lateinit var take_picture_button: Button

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

        inactivityTimer = InactivityTimer(this, 420000)

        start_chat_button = findViewById(R.id.start_chat_button)
        take_picture_button = findViewById(R.id.take_picture_button)

        start_chat_button.setOnClickListener {
            val intent = Intent(this, SelectContextScreen::class.java)
            startActivity(intent)
            finish()
        }
        take_picture_button.setOnClickListener {

            val intent = Intent(this, TakePictureScreen::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        Log.d("callback: ", "iam in destroy fun ")
        inactivityTimer.stop()
        super.onDestroy()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        inactivityTimer.onUserInteraction()
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        val ret = "Chose one of the provided button and let's start"

        val TheStringToSay = SayBuilder.with(qiContext)
            .withText(ret)
            .build()

        val animation_6: Animation = AnimationBuilder.with(qiContext)
            .withResources(R.raw.show_tablet_06).build()
        val animate_6: Animate = AnimateBuilder.with(qiContext)
            .withAnimation(animation_6)
            .build()
        runOnUiThread {
            start_chat_button.isEnabled = false
            take_picture_button.isEnabled = false
        }
        animate_6.async().run()
        TheStringToSay.run()
        runOnUiThread {
            start_chat_button.isEnabled = true
            take_picture_button.isEnabled = true
        }
    }

    override fun onRobotFocusLost() {}

    override fun onRobotFocusRefused(reason: String?) {}
}