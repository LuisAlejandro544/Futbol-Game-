package com.example.ui.screens

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator

enum class SimPhase {
    NOT_STARTED,
    FIRST_HALF,
    HALF_TIME_PAUSE,
    SECOND_HALF,
    FINISHED
}

// Play a nice goal sound (whistle/beep melody) safely in the background
fun playGoalSound() {
    try {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        Thread {
            try {
                Thread.sleep(150)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
                Thread.sleep(150)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Fallback high-pitched dual tone representing a referee's whistle
fun playWhistleFallbackTone() {
    try {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
        Thread {
            try {
                Thread.sleep(300)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
                Thread.sleep(180)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 300)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Play sound using SoundPool as an alternative low-latency option
fun playWhistleSoundPool(context: Context, resId: Int) {
    try {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        val soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(attrs)
            .build()
        
        val soundId = soundPool.load(context, resId, 1)
        soundPool.setOnLoadCompleteListener { sp, sampleId, status ->
            if (status == 0) {
                sp.play(sampleId, 1f, 1f, 1, 0, 1f)
                // Release SoundPool resources after a short delay
                Thread {
                    try {
                        Thread.sleep(1500)
                        sp.release()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } else {
                playWhistleFallbackTone()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        playWhistleFallbackTone()
    }
}
