package com.example.Screen.AboutScreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.example.Screen.HomeScreen
import com.example.Screen.SelectContextScreen
import com.example.Screen.TakePictureScreen
import com.example.Screen.WelcomeScreen
import com.example.Utils.InactivityTimer
import com.example.empathymap.R
import com.example.empathymap.databinding.ActivityAboutScreen4Binding

class AboutScreen_4 : AppCompatActivity(), RobotLifecycleCallbacks {

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

        inactivityTimer = InactivityTimer(this, 2 * 60 * 1000L)
        supportActionBar?.hide()
        setContentView(R.layout.activity_about_screen4)

        QiSDK.register(this, this)
        val next_button: Button = findViewById(R.id.next_button_about_4)
        val left_button: Button = findViewById(R.id.left_button_about_4)

        next_button.setOnClickListener {
            val intent = Intent(this, AboutScreen_5::class.java)
            startActivity(intent)
        }
        left_button.setOnClickListener {
            val intent = Intent(this, AboutScreen_3::class.java)
            startActivity(intent)
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        val ret = "Need to talk? I’m here for conversations, help, or just a casual chat. Let’s discuss whatever’s on your mind!"
        val TheStringToSay = SayBuilder.with(qiContext)
            .withText(ret)
            .build()

        val animation_1: Animation = AnimationBuilder.with(qiContext)
            .withResources(R.raw.hello_a001).build()
        val animate_1: Animate = AnimateBuilder.with(qiContext)
            .withAnimation(animation_1)
            .build()
        TheStringToSay.async().run()
        animate_1.async().run()
    }
    override fun onRobotFocusLost() {}
    override fun onRobotFocusRefused(reason: String?) {}
}