package com.example.Screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.example.empathymap.R

import android.util.Log

import android.widget.Button
import android.widget.ImageView

import com.aldebaran.qi.sdk.builder.TakePictureBuilder
import com.aldebaran.qi.sdk.`object`.image.TimestampedImageHandle

import com.aldebaran.qi.Future



import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Toast


import com.aldebaran.qi.sdk.Qi
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder

import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.`object`.camera.TakePicture
import com.example.Utils.InactivityTimer

import java.util.concurrent.TimeUnit

private const val TAG = "TakePictureActivity"

class TakePictureScreen : AppCompatActivity(), RobotLifecycleCallbacks {

    private lateinit var inactivityTimer: InactivityTimer
    private var qiContext: QiContext? = null
    private var pictureBitmap: Bitmap? = null
    private lateinit var pictureView: ImageView
    private lateinit var takePictureButton: Button
    private lateinit var sendMail: Button
    private lateinit var backButton: Button
    private lateinit var user_name: EditText
    private lateinit var user_mail: EditText
    private lateinit var retakePicturesImageView: ImageView


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
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        inactivityTimer = InactivityTimer(this, 420000)
        supportActionBar?.hide()
        setContentView(R.layout.activity_take_picture_screen)

        retakePicturesImageView = findViewById<ImageView>(R.id.retake_pictures)
        retakePicturesImageView.visibility = View.INVISIBLE

        backButton = findViewById(R.id.Backbutton)
        sendMail = findViewById(R.id.send_mail_button)
        user_name = findViewById(R.id.Name_of_user)
        user_mail = findViewById(R.id.userMail)
        pictureView = findViewById(R.id.imageView2)
        takePictureButton = findViewById(R.id.take_pic_button)

        takePictureButton.setOnClickListener {
            DestroyAll()
            takePictureButton.isEnabled = false
            QiSDK.register(this, this)
            retakePicturesImageView.visibility = View.VISIBLE
        }
        backButton.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
        retakePicturesImageView.setOnClickListener {
            DestroyAll()
            retakePicturesImageView.visibility = View.INVISIBLE
            takePictureButton.isEnabled = true
        }

        sendMail.setOnClickListener {
            val name = user_mail.text.toString().trim()
            val mail = user_mail.text.toString().trim()

            if (name.isNotEmpty() && mail.isNotEmpty()  ) {
                if (pictureView == null || pictureView.drawable == null ) {
                    Toast.makeText(this, "PepperAi: You Need To Take Pictures First", Toast.LENGTH_LONG).show()
                } else {
                    try {

                        /*
                        var EmailManager = EmailHelper(this)
                        pictureBitmap?.let { it1 ->
                            EmailManager.sendEmailWithImage(mail, name, "Pepper pictures: ",
                                it1
                            )
                        }
                         */
                        DestroyAll()
                        Toast.makeText(this, "PepperAi: Pictures send by success!", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "PepperAi: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    private fun DestroyAll() {
        if (qiContext == null) {
            return
        }
        retakePicturesImageView.visibility = View.INVISIBLE
        pictureBitmap?.let{
            it.recycle()
            pictureBitmap = null
            pictureView.setImageBitmap(null)
        }
    }

    private fun takePic() {

        if (qiContext == null) {
            return
        }
        takePictureButton.isEnabled = false
        val takePictureFuture = TakePictureBuilder.with(qiContext).buildAsync()

        takePictureFuture.andThenCompose<TimestampedImageHandle>(Qi.onUiThread<TakePicture, Future<TimestampedImageHandle>> { takePicture ->
            takePicture.async().run()

        }).andThenConsume { timestampedImageHandle ->
            val encodedImageHandle = timestampedImageHandle.image
            val encodedImage = encodedImageHandle.value
            val buffer = encodedImage.data
            buffer.rewind()
            val pictureBufferSize = buffer.remaining()
            val pictureArray = ByteArray(pictureBufferSize)
            buffer.get(pictureArray)

            pictureBitmap = BitmapFactory.decodeByteArray(pictureArray, 0, pictureBufferSize)
            runOnUiThread { pictureView.setImageBitmap(pictureBitmap) }
        }

    }

    override fun onDestroy() {
        inactivityTimer.stop()
        super.onDestroy()
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        this.qiContext = qiContext

        runOnUiThread { takePictureButton.isEnabled = false }

        val say1 = SayBuilder.with(qiContext)
            .withText("Make Smile")
            .build()

        val say2 = SayBuilder.with(qiContext)
            .withText("three")
            .build()

        val say3 = SayBuilder.with(qiContext)
            .withText("Two")
            .build()

        val say4 = SayBuilder.with(qiContext)
            .withText("one")
            .build()

        val say5 = SayBuilder.with(qiContext)
            .withText("Yeaaaah i take it! Amazing ")
            .build()

        val animation_1: Animation = AnimationBuilder.with(qiContext)
            .withResources(R.raw.show_body_02).build()
        val animate_1: Animate = AnimateBuilder.with(qiContext)
            .withAnimation(animation_1)
            .build()

        say1.async().run()
        TimeUnit.SECONDS.sleep(2L)
        say2.async().run()
        TimeUnit.SECONDS.sleep(2L)
        say3.async().run()
        TimeUnit.SECONDS.sleep(2L)
        say4.async().run()
        TimeUnit.SECONDS.sleep(2L)
        animate_1.async().run()
        takePic()
        say5.async().run()
    }

    override fun onRobotFocusLost() {
        this.qiContext = null
    }
    override fun onRobotFocusRefused(reason: String?) {}


}