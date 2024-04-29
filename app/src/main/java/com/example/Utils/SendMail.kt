package com.example.Utils

import android.content.Context
import android.util.Log
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class EmailSender(private val context: Context) {

    private var isEmailSent = false

    fun sendEmailWithBitmap(image: Bitmap, emailAddress: String, subject: String, body: String) {
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

        try {
            FileOutputStream(file).use { out ->
                image.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }

            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/jpeg"
            }
            context.startActivity(Intent.createChooser(emailIntent, "Send email"))
            isEmailSent = true
        } catch (e: Exception) {
            Log.d("mailSender:", "exp ${e.localizedMessage}" )
            isEmailSent = false
            e.printStackTrace()
        }
    }
    fun isEmailSentSuccessfully(): Boolean {
        return isEmailSent
    }
}
