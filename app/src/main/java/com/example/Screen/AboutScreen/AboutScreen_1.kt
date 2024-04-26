package com.example.Screen.AboutScreen

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
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.example.Screen.SelectContextScreen
import com.example.Screen.TakePictureScreen
import com.example.Utils.InactivityTimer
import com.example.empathymap.R

class AboutScreen_1 : AppCompatActivity(), RobotLifecycleCallbacks {

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
        supportActionBar?.hide()
        setContentView(R.layout.activity_about_screen1)
        QiSDK.register(this, this)
        inactivityTimer = InactivityTimer(this, 420000)
        val next_button: Button = findViewById(R.id.about_1_next_button)
        next_button.setOnClickListener {
            val intent = Intent(this, AboutScreen_2::class.java)
            startActivity(intent)
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {

        var ret = "Hi there! I'm PepperAi, your new AI companion and photographer. Let’s start our adventure and create memorable moments together!"

        val TheStringToSay = SayBuilder.with(qiContext)
            .withText(ret)
            .build()

        val animation_5: Animation = AnimationBuilder.with(qiContext)
            .withResources(R.raw.show_self_01).build()
        val animate_5: Animate = AnimateBuilder.with(qiContext)
            .withAnimation(animation_5)
            .build()

        animate_5.async().run()

        TheStringToSay.async().run()
    }
    override fun onRobotFocusLost() {}
    override fun onRobotFocusRefused(reason: String?) {}
}