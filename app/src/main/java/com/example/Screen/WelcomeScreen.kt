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


var messageList = mutableListOf<Pair<Int, String>>()

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
        messageList.clear()
        val about_button: Button = findViewById(R.id.About_button)
        val start_button: Button = findViewById(R.id.start_button)

        about_button.setOnClickListener {
            val intent = Intent(this, AboutScreen_1::class.java)
            startActivity(intent)
            finish()
        }
        start_button.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {
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
        val humansAroundFuture: Future<List<Human>>? = humanAwareness?.async()?.humansAround

        if (humansAroundFuture != null) {
            try {
                val humansAround = humansAroundFuture.get()
                if (humansAround.isNotEmpty()) {
                    timeoutOccurred = false
                    lastDetectedTime = System.currentTimeMillis()
                    PepperTalkWithDetectedHumans(humansAround)
                }else {
                    refocusIfTimeout()
                }
            } catch (e: Exception) {  }
        } else {
            refocusIfTimeout()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executorService.shutdownNow()
    }

    private fun PepperTalkWithDetectedHumans(humans: List<Human>) {
        val TalkWithHuman = SayBuilder.with(qiContext)
            .withText("Hey there! Want to have a friendly chat?")
            .build()
        val TalkWithMoreThanHuman = SayBuilder.with(qiContext)
            .withText("Hello folks! I'm feeling chatty. Who's up for a conversation?")
            .build()

        // TODO (Change the animation): Done
        val animation_1: Animation = AnimationBuilder.with(qiContext)
            .withResources(R.raw.hello_04).build()
        val animate_1: Animate = AnimateBuilder.with(qiContext)
            .withAnimation(animation_1)
            .build()
        when (humans.size) {
            0 ->    {}
            1 ->    {
                animate_1.async().run()
                TalkWithHuman.async().run()

            }
            else -> {
                animate_1.async().run()
                TalkWithMoreThanHuman.async().run()
            }
        }
        // TODO("sleep"): done
        retrieveCharacteristics(humans)
        if (AttentionHumanState == humans.size) {
            refocusIfTimeout()
            AttentionHumanState = 0
            return
        }
        refocusIfTimeout()
    }

    private fun retrieveCharacteristics(humans: List<Human>) {
        humans.forEachIndexed { index, human ->
            val attentionState: AttentionState = human.attention
            AttentionHumanState += if (attentionState == AttentionState.UNKNOWN) 1 else 0
        }
    }

    private fun refocusIfTimeout() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastDetectedTime
        val timeout:Long = 5000
        val extendedTimeout: Long = 10000

        synchronized(this) {

            if (elapsedTime >= timeout && !timeoutOccurred) {
                Log.d("callback: ", " detected ")

                timeoutOccurred = true

                executorService.schedule({
                    this.qiContext?.let { QiSDK.register(this, this) }
                }, timeout, TimeUnit.MILLISECONDS)


            } else if (elapsedTime >= extendedTimeout && timeoutOccurred) {
                Log.d("callback: ", "not detected ")

                executorService.schedule({
                    Log.d("callback: ", " reinit ")
                    this.qiContext?.let { QiSDK.register(this, this) }
                }, extendedTimeout, TimeUnit.MILLISECONDS)
            } else {

            }
        }
    }

}