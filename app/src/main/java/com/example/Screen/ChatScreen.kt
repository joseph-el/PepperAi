package com.example.Screen

import  android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.ListenBuilder
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.actuation.Animation
import com.aldebaran.qi.sdk.`object`.conversation.Listen
import com.aldebaran.qi.sdk.`object`.conversation.ListenResult
import com.aldebaran.qi.sdk.`object`.conversation.PhraseSet
import com.example.Core.AppAction
import com.example.Core.AppViewModel
import com.example.Core.Artificial_intelligence_model
import com.example.Core.SummarizeUiState
import com.example.Core.getCategoryInfo
import com.example.Utils.InactivityTimer
import com.example.empathymap.R
import com.example.Core.Category
import com.example.Core.VoiceRecorder
import com.example.Core.category
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import java.io.File

val IS_ROBOT: Int = (1 shl 0)
val IS_LAST_ITEM: Int = (1 shl 1)
val IS_NOT_ROBOT: Int = (1 shl 2)
val IS_IS_LOADING: Int = (1 shl 3)
var USER_LOADING: Int = (1 shl 4)

val MAKE_ROBOT_LISTEN: Int = (1 shl 4)
val MAKE_ROBOT_SAY: Int    = (1 shl 5)
val MAKE_THINKING: Int     = (1 shl 6)
val MAKE_ROBOT_SAD: Int = (1 shl 7)

class ChatScreen : AppCompatActivity(), RobotLifecycleCallbacks {

    private lateinit var inactivityTimer: InactivityTimer
    private lateinit var scrollView: ScrollView
    private lateinit var chatLayout: LinearLayout
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var Back_button: ImageButton
    private lateinit var Voice_Gif: GifImageView
    private lateinit var ImageButtonError: ImageView

    private lateinit var toolbarTitle: TextView
    private val SviewModel: Artificial_intelligence_model by viewModels()
    private var messageList = mutableListOf<Pair<Int, String>>()
    private var EditMessageState by mutableStateOf(false)
    private var qiContext: QiContext? = null
    var ToShortMessage: String = ""
    private var PepperState: Int = MAKE_ROBOT_LISTEN
    private lateinit var Recorder: VoiceRecorder
    private lateinit var viewModel: AppViewModel
    private lateinit var outputDir: File
    private lateinit var mediaPlayerStart:MediaPlayer
    private lateinit var mediaPlayerStop: MediaPlayer


    fun startListen() {
        Voice_Gif.isEnabled = false
        this.qiContext?.let { QiSDK.unregister(this, this) }
        mediaPlayerStart.start()
        Voice_Gif.setImageResource(R.drawable.final_edited)
        captureAndTranscribe {
            mediaPlayerStop.start()
        }
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
            setContentView(R.layout.activity_chat_screen)
            inactivityTimer = InactivityTimer(this, 420000)

            /* setup layout ids */
            toolbarTitle = findViewById(R.id.toolbarTitle)
            Back_button = findViewById(R.id.backButton)
            chatLayout = findViewById(R.id.chat_messages_layout)
            scrollView = findViewById(R.id.chat_layout)
            messageInput = findViewById(R.id.messageInput)
            sendButton = findViewById(R.id.sendButton)
            Voice_Gif = findViewById(R.id.gifButton)
            mediaPlayerStart = MediaPlayer.create(this, R.raw.siri_start)
            mediaPlayerStop  = MediaPlayer.create(this, R.raw.siri_stop)
            toolbarTitle.text = padStringIfNeeded(getCategoryInfo(category as Category).name)


            /* setup Voice */
            outputDir = File(Environment.getExternalStorageDirectory(), "recordings")
            Recorder = VoiceRecorder(outputDir)
            viewModel = AppViewModel(Recorder)

            QiSDK.register(this, this) /* make robot focus on first lunch */

            Voice_Gif.setOnClickListener {
                startListen()
            }

            /*  Done funcs */

            Back_button.setOnClickListener {
                val intent = Intent(this, HomeScreen::class.java)
                startActivity(intent)
            }

            sendButton.setOnClickListener {
                if (EditMessageState) {
                    try {
                        Voice_Gif.visibility = View.VISIBLE
                        messageInput.visibility = View.GONE
                        sendButton.visibility = View.GONE
                        removeLastAddedNodes(true)
                    } catch (e: Exception) {}
                    EditMessageState = false
                }
                val message = messageInput.text.toString().trim()
                if (message.isNotEmpty()) {
                    UpdateNode(IS_NOT_ROBOT)
                    messageList.add((IS_LAST_ITEM or IS_NOT_ROBOT) to message)
                    addMessage(message, (IS_LAST_ITEM or IS_NOT_ROBOT))
                    scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                    SviewModel.summarize(message)
                    messageInput.text.clear()
                }
            }

            lifecycleScope.launchWhenStarted { SviewModel.uiState.collect { newState -> renderChatContent(newState) } }

            lifecycleScope.launch {
                viewModel.state.collect { state ->
                    Voice_Gif.setImageResource(R.drawable.pepper_listen_icon) // siri_voice
                    Recorder.deleteRecordedFile()
                    if (state.display?.isNotEmpty() == true) {
                        UpdateNode(IS_NOT_ROBOT)
                        messageList.add((IS_LAST_ITEM or IS_NOT_ROBOT) to state.display)
                        SviewModel.summarize(state.display)
                        MakeRobotThinking()
                    } else if (state.display?.isNotEmpty() == false) {
                        Init()
                        SviewModel.resetUiStateToInitial()
                        Log.d("EXECPTION:", "IAM HERE TO RE INIT SCREEN!! ")
                    } else {
                        Log.d("EXECPTION:", "HTTP ERROR!! ")
                        SetupErrorGif()
                    }
                }
            }

    }

