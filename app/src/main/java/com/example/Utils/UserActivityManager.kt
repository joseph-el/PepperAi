package com.example.Utils

import android.os.Handler
import android.os.Looper
import android.app.Activity
import android.content.Intent
import android.util.Log
import com.example.Screen.WelcomeScreen

class InactivityTimer(private val activity: Activity, private val timeout: Long) {

    private var handler: Handler = Handler(Looper.getMainLooper())

    private var runnable: Runnable = Runnable {
        Log.d("callback: ", "hello iam inn callback")
        val intent = Intent(activity, WelcomeScreen::class.java)
        activity.startActivity(intent)
    }

    init {
        resetTimer()
    }

    fun onUserInteraction() {
        Log.d("callback: ", "iam in user interaction! ")
        resetTimer()
    }

    private fun resetTimer() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable,  300000)
    }

    fun stop() {
        Log.d("callback: ", "iam in stop callback")
        handler.removeCallbacks(runnable)
    }
}
