package com.example.Screen.AboutScreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class AboutScreen_3 : AppCompatActivity() , RobotLifecycleCallbacks {

    private lateinit var inactivityTimer: InactivityTimer

    override fun onUserInteraction() {
        super.onUserInteraction()
        inactivityTimer.onUserInteraction()
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
        setContentView(R.layout.activity_about_screen3)
        QiSDK.register(this, this)

        val next_button: Button = findViewById(R.id.next_button_about_3)
        val left_button: Button = findViewById(R.id.left_button_about_3)

        next_button.setOnClickListener {

            val intent = Intent(this, AboutScreen_4::class.java)
            startActivity(intent)
            finish()
        }
        left_button.setOnClickListener {
            val intent = Intent(this, AboutScreen_2::class.java)
            startActivity(intent)
            finish()
        }


    }

    override fun onDestroy() {
        Log.d("callback: ", "iam in destroy fun ")
        inactivityTimer.stop()
        super.onDestroy()
    }


    override fun onRobotFocusGained(qiContext: QiContext?) {
        val ret = "I’m not just a robot, I’m a friend-maker! If there are people nearby, I’ll invite them over so we can all have some fun together."
        val TheStringToSay = SayBuilder.with(qiContext)
            .withText(ret)
            .build()

        val animation_1: Animation = AnimationBuilder.with(qiContext)
            .withResources(R.raw.show_tablet_04).build()
        val animate_1: Animate = AnimateBuilder.with(qiContext)
            .withAnimation(animation_1)
            .build()
        TheStringToSay.async().run()
        animate_1.async().run()
    }
    override fun onRobotFocusLost() {}
    override fun onRobotFocusRefused(reason: String?) {}
}