    private fun MakeRobotThinking() {
        PepperState = MAKE_THINKING
        SviewModel.setUiStateToLoading()
        this.qiContext?.let { QiSDK.register(this, this) }
    }

    private  fun SetupErrorGif() {

        chatLayout.removeAllViews()

        ImageButtonError = ImageView(this)
        ImageButtonError.id = View.generateViewId()
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(370, 37, 0, 0) // left, top, right, bottom
        ImageButtonError.layoutParams = params
        ImageButtonError.setImageResource(R.drawable.error_robot_failed)
        chatLayout.addView(ImageButtonError)
        sendButton.visibility = View.GONE
        messageInput.visibility = View.GONE
        Voice_Gif.visibility = View.GONE
        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        PepperState = MAKE_ROBOT_SAD
        this.qiContext?.let { QiSDK.register(this, this) }
        ImageButtonError.setOnClickListener {
            Voice_Gif.visibility = View.VISIBLE
            ImageButtonError.visibility = View.GONE
            updateScreen()
            Init()
        }
    }

    private fun captureAndTranscribe(onRecordingFinished: () -> Unit) {
        viewModel.send(AppAction.StartRecord)

        Log.d("voice_start", ": the voice is start recording")

        Recorder.onRecordingFinishedListener = {

            Log.d("voice_start", ": the voice is STOP recording")

            var voice_path = Recorder.getRecordedFilePath()!!
            viewModel.send(AppAction.SetUpHttpRequest(voice_path))
            onRecordingFinished()
        }
    }

    /* List Methods */
    private fun renderChatContent(newState: SummarizeUiState) {
        var IsSay = false
        var UserLoading = false
        var state =
            when (newState) {
                is SummarizeUiState.Initial -> {  false  }
                is SummarizeUiState.Loading -> {  true   }
                is SummarizeUiState.Success -> {
                    Init()
                    UpdateNode(IS_ROBOT)
                    messageList.add((IS_ROBOT or IS_LAST_ITEM) to newState.outputText)
                    ToShortMessage = newState.outputText
                    IsSay = true // updateded TODO
                    SviewModel.resetUiStateToInitial()
                    true
                }
                is SummarizeUiState.Error -> {
                    SetupErrorGif()
                    Log.d("EXECPTION:", "exec: ${newState.errorMessage}")
                    SviewModel.resetUiStateToInitial()
                    false
                }
            }
        if (!IsSay) {
            if (!state) {
                updateScreen()
            } else {
                addMessage("", if (UserLoading) USER_LOADING else IS_IS_LOADING)
            }
            scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        } else {
            PepperState = MAKE_ROBOT_SAY
            this.qiContext?.let { QiSDK.unregister(this, this) }
            this.qiContext?.let { QiSDK.register(this, this) }
        }
    }

    private fun updateScreen() {
        chatLayout.removeAllViews()
        messageList.forEach { (flag, message) -> addMessage(message, flag) }
    }

    private fun Init() {
        Voice_Gif.isEnabled = true
        ToShortMessage = ""
        PepperState = MAKE_ROBOT_LISTEN
        this.qiContext?.let { QiSDK.register(this, this) }
    }

    private fun addMessage(message: String, flag: Int) {

        val chatMessagesLayout = findViewById<LinearLayout>(R.id.chat_messages_layout)
        var messageBubbleLayout =
            if (flag and IS_NOT_ROBOT != 0) R.layout.user_chat_bubbles
            else if (flag and IS_ROBOT != 0) R.layout.robot_chat_bubble else { R.layout.robot_chat_generating  }
        val messageBubbleView = layoutInflater.inflate(messageBubbleLayout, null)

        if (flag and (IS_NOT_ROBOT or IS_ROBOT) != 0) {

            val visibleOrNotLayout = messageBubbleView.findViewById<LinearLayout>(R.id.visible_or_not)
            visibleOrNotLayout.visibility = if (flag and IS_LAST_ITEM != 0) View.VISIBLE else View.GONE
            var buttonEffect: ImageView =
                visibleOrNotLayout.findViewById(if ((flag and IS_ROBOT) != 0) R.id.refresh_button else R.id.edit_messageIcon)

            buttonEffect.setOnClickListener {
                this.qiContext?.let { QiSDK.unregister(this, this) }
                if (flag and IS_ROBOT != 0) {
                    removeLastAddedNodes(false)
                    SviewModel.summarize(messageList[findLastIndexOfFlag(IS_NOT_ROBOT)].second)
                } else {
                    Voice_Gif.visibility = View.GONE
                    messageInput.visibility = View.VISIBLE
                    sendButton.visibility = View.VISIBLE
                    messageInput.setText(messageList[findLastIndexOfFlag(IS_NOT_ROBOT)].second)
                    EditMessageState = true
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(messageInput, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = if (flag and IS_NOT_ROBOT != 0 || flag and USER_LOADING != 0) Gravity.END else Gravity.START
        messageBubbleView.layoutParams = layoutParams

        if (flag and (IS_ROBOT or IS_NOT_ROBOT) != 0) {
            val messageTextView = messageBubbleView.findViewById<TextView>(R.id.text_message)
            messageTextView.text = message
        }
        chatMessagesLayout.addView(messageBubbleView)
    }
    private fun UpdateNode(flag: Int) {
        if (messageList.isNotEmpty()) {
            var lastItemIndex = -1
            for (i in messageList.indices.reversed()) {
                if (messageList[i].first and flag != 0 && messageList[i].first and IS_LAST_ITEM != 0) {
                    lastItemIndex = i
                    break
                }
            }
            if (lastItemIndex != -1) {
                messageList[lastItemIndex] =
                    Pair(messageList[lastItemIndex].first and IS_LAST_ITEM.inv(), messageList[lastItemIndex].second)
            }
        }
    }
    private fun padStringIfNeeded(categoryName: String): String {
        val maxSize = "Education and Learning".length
        return if (categoryName.length < maxSize) {
            val spacesToAdd = maxSize - categoryName.length
            " ".repeat(spacesToAdd) + categoryName
        } else {
            categoryName
        }
    }
    private fun findLastIndexOfFlag(flag: Int): Int {

        for (i in messageList.indices.reversed()) {
            val flags = messageList[i].first
            if (flags and flag != 0 && flags and IS_LAST_ITEM != 0) {
                return i
            }
        }
        return -1
    }
    private fun removeLastAddedNodes(RemoveTwo: Boolean) {
        val lastIndex = messageList.size - 1
        if (lastIndex >= 0) {
            messageList.removeAt(lastIndex)
        }
        if (!RemoveTwo) return
        val secondLastIndex = messageList.size - 1
        if (secondLastIndex >= 0) {
            messageList.removeAt(secondLastIndex)
        }
    }
    private fun markdownToPlainText(markdown: String): String {
        return markdown
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
            .replace(Regex("\\*(.*?)\\*"), "$1")
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")
            .replace(Regex("`{1,3}(.*?)`{1,3}"), "$1")
            .replace(Regex("\n"), " ")
    }


    /* Robot funcs */

    override fun onRobotFocusGained(qiContext: QiContext?) {
        this.qiContext = qiContext

        if ( PepperState == MAKE_ROBOT_SAY ) {
            val Make_Robot_Say = SayBuilder.with(qiContext).withText(markdownToPlainText(ToShortMessage)).build()
            Make_Robot_Say.run()
            runOnUiThread {
                Init()
            }
            Log.d("pepper:", " re init")
        }

        else if (PepperState == MAKE_ROBOT_LISTEN) {

            val phraseSet: PhraseSet = PhraseSetBuilder.with(qiContext)
                .withTexts("hello")
                .build()
            val listen: Listen = ListenBuilder.with(qiContext)
                .withPhraseSet(phraseSet)
                .build()

            val listenResult: ListenResult = listen.run()

            Log.d("pepper:", " listen word: ${listenResult.heardPhrase.text}")

            if  (listenResult.heardPhrase.text.toLowerCase() == "Hey Pepper"){
                runOnUiThread {
                    startListen()
                }
            }
            PepperState = 0
        } else if (PepperState == MAKE_THINKING) {
            // this not Perfect good to use media
            val ret = "on it"
            val TheStringToSay = SayBuilder.with(qiContext)
                .withText(ret)
                .build()
            val animation_5: Animation = AnimationBuilder.with(qiContext)
                .withResources(R.raw.show_head_01).build()
            val animate_5: Animate = AnimateBuilder.with(qiContext)
                .withAnimation(animation_5)
                .build()
            animate_5.async().run()
            TheStringToSay.async().run()
            PepperState = 0
        }
        else if (PepperState == MAKE_ROBOT_SAD) {
            val ret = "Oooooh !"
            val TheStringToSay = SayBuilder.with(qiContext)
                .withText(ret)
                .build()
            val animation_5: Animation = AnimationBuilder.with(qiContext)
                .withResources(R.raw.sadreaction_02).build()
            val animate_5: Animate = AnimateBuilder.with(qiContext)
                .withAnimation(animation_5)
                .build()
            animate_5.async().run()
            TheStringToSay.async().run()
            PepperState = 0
        }
    }

    override fun onRobotFocusLost() {
        this.qiContext = null
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        inactivityTimer.onUserInteraction()
    }

    override fun onDestroy() {
        inactivityTimer.stop()
        super.onDestroy()
    }

    override fun onRobotFocusRefused(reason: String?) {}

}




/*
    private fun captureAndTranscribe_() {

        viewModel.send(AppAction.StartRecord)
        // remove the button of start listen and display gif

        Log.d("voice_start", ": the voice is start recording")

        while (Recorder.isRecording) {} // loop until finish

        Log.d("voice_start", ": the voice is STOP recording")

        var voice_path = Recorder.getRecordedFilePath()!!

        viewModel.send(AppAction.SetUpHttpRequest(voice_path))
    }


     */


// Get OpenAI key
// Hey pepper and button 2 listen
// Gif for Error
// Gif for Listing
// Gif for

/*

                if (!Showing)
            Showing = true
        else
            Showing = false

        if (Showing) {
            Voice_Gif.visibility = View.VISIBLE
            messageInput.visibility = View.GONE
            sendButton.visibility = View.GONE
        } else {
            Voice_Gif.visibility = View.GONE
            messageInput.visibility = View.VISIBLE
            sendButton.visibility = View.VISIBLE
        }

 */

// old
/*
        voice_button.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Toast.makeText(this, "Pepper Listen!", Toast.LENGTH_LONG).show()
                    try {
                        if (permission) {
                            viewModel.send(AppAction.StartRecord)
                        } else {
                            launcher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    try {
                        viewModel.send(AppAction.EndRecord)
                    } catch (e: Exception) {
                        Toast.makeText(this, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
                    }
                    view.performClick()
                    true
                }
                else -> false
            }
        }



 */