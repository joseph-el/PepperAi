package com.example.Screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.Core.Category
import com.example.Core.category
import com.example.Utils.InactivityTimer
import com.example.empathymap.R

class SelectContextScreen : AppCompatActivity() {

    private lateinit var inactivityTimer: InactivityTimer
    private lateinit var educationButton: Button
    private lateinit var adventureButton: Button
    private lateinit var comedyButton: Button
    private lateinit var scienceButton: Button
    private lateinit var foodButton: Button
    private lateinit var knowledgeButton: Button
    private lateinit var healthButton: Button
    private lateinit var bookButton: Button
    private lateinit var startChatButton: Button
    private lateinit var textView: TextView

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

        setContentView(R.layout.activity_select_context_screen)
        educationButton = findViewById(R.id.education_button)
        adventureButton = findViewById(R.id.adventure_button)
        comedyButton = findViewById(R.id.comedy_button)
        scienceButton = findViewById(R.id.science_button)
        foodButton = findViewById(R.id.food_button)
        knowledgeButton = findViewById(R.id.knowledge_button)
        healthButton = findViewById(R.id.health_button)
        bookButton = findViewById(R.id.book_button)
        startChatButton = findViewById(R.id.start__chat_button)
        textView = findViewById(R.id.textView2)


        educationButton.setOnClickListener {
            category = Category.EDUCATION_AND_LEARNING
            updateTextView("Education and Learning")
        }

        adventureButton.setOnClickListener {
            category = Category.TRAVEL_AND_ADVENTURE
            updateTextView("Travel and Adventure")
        }

        comedyButton.setOnClickListener {
            category = Category.COMEDY
            updateTextView("Comedy")
        }

        scienceButton.setOnClickListener {
            category = Category.SCIENCE_AND_TECHNOLOGY
            updateTextView("Science and Technology")
        }

        foodButton.setOnClickListener {
            category = Category.FOOD_AND_COOKING
            updateTextView("Food and Cooking")
        }

        knowledgeButton.setOnClickListener {
            category = Category.GENERAL_KNOWLEDGE
            updateTextView("General Knowledge")
        }

        healthButton.setOnClickListener {
            category = Category.HEALTH_AND_WELLNESS
            updateTextView("Health and Wellness")
        }

        bookButton.setOnClickListener {
            category = Category.BOOKS

            updateTextView("Books")
        }

        startChatButton.setOnClickListener {
            if (textView.text.isEmpty()) {
                Toast.makeText(this, "PepperAi: You Need To Select Context First", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, ChatScreen::class.java)
                startActivity(intent)
            }
        }
    }
    private fun updateTextView(buttonText: String) {
        textView.text = buttonText
    }

}