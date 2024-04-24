package com.example.Screen

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
import android.widget.ScrollView
import android.widget.Toast


import com.aldebaran.qi.sdk.Qi

import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.camera.TakePicture
import com.example.Utils.EmailHelper

private const val TAG = "TakePictureActivity"

class TakePictureScreen : AppCompatActivity(), RobotLifecycleCallbacks {

    private var qiContext: QiContext? = null
    private var pictureBitmap: Bitmap? = null
    private lateinit var pictureView: ImageView
    private lateinit var takePictureButton: Button
    private lateinit var sendMail: Button
    private lateinit var user_name: EditText
    private lateinit var user_mail: EditText
    private lateinit var retakePicturesImageView: ImageView

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
        setContentView(R.layout.activity_take_picture_screen)

        retakePicturesImageView = findViewById<ImageView>(R.id.retake_pictures)
        retakePicturesImageView.visibility = View.INVISIBLE


        sendMail = findViewById(R.id.send_mail_button)
        user_name = findViewById(R.id.Name_of_user)
        user_mail = findViewById(R.id.userMail)
        pictureView = findViewById(R.id.imageView2)
        takePictureButton = findViewById(R.id.take_pic_button)
        takePictureButton.isEnabled = false

        takePictureButton.setOnClickListener {
            takePic()
            Log.d("yes iam here", "dddd")
            retakePicturesImageView.visibility = View.VISIBLE
        }

        retakePicturesImageView.setOnClickListener {
            Log.d("yes iam here", "dddd")
            DestroyAll()
            retakePicturesImageView.visibility = View.INVISIBLE
            takePic()
        }

        sendMail.setOnClickListener {
            val name = user_mail.text.toString().trim()
            val mail = user_mail.text.toString().trim()

            if (name.isNotEmpty() && mail.isNotEmpty()  ) {
                if (pictureView == null || pictureView.drawable == null ) {
                    Toast.makeText(this, "PepperAi: You Need To Take Pictures First", Toast.LENGTH_LONG).show()
                } else {
                    try {
                        var EmailManager = EmailHelper(this)
                        pictureBitmap?.let { it1 ->
                            EmailManager.sendEmailWithImage(mail, name, "Pepper pictures: ",
                                it1
                            )
                        }
                        DestroyAll()
                        Toast.makeText(this, "PepperAi: Pictures send by success!", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "PepperAi: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        QiSDK.register(this, this)
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

        val takePictureFuture = TakePictureBuilder.with(qiContext).buildAsync()

        takePictureFuture.andThenCompose<TimestampedImageHandle>(Qi.onUiThread<TakePicture, Future<TimestampedImageHandle>> { takePicture ->

            takePictureButton.isEnabled = false

            takePicture.async().run()

        }).andThenConsume { timestampedImageHandle ->
            //Consume take picture action when it's ready
            Log.i(TAG, "Picture taken")
            // get picture
            val encodedImageHandle = timestampedImageHandle.image

            val encodedImage = encodedImageHandle.value
            Log.i(TAG, "PICTURE RECEIVED!")

            runOnUiThread {
                takePictureButton.isEnabled = true
            }

            val buffer = encodedImage.data
            buffer.rewind()
            val pictureBufferSize = buffer.remaining()
            val pictureArray = ByteArray(pictureBufferSize)
            buffer.get(pictureArray)

            Log.i(TAG, "PICTURE RECEIVED! ($pictureBufferSize Bytes)")
            pictureBitmap = BitmapFactory.decodeByteArray(pictureArray, 0, pictureBufferSize)
            runOnUiThread { pictureView.setImageBitmap(pictureBitmap) }
        }
    }
    override fun onDestroy() {
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        this.qiContext = qiContext

        runOnUiThread { takePictureButton.isEnabled = true }

        val say = SayBuilder.with(qiContext)
            .withText("I can take pictures. Press the button to try!")
            .build()
        // add animation //
        say.run()
    }

    override fun onRobotFocusLost() {
        this.qiContext = null
    }
    override fun onRobotFocusRefused(reason: String?) {}


}