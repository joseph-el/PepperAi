package com.example.Screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.empathymap.R
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.`object`.human.AttentionState
import com.aldebaran.qi.sdk.`object`.human.Human
import com.aldebaran.qi.sdk.`object`.humanawareness.HumanAwareness
import java.util.concurrent.Future
import com.example.Screen.AboutScreen.AboutScreen_1
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit



class WelcomeScreen : AppCompatActivity(),RobotLifecycleCallbacks {
    private var humanAwareness: HumanAwareness? = null
    private var qiContext: QiContext? = null
    private var AttentionHumanState = 0
    private var lastDetectedTime: Long = 0
    private var timeoutOccurred: Boolean = false

    private val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

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
        setContentView(R.layout.activity_welcome_screen)
        QiSDK.register(this, this)

        Log.d("pepper_state:", "Start The Welcome Screen !!")


        val about_button: Button = findViewById(R.id.About_button)
        val start_button: Button = findViewById(R.id.start_button)

        about_button.setOnClickListener {
            val intent = Intent(this, AboutScreen_1::class.java)
            startActivity(intent)
        }
        start_button.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
        Log.d("pepper_state:", " ROBOT FOCUS !")

        this.qiContext = qiContext
        humanAwareness = qiContext?.humanAwareness
        findHumansAround()
    }

    override fun onRobotFocusLost() {
        this.qiContext = null
        this.humanAwareness = null
        AttentionHumanState = 0
    }

    override fun onRobotFocusRefused(reason: String?) {}

    private fun findHumansAround() {
        Log.d("pepper_state:", "iam trying to find people!!")
        val humansAroundFuture: Future<List<Human>>? = humanAwareness?.async()?.humansAround

        if (humansAroundFuture != null) {
            Log.d("pepper_state:", "i found human")

            try {
                val humansAround = humansAroundFuture.get()
                if (humansAround.isNotEmpty()) {
                    Log.d("pepper_state:", "yes its true peepper see u")

                    timeoutOccurred = false
                    lastDetectedTime = System.currentTimeMillis()
                    PepperTalkWithDetectedHumans(humansAround)
                }
            } catch (e: Exception) {  }
        } else {
            Log.d("pepper_state:", "No human founded!!")
            refocusIfTimeout()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executorService.shutdownNow()
    }

    private fun PepperTalkWithDetectedHumans(humans: List<Human>) {
        val TalkWithHuman = SayBuilder.with(qiContext)
            .withText("Heey comme to talk with me !!")
            .build()
        val TalkWithMoreThanHuman = SayBuilder.with(qiContext)
            .withText("Hello Pepople iam alone can someone Talk to me ?")
            .build()

        // TODO (Change the animation)
        val animation_1: Animation = AnimationBuilder.with(qiContext)
            .withResources(R.raw.attract_r05).build()
        val animate_1: Animate = AnimateBuilder.with(qiContext)
            .withAnimation(animation_1)
            .build()

        Log.d("pepper_state:", "retrieveCharacteristics state: human size : ${humans.size}")

        when (humans.size) {
            0 ->    {}
            1 ->    {
                animate_1.async().run()
                TimeUnit.SECONDS.sleep(4L)
                TalkWithHuman.async().run()
            }
            else -> {
                animate_1.async().run()
                TimeUnit.SECONDS.sleep(4L)
                TalkWithMoreThanHuman.async().run()
            }
        }
        // TODO("sleep")
        retrieveCharacteristics(humans)
        if (AttentionHumanState == humans.size) {
            Log.d("pepper_state:", "retrieveCharacteristics state: no person take care with pepper ")

            this.qiContext?.let { QiSDK.register(this, this) }
            AttentionHumanState = 0
        } else {
            Log.d("pepper_state:", "retrieveCharacteristics state: active user with pepper !!")
        }
    }

    private fun retrieveCharacteristics(humans: List<Human>) {

        Log.d("pepper_state:", "retrieveCharacteristics: ")

        humans.forEachIndexed { index, human ->
            val attentionState: AttentionState = human.attention
            AttentionHumanState += if (attentionState == AttentionState.UNKNOWN) 1 else 0
        }
        Log.d("pepper_state:", "retrieveCharacteristics state: ${AttentionHumanState} ")

    }


    private fun refocusIfTimeout() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastDetectedTime
        val timeout:Long = 5000 // 5 seconds timeout for rescan
        val extendedTimeout: Long = 15000 // 15 seconds timeout if people are detected


        Log.d("pepper_state:", " iam refocusIfTimeout")

        synchronized(this) {
            if (elapsedTime >= timeout && !timeoutOccurred) {
                timeoutOccurred = true
                // Schedule a task to find humans after a delay
                Log.d("pepper_state:", "in if ")
                executorService.schedule({
                    Log.d("pepper_state:", "rescan 1")
                    this.qiContext?.let { QiSDK.register(this, this) }
                }, timeout, TimeUnit.MILLISECONDS)
            } else if (elapsedTime >= extendedTimeout && timeoutOccurred) {
                // Schedule next scan with a longer delay if people were detected
                Log.d("pepper_state:", "else if ")
                executorService.schedule({
                    Log.d("pepper_state:", "rescan 2")

                    this.qiContext?.let { QiSDK.register(this, this) }
                }, extendedTimeout, TimeUnit.MILLISECONDS)
            }else {}
        }
    }

    private fun refocusIfTimeout__() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastDetectedTime
        val timeout = 50
        if (elapsedTime >= timeout && !timeoutOccurred) {
            timeoutOccurred = true

        }
    }
}