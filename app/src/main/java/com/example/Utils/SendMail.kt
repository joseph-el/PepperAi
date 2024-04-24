package com.example.Utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EmailHelper(private val context: Context) {

    private val TAG = "EmailHelper"

    fun sendEmailWithImage(email: String, subject: String, body: String, bitmap: Bitmap) {
        val uri = saveBitmapToFile(bitmap)

        val emailIntent = android.content.Intent(android.content.Intent.ACTION_SEND)
        emailIntent.type = "image/*"
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf(email))
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body)
        emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri)

        emailIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            deleteTemporaryFile(uri)
            context.startActivity(android.content.Intent.createChooser(emailIntent, "Send email..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Log.e(TAG, "No email clients installed.")
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "image.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error saving bitmap to file: ${e.message}")
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    private fun deleteTemporaryFile(uri: Uri) {
        try {
            val fileToDelete = File(uri.path)
            if (fileToDelete.exists()) {
                fileToDelete.delete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting temporary file: ${e.message}")
        }
    }
}