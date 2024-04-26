package com.example

/*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.translation.TranslationRequest
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.empathymap.R
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder
import com.aldebaran.qi.sdk.builder.ListenBuilder
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.`object`.conversation.Chat
import com.aldebaran.qi.sdk.`object`.conversation.ListenResult
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import java.util.concurrent.Future
import kotlin.time.Duration.Companion.seconds


/*
    import com.aldebaran.qi.sdk.QiContext
    import com.aldebaran.qi.sdk.builder.SayBuilder
    import com.aldebaran.qi.sdk.object.conversation.Phrase
    import com.aldebaran.qi.sdk.object.conversation.PhraseSet

    class PepperListener(context: QiContext) : QiContext(context) {
        private val sayBuilder: SayBuilder = SayBuilder.with(qiContext)
        private val phraseSet = PhraseSet("hey_pepper")
        private val wordSpotted = "Hey Pepper"

        fun startListening() {
            qiContext.conversation().async().startWatchingForPhrases(phraseSet)
                .thenConsume { phraseDetected ->
                    if (phraseDetected.phrase.equals(wordSpotted)) {
                        sayBuilder.withText("Yes, I'm listening. What would you like to say?")
                        qiContext.conversation().async().startRecognition()
                            .thenConsume { words ->
                                val allWords = words.joinToString(" ")
                                sayBuilder.withText("You said: $allWords")
                            }
                    }
                }
        }
    }



 */
class MainActivity : AppCompatActivity(), RobotLifecycleCallbacks {
    private lateinit var text_ss: TextView
    private lateinit var star_talk: Button
    private var qiContext: QiContext? = null


    val openai = OpenAI(
        token = "your-api-key",
        timeout = Timeout(socket = 60.seconds),
    )

    val request = TranscriptionRequest(
        audio = FileSource(name = "<filename>", source = audioSource),
        model = ModelId("whisper-1"),
    )
    val transcription = openai.transcription(request)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        supportActionBar?.hide()
        setContentView(R.layout.activity_speesh_to_text)

         text_ss =   findViewById(R.id.textView3)
         star_talk = findViewById(R.id.start_listen)

        QiSDK.register(this, this)

        star_talk.setOnClickListener {
            Log.d("Listen_Pepper:", "button touched")
            this.qiContext?.let { QiSDK.register(this, this) }
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        this.qiContext = qiContext

        val phraseSet = PhraseSetBuilder.with(qiContext)  // Assuming you need the context to build the PhraseSet
            .withTexts("Hello Pepper")  // Add more phrases as needed
            .build()


        val listen = ListenBuilder.with(qiContext)  // Use qiContext to create the Listen action
            .withPhraseSet(phraseSet)
            .build()


        listen.async().run().andThenConsume { listenResult ->
            val recognizedPhrase = listenResult.heardPhrase.text
            Log.d("Listen_Pepper:", "Heard phrase: $recognizedPhrase")


                // openAI

                // Update UI on the main thread
            runOnUiThread {

                // start recording
                text_ss.text = recognizedPhrase
            }
        }





        /*
        Log.d("Listen_Pepper:", "robot focus gained")
        val listen = ListenBuilder.with(qiContext)
            .build()

        listen.async().run().andThenConsume { listenResult ->
            val recognizedPhrase = listenResult.heardPhrase.text

            Log.d("Listen_Pepper:", "Heard phrase: $recognizedPhrase")

            runOnUiThread {
              //  text_ss.text = recognizedPhrase
            }
        }
         */

    }

    override fun onRobotFocusLost() {
        this.qiContext = null
    }

    override fun onRobotFocusRefused(reason: String?) {}
}

 */