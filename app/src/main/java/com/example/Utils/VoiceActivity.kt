package com.example.Core

import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.IOException

class VoiceRecorder(private val outputDir: File) {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    var isRecording = false
    var onRecordingFinishedListener: (() -> Unit)? = null

    private val MIN_SILENCE_DURATION_MS = 2200 // TODO: make test it: Done
    private val SILENCE_THRESHOLD_DB = 0
    private var lastSilenceTime: Long = 0

    init {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
    }
    fun startRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = MediaRecorder()
            try {
                val fileName = "voice_${System.currentTimeMillis()}.m4a"
                outputFile = File(outputDir, fileName)
                mediaRecorder?.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setOutputFile(outputFile?.absolutePath)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    prepare()
                    start()
                    lastSilenceTime = System.currentTimeMillis()
                    isRecording = true
                    Thread {
                        while (isRecording) {
                            val amplitude = getMaxAmplitude()
                            Log.d("YourTag", "amplitude: $amplitude")
                            val audioLevel = amplitude / 2000
                            Log.d("YourTag", "the level: $audioLevel")
                            processAudioLevel(audioLevel)
                            Thread.sleep(100)
                        }
                    }.start()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording() {
        Log.d("YourTag", "i try to stop")
        if (mediaRecorder != null && isRecording) {
            try {
                mediaRecorder?.stop()
                mediaRecorder?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mediaRecorder = null
                isRecording = false
                onRecordingFinishedListener?.invoke()
            }
        }
    }

    fun getRecordedFilePath(): String? {
        return outputFile?.absolutePath
    }

    fun deleteRecordedFile() {
        outputFile?.delete()
        outputFile = null
    }

    fun processAudioLevel(audioLevel: Int) {
        if (!isRecording) return

        if (audioLevel <= SILENCE_THRESHOLD_DB) {

            val currentTime = System.currentTimeMillis()

            Log.d("YourTag", "last-time: $lastSilenceTime")
            Log.d("YourTag", "curr-time: $currentTime")
            Log.d("YourTag", "MIN: ${currentTime - lastSilenceTime}")

            if ((currentTime - lastSilenceTime) >= MIN_SILENCE_DURATION_MS) {
                stopRecording()
            }
        } else {
            lastSilenceTime = System.currentTimeMillis()
        }
    }
}