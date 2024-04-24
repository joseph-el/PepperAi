package com.example.Screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.example.Core.AppAction
import com.example.Core.AppViewModel
import com.example.Core.Artificial_intelligence_model
import com.example.Core.Category
import com.example.Core.SummarizeUiState
import com.example.Core.category
import com.example.Core.getCategoryInfo
import com.example.Screen.AboutScreen.AboutScreen_1
import com.example.Utils.InactivityTimer
import com.example.Utils.RealSpeechToText
import com.razzaghimahdi78.dotsloading.linear.LoadingWavy
import com.example.empathymap.R
import kotlinx.coroutines.launch

val IS_ROBOT: Int =      (1 shl 0)
val IS_LAST_ITEM: Int =  (1 shl 1)
val IS_NOT_ROBOT: Int =  (1 shl 2)
val IS_IS_LOADING: Int = (1 shl 3)

class ChatScreen : AppCompatActivity() , RobotLifecycleCallbacks {

    private lateinit var inactivityTimer: InactivityTimer
    private lateinit var scrollView: ScrollView
    private lateinit var chatLayout: LinearLayout
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var Back_button: ImageButton
    private lateinit var voice_button: ImageButton
    private lateinit var toolbarTitle: TextView
    private val SviewModel: Artificial_intelligence_model by viewModels()
    private lateinit var stt: RealSpeechToText
    private lateinit var viewModel: AppViewModel
    private var messageList = mutableListOf<Pair<Int, String>>()
    private var EditMessageState by mutableStateOf(false)
    private var pressed by mutableStateOf(false)
    private var qiContext: QiContext? = null
    var ToShortMessage:String = ""


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
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        supportActionBar?.hide()
        setContentView(R.layout.activity_chat_screen)
        QiSDK.register(this, this)
        inactivityTimer = InactivityTimer(this, 420000)


        toolbarTitle = findViewById(R.id.toolbarTitle)
        stt = RealSpeechToText(this)
        viewModel = AppViewModel(stt)
        Back_button = findViewById(R.id.backButton)

        chatLayout   = findViewById(R.id.chat_messages_layout)
        scrollView   = findViewById(R.id.chat_layout)
        messageInput = findViewById(R.id.messageInput)
        sendButton   = findViewById(R.id.sendButton)
        voice_button = findViewById(R.id.voiceButton)
        toolbarTitle.text = getCategoryInfo(category as Category).name

        /* SetupVoice: Activity*/

