package com.example.Screen.AboutScreen

import android.content.Intent
import android.os.Bundle
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
import com.example.Screen.HomeScreen
import com.example.Utils.InactivityTimer
import com.example.empathymap.R

class AboutScreen_5 : AppCompatActivity(), RobotLifecycleCallbacks {

    private lateinit var inactivityTimer: InactivityTimer

    override fun onUserInteraction() {
        super.onUserInteraction()
        inactivityTimer.onUserInteraction()
    }
    override fun onDestroy() {
        inactivityTimer.stop()
        super.onDestroy()
    }

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
        inactivityTimer = InactivityTimer(this, 420000)
        supportActionBar?.hide()
        setContentView(R.layout.activity_about_screen5)
        QiSDK.register(this, this)
        val next_button: Button = findViewById(R.id.go_to_home_icon)
        val left_button: Button = findViewById(R.id.left_button_about_5)

        next_button.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
        left_button.setOnClickListener {
            val intent = Intent(this, AboutScreen_4::class.java)
            startActivity(intent)
        }

    }
    override fun onRobotFocusGained(qiContext: QiContext?) {
        val ret = "I was crafted by a student from School 1337, aimed at enhancing how humans and robots interact. Excited to show you what I can do!"

        val TheStringToSay = SayBuilder.with(qiContext)
            .withText(ret)
            .build()

        val animation_1: Animation = AnimationBuilder.with(qiContext)
            .withResources(R.raw.funny_02).build()
        val animate_1: Animate = AnimateBuilder.with(qiContext)
            .withAnimation(animation_1)
            .build()
        TheStringToSay.async().run()
        animate_1.async().run()
    }
    override fun onRobotFocusLost() {}
    override fun onRobotFocusRefused(reason: String?) {}
}