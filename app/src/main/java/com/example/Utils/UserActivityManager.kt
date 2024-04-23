package com.example.Utils

import android.os.Handler
import android.os.Looper

class UserActivityManager private constructor() {
    private var isActive = false

    companion object {
        private var instance: UserActivityManager? = null

        fun getInstance(): UserActivityManager {
            if (instance == null) {
                instance = UserActivityManager()
            }
            return instance!!
        }
    }

    // Method to simulate user activity (you will replace this with your actual logic)
    fun simulateUserActivity() {
        isActive = true
        // Reset the activity flag after a certain duration
        Handler(Looper.getMainLooper()).postDelayed({
            isActive = false
        }, 5000) // Adjust the duration as needed
    }

    // Method to start checking user activity periodically
    fun startChecking() {
        val thread = Thread {
            while (true) {
                // Check for user activity
                if (!isActive) {
                    // If no active users, navigate to main home screen
                    navigateToMainHomeScreen()
                }
                // Sleep for a certain duration before checking again
                Thread.sleep(1000) // Adjust the duration as needed
            }
        }
        thread.start()
    }

    // Method to navigate to main home screen (replace with your actual logic)
    private fun navigateToMainHomeScreen() {
        println("Navigating to main home screen...")
        // Code to navigate to main home screen goes here
    }
}