        var permission: Boolean = ContextCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        val launcher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            permission = granted
        }

        voice_button.setOnTouchListener { view, motionEvent ->

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Toast.makeText(this, "Pepper Listen!", Toast.LENGTH_LONG).show()
                        getDrawable(R.drawable.mini_voice)
                        try {
                        if (permission) {
                            pressed = true
                            viewModel.send(AppAction.StartRecord)
                        } else {
                            launcher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                        }catch (e: Exception) {
                            Toast.makeText(this, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        try {
                        viewModel.send(AppAction.EndRecord)
                        pressed = false
                    }catch (e: Exception) {
                        Toast.makeText(this, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
                    }
                        true
                        view.performClick()
                    }

                    else -> false
                }
        }

        Back_button.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }

        sendButton.setOnClickListener {
            if (EditMessageState) {
                try { removeLastAddedNodes(true) } catch (e: Exception) { }
                EditMessageState = false
            }
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                UpdateNode(   IS_NOT_ROBOT   )
                messageList.add( (IS_LAST_ITEM or IS_NOT_ROBOT)  to  message)
                addMessage(message, (IS_LAST_ITEM or IS_NOT_ROBOT))
                scrollView.post {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }
                SviewModel.summarize(message, false)
                messageInput.text.clear()
            }
        }

        lifecycleScope.launchWhenStarted {
            SviewModel.uiState.collect { newState ->
                renderChatContent(newState)
            }
        }

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (state.display.isNotEmpty()) {
                    messageList.add( (IS_LAST_ITEM or IS_NOT_ROBOT)  to  state.display)
                    SviewModel.summarize(state.display, false)
                    SviewModel.resetUiStateToInitial()
                }
            }
        }
    }

    /* List Methods */

    private fun renderChatContent(newState: SummarizeUiState) {

        var IsSay = false

        var state =  when (newState) {
            is SummarizeUiState.Initial -> {false}
            is SummarizeUiState.Loading -> {true}
            is SummarizeUiState.Prompt  -> {
                ToShortMessage = newState.prompt_edit
                IsSay = true
                true
            }
            is SummarizeUiState.Success -> {
                UpdateNode( IS_ROBOT )
                messageList.add((IS_ROBOT or IS_LAST_ITEM) to newState.outputText)
                SviewModel.summarize(newState.outputText, true)
                SviewModel.resetUiStateToInitial()
                false
            }
            is SummarizeUiState.Error   -> {
                UpdateNode(IS_ROBOT)
                messageList.add((IS_ROBOT or IS_LAST_ITEM) to newState.errorMessage)
                SviewModel.resetUiStateToInitial()
                false
            }
        }
        if (!IsSay) {
            if (!state) {
                chatLayout.removeAllViews()
                messageList.forEach { (flag, message) ->
                    addMessage(message, flag)
                }
            } else {
                addMessage("", IS_IS_LOADING)
            }
            scrollView.post {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        } else {
            this.qiContext?.let { QiSDK.register(this, this) }
        }

    }

    private fun addMessage(message: String, flag: Int) {

        val chatMessagesLayout = findViewById<LinearLayout>(R.id.chat_messages_layout)
        var messageBubbleLayout = if (flag and IS_NOT_ROBOT != 0) R.layout.user_chat_bubbles
        else if(flag and IS_ROBOT != 0) R.layout.robot_chat_bubble else R.layout.robot_chat_generating
        val messageBubbleView = layoutInflater.inflate(messageBubbleLayout, null)

        if (flag and (IS_NOT_ROBOT or IS_ROBOT) != 0) {

            val visibleOrNotLayout = messageBubbleView.findViewById<LinearLayout>(R.id.visible_or_not)
            visibleOrNotLayout.visibility = if (flag and IS_LAST_ITEM != 0) View.VISIBLE else View.GONE
            var buttonEffect: ImageView = visibleOrNotLayout.findViewById(if ((flag and IS_ROBOT) != 0 ) R.id.refresh_button
            else R.id.edit_messageIcon)
            buttonEffect.setOnClickListener {
                if (flag and IS_ROBOT != 0) {
                    removeLastAddedNodes(false)
                    SviewModel.summarize( messageList[findLastIndexOfFlag(IS_NOT_ROBOT)].second, false)
                }
                else {
                    messageInput.setText( messageList[findLastIndexOfFlag(IS_NOT_ROBOT)].second )
                    EditMessageState = true
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(messageInput, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = if (flag and IS_NOT_ROBOT != 0) Gravity.END else Gravity.START
        messageBubbleView.layoutParams = layoutParams

        if (flag and (IS_ROBOT or IS_NOT_ROBOT) != 0) {
            val messageTextView = messageBubbleView.findViewById<TextView>(R.id.text_message)
            messageTextView.text = message
        }
        chatMessagesLayout.addView(messageBubbleView)
    }

    private fun UpdateNode(flag: Int) {
        if (messageList.isNotEmpty() ) {
            var lastItemIndex = -1
            for (i in messageList.indices.reversed()) {
                if (messageList[i].first and flag != 0 && messageList[i].first and IS_LAST_ITEM != 0) {
                    lastItemIndex = i
                    break
                }
            }
            if (lastItemIndex != -1) {
                messageList[lastItemIndex] = Pair(
                    messageList[lastItemIndex].first and IS_LAST_ITEM.inv(),
                    messageList[lastItemIndex].second
                )
            }
        }
    }

    private fun findLastIndexOfFlag(flag: Int): Int {

        for (i in messageList.indices.reversed()) {
            val flags = messageList[i].first
            if (flags and flag != 0 && flags and IS_LAST_ITEM != 0 ) {
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
        if (!RemoveTwo)
            return
        val secondLastIndex = messageList.size - 1
        if (secondLastIndex >= 0) {
            messageList.removeAt(secondLastIndex)
        }
    }

    fun markdownToPlainText(markdown: String): String {
        return markdown
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
            .replace(Regex("\\*(.*?)\\*"), "$1")
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")
            .replace(Regex("`{1,3}(.*?)`{1,3}"), "$1")
            .replace(Regex("\n"), " ")
    }

    override fun onRobotFocusGained(qiContext: QiContext?) {

        this.qiContext = qiContext

        val Make_Robot_Say = SayBuilder.with(qiContext)
            .withText(markdownToPlainText(ToShortMessage))
            .build()
        Make_Robot_Say.run()
        ToShortMessage = ""
    }
    override fun onRobotFocusLost() {
        this.qiContext = null
    }
    override fun onRobotFocusRefused(reason: String?) {}
}