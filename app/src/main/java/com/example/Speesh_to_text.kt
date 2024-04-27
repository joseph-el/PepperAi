package com.example

import android.app.Activity
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.Menu
import com.example.empathymap.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class VoiceRecorder(private val outputDir: File) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
     var isRecording = false

    private val MIN_SILENCE_DURATION_MS = 1200
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
                val fileName = "voice_${System.currentTimeMillis()}.3gp"
                outputFile = File(outputDir, fileName)

                mediaRecorder?.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(outputFile?.absolutePath)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    prepare()
                    start()
                    lastSilenceTime = System.currentTimeMillis()
                    isRecording = true
                    Thread {
                        while (isRecording) {
                            val amplitude = getMaxAmplitude()
                            Log.d("YourTag", "amplitude: $amplitude")
                            val audioLevel = amplitude / 2000 // Adjust as needed
                            Log.d("YourTag", "the level: $audioLevel")
                            processAudioLevel(audioLevel)
                            Thread.sleep(100) // Adjust as needed, this controls how often you check the amplitude
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
            }
        }
    }


    fun getRecordedFilePath(): String? {
        return outputFile?.absolutePath
    }

    fun deleteRecordedFile() {
        outputFile?.delete()
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



/*
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.IOException
import kotlin.math.abs

class VoiceRecorder(private val outputDir: File) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var isRecording = false

    private val audioRecorder = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        BUFFER_SIZE
    )
    init {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
    }


    // Inside VoiceRecorder class

    private val MIN_AMPLITUDE_THRESHOLD = 500 // Adjust threshold as needed
    private val SILENCE_DURATION_THRESHOLD = 2000 // Adjust duration as needed (in milliseconds)

    fun startRecordingWithSilenceDetection() {
        audioRecorder.startRecording()
        val buffer = ShortArray(BUFFER_SIZE)
        var silentDuration = 0

        while (isRecording) {
            val readResult = audioRecorder.read(buffer, 0, buffer.size, AudioRecord.READ_BLOCKING)
            if (readResult < 0) {
                // Error handling
                break
            }

            val amplitude = buffer.maxByOrNull { abs(it.toInt()) } ?: 0
            if (amplitude < MIN_AMPLITUDE_THRESHOLD) {
                silentDuration += SAMPLE_RATE / buffer.size
            } else {
                silentDuration = 0
            }

            if (silentDuration >= SILENCE_DURATION_THRESHOLD) {
                stopRecording()
                break
            }
        }
        audioRecorder.stop()
    }

    companion object {
        private val SAMPLE_RATE = 44100
        private  val BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        const val RECORD_AUDIO_PERMISSION = android.Manifest.permission.RECORD_AUDIO
    }

    fun startRecording() {
        if (!isRecording) {
            mediaRecorder = MediaRecorder()
            try {
                val fileName = "voice_${System.currentTimeMillis()}.3gp"
                outputFile = File(outputDir, fileName)
                Log.d("YourTag", "Output file path: ${outputFile?.absolutePath}")

                mediaRecorder?.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(outputFile?.absolutePath)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setMaxDuration(60000) // Maximum duration in milliseconds (1 minute in this case)
                    setOnInfoListener { _, what, _ ->
                        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                            stopRecording()
                        }
                    }
                    prepare()
                    start()
                    isRecording = true
                }
            } catch (e: IOException) {
                Log.e("YourTag", "Error creating temporary file: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun stopRecording() {
        if (isRecording) {
            mediaRecorder?.apply {
                stop()
                release()
                isRecording = false
            }
        }
    }

    fun getRecordedFilePath(): String? {
        return outputFile?.absolutePath
    }

    fun deleteRecordedFile() {
        outputFile?.delete()
    }
}


 */
// Inside VoiceRecorder class





/*

class VoiceRecorder(private val outputDir: File) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    init {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
    }
    fun startRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = MediaRecorder()
            try {
                val fileName = "voice_${System.currentTimeMillis()}.3gp"
                outputFile = File(outputDir, fileName)
                Log.d("YourTag", "Output file path: ${outputFile?.absolutePath}")

                mediaRecorder?.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(outputFile?.absolutePath)
                    Log.d("YourTag", "DONE HERE")

                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setMaxDuration(60000) // Maximum duration in milliseconds (1 minute in this case)
                    setOnInfoListener { _, what, _ ->
                        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                            stopRecording()
                        }
                    }
                    prepare()
                    start()
                }
            } catch (e: IOException) {
                Log.e("YourTag", "Error creating temporary file: ${e.message}")

                e.printStackTrace()
            }
        }
    }
    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    fun getRecordedFilePath(): String? {
        return outputFile?.absolutePath
    }

    fun deleteRecordedFile() {
        outputFile?.delete()
    }
}

 */


/*
import android.media.AudioFormat
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.TargetDataLine

class VoiceRecorder(private val outputFile: String) {
    private var audioRecorder: TargetDataLine? = null
    private var isRecording = false

    fun startRecording() {
        if (!isRecording) {
            val audioFormat = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100f, 16, 2, 4, 44100f, false)
            val info = DataLine.Info(TargetDataLine::class.java, audioFormat)

            if (!AudioSystem.isLineSupported(info)) {
                println("Audio line not supported!")
                return
            }

            try {
                audioRecorder = AudioSystem.getLine(info) as TargetDataLine
                audioRecorder!!.open(audioFormat)
                audioRecorder!!.start()
                isRecording = true
                println("Recording started...")
            } catch (ex: LineUnavailableException) {
                ex.printStackTrace()
            }
        }
    }

    fun stopRecording() {
        if (isRecording) {
            audioRecorder?.stop()
            audioRecorder?.close()
            isRecording = false
            println("Recording stopped.")
            saveRecording()
        }
    }

    private fun saveRecording() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val bufferSize = 4096
        val buffer = ByteArray(bufferSize)
        var bytesRead: Int

        while (audioRecorder!!.read(buffer, 0, buffer.size).also { bytesRead = it } != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead)
        }

        val audioData = byteArrayOutputStream.toByteArray()
        byteArrayOutputStream.close()

        val outputFile = File(outputFile)
        AudioSystem.write(AudioInputStream(ByteArrayInputStream(audioData), audioRecorder!!.format, audioData.size.toLong()), AudioFileFormat.Type.WAVE, outputFile)
        println("Recording saved to: ${outputFile.absolutePath}")
    }

    fun isRecordingFinished(): Boolean {
        return !isRecording
    }

    fun getRecordingPath(): String {
        return outputFile
    }

    fun deleteRecording() {
        val file = File(outputFile)
        if (file.exists()) {
            file.delete()
            println("Recording file deleted.")
        } else {
            println("Recording file not found.")
        }
    }
}

fun main() {
    val outputFile = "recorded_audio.wav"
    val voiceRecorder = VoiceRecorder(outputFile)

    // Test the functionality
    voiceRecorder.startRecording()

    // Simulating some time of recording
    Thread.sleep(5000) // Recording for 5 seconds

    voiceRecorder.stopRecording()
    println("Is recording finished? ${voiceRecorder.isRecordingFinished()}")
    println("Recording saved at: ${voiceRecorder.getRecordingPath()}")

    // Uncomment below line to test deleteRecording() function
    // voiceRecorder.deleteRecording()
}


 */