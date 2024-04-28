package com.example.Core

import android.content.Context
import android.os.Environment
import android.util.Log
import org.json.JSONObject
import java.io.File

class ConfigManager(private val fileName: String) {
    private var config: JSONObject? = null
    private var fileFound: Boolean = false

    init {
        loadConfig()
    }

    private fun loadConfig() {
        try {
            val openDir = File(Environment.getExternalStorageDirectory(), "Recordings")
            if (!openDir.exists()) {

                Log.d("config_file", ": dir")

                fileFound = false
                return
            }
            val file = File(openDir, fileName)
            if (!file.exists()) {
                Log.d("config_file", ": file")
                fileFound = false
                return
            }
            val json = file.readText(Charsets.UTF_8)
            config = JSONObject(json)
            fileFound = true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("config_file", ": exc : ${e.localizedMessage}")
            fileFound = false
        }
    }

    fun getValueByKey(key: String): String? {
        return config?.optString(key)
    }

    fun isFileFound(): Boolean {
        return fileFound
    }
}
