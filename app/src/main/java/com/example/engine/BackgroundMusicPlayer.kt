package com.example.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileOutputStream

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val resId: Int = 0,
    val durationStr: String = "3:30",
    val customUriString: String? = null,
    val isCustom: Boolean = false
)

object BackgroundMusicPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var currentContext: Context? = null

    // Default bundled tracks
    val defaultTracks = listOf(
        MusicTrack(
            id = "fifa_theme_1",
            title = "FIFA EA Sports Anthem 26",
            artist = "FEDEBOL Soundtrack",
            resId = com.example.R.raw.better_day,
            durationStr = "2:45"
        ),
        MusicTrack(
            id = "moirefree_toy",
            title = "Moirefree Toy",
            artist = "Uploaded Music",
            resId = com.example.R.raw.better_day,
            durationStr = "2:15"
        ),
        MusicTrack(
            id = "better_day",
            title = "Better Day",
            artist = "penguinmusic",
            resId = com.example.R.raw.better_day,
            durationStr = "1:30"
        ),
        MusicTrack(
            id = "ambient_dnb",
            title = "Ambient DnB",
            artist = "AbsoluteSound",
            resId = com.example.R.raw.better_day,
            durationStr = "2:30"
        )
    )

    private val _customTracks = MutableStateFlow<List<MusicTrack>>(emptyList())
    val customTracks: StateFlow<List<MusicTrack>> = _customTracks

    val tracks: List<MusicTrack> get() = defaultTracks + _customTracks.value

    private val _currentTrack = MutableStateFlow<MusicTrack?>(null)
    val currentTrack: StateFlow<MusicTrack?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume

    private val _isMusicEnabled = MutableStateFlow(true)
    val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled

    fun initialize(context: Context) {
        val appContext = context.applicationContext
        currentContext = appContext

        // Load custom tracks stored in local directory
        loadCustomTracksFromDisk(appContext)

        // Load settings from SharedPreferences
        val prefs = appContext.getSharedPreferences("fedebol_music_prefs", Context.MODE_PRIVATE)
        _volume.value = prefs.getFloat("music_volume", 0.5f)
        _isMusicEnabled.value = prefs.getBoolean("music_enabled", true)
        val savedTrackId = prefs.getString("current_track_id", "fifa_theme_1") ?: "fifa_theme_1"

        val initialTrack = tracks.find { it.id == savedTrackId } ?: tracks.firstOrNull()
        _currentTrack.value = initialTrack

        if (_isMusicEnabled.value) {
            initialTrack?.let { playTrack(appContext, it, resumeIfMatches = false) }
        }
    }

    private fun loadCustomTracksFromDisk(context: Context) {
        try {
            val musicDir = File(context.filesDir, "custom_soundtrack")
            if (musicDir.exists()) {
                val files = musicDir.listFiles() ?: emptyArray()
                val loadedList = files.map { file ->
                    MusicTrack(
                        id = file.nameWithoutExtension,
                        title = file.nameWithoutExtension.replace("_", " "),
                        artist = "Usuario (FIFA Mod)",
                        resId = 0,
                        durationStr = "3:45",
                        customUriString = file.absolutePath,
                        isCustom = true
                    )
                }
                _customTracks.value = loadedList
            }
        } catch (e: Exception) {
            Log.e("BackgroundMusicPlayer", "Failed loading custom tracks", e)
        }
    }

    fun importCustomAudioTrack(context: Context, uri: Uri, customName: String? = null): MusicTrack? {
        return try {
            val musicDir = File(context.filesDir, "custom_soundtrack")
            if (!musicDir.exists()) musicDir.mkdirs()

            val cleanTitle = (customName ?: "Tema_FIFA_${System.currentTimeMillis()}").replace(" ", "_")
            val targetFile = File(musicDir, "$cleanTitle.mp3")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }

            val newTrack = MusicTrack(
                id = cleanTitle,
                title = cleanTitle.replace("_", " "),
                artist = "Pista Personalizada FIFA",
                resId = 0,
                durationStr = "3:30",
                customUriString = targetFile.absolutePath,
                isCustom = true
            )

            val updated = _customTracks.value.toMutableList()
            updated.add(newTrack)
            _customTracks.value = updated

            // Play newly imported track immediately
            playTrack(context, newTrack, resumeIfMatches = false)
            newTrack
        } catch (e: Exception) {
            Log.e("BackgroundMusicPlayer", "Error importing custom track", e)
            null
        }
    }

    fun deleteCustomTrack(context: Context, track: MusicTrack) {
        if (!track.isCustom || track.customUriString == null) return
        try {
            val file = File(track.customUriString)
            if (file.exists()) file.delete()

            val updated = _customTracks.value.filter { it.id != track.id }
            _customTracks.value = updated

            if (_currentTrack.value?.id == track.id) {
                playNext(context)
            }
        } catch (e: Exception) {
            Log.e("BackgroundMusicPlayer", "Error deleting track", e)
        }
    }

    fun playTrack(context: Context, track: MusicTrack, resumeIfMatches: Boolean = true) {
        if (!isMusicEnabled.value) return

        if (resumeIfMatches && _currentTrack.value?.id == track.id && mediaPlayer != null) {
            try {
                if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.start()
                    _isPlaying.value = true
                }
            } catch (e: Exception) {
                Log.e("BackgroundMusicPlayer", "Failed to resume matching track", e)
            }
            return
        }

        stop()

        _currentTrack.value = track
        saveCurrentTrackId(track.id)

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )

                if (track.isCustom && track.customUriString != null) {
                    val file = File(track.customUriString)
                    if (file.exists()) {
                        setDataSource(file.absolutePath)
                        prepare()
                    } else {
                        Log.w("BackgroundMusicPlayer", "Custom file missing, fallback to default")
                        val fallback = defaultTracks.first()
                        _currentTrack.value = fallback
                        val afd = context.resources.openRawResourceFd(fallback.resId)
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                        prepare()
                    }
                } else {
                    val rawId = if (track.resId != 0) track.resId else com.example.R.raw.better_day
                    try {
                        val afd = context.resources.openRawResourceFd(rawId)
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                        prepare()
                    } catch (ex: Exception) {
                        Log.w("BackgroundMusicPlayer", "Failed playing via Fd, trying Uri...", ex)
                        reset()
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        val uri = Uri.parse("android.resource://${context.packageName}/$rawId")
                        setDataSource(context, uri)
                        prepare()
                    }
                }

                isLooping = false
                setVolume(_volume.value, _volume.value)
                setOnCompletionListener {
                    playNext(context)
                }
                start()
            }
            _isPlaying.value = true
        } catch (e: Exception) {
            Log.e("BackgroundMusicPlayer", "Failed to start media player", e)
        }
    }

    fun pause() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.pause()
                }
            } catch (e: Exception) {
                Log.e("BackgroundMusicPlayer", "Error pausing player", e)
            } finally {
                _isPlaying.value = false
            }
        }
    }

    fun resume(context: Context) {
        if (!isMusicEnabled.value) return
        mediaPlayer?.let {
            try {
                if (!it.isPlaying) {
                    it.start()
                }
                _isPlaying.value = true
            } catch (e: Exception) {
                Log.e("BackgroundMusicPlayer", "Error resuming player", e)
            }
        } ?: run {
            _currentTrack.value?.let { playTrack(context, it) }
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e("BackgroundMusicPlayer", "Error stopping player", e)
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
        }
    }

    fun setVolume(vol: Float) {
        val coerced = vol.coerceIn(0f, 1f)
        _volume.value = coerced
        try {
            mediaPlayer?.setVolume(coerced, coerced)
        } catch (e: Exception) {
            Log.e("BackgroundMusicPlayer", "Error setting volume on MediaPlayer", e)
        }

        currentContext?.let {
            val prefs = it.getSharedPreferences("fedebol_music_prefs", Context.MODE_PRIVATE)
            prefs.edit().putFloat("music_volume", coerced).apply()
        }
    }

    fun setMusicEnabled(enabled: Boolean, context: Context) {
        _isMusicEnabled.value = enabled

        val prefs = context.getSharedPreferences("fedebol_music_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("music_enabled", enabled).apply()

        if (enabled) {
            _currentTrack.value?.let { playTrack(context, it) }
        } else {
            stop()
        }
    }

    fun playNext(context: Context) {
        val allList = tracks
        if (allList.isEmpty()) return
        val currentIndex = allList.indexOfFirst { it.id == _currentTrack.value?.id }
        val nextIndex = (currentIndex + 1) % allList.size
        playTrack(context, allList[nextIndex], resumeIfMatches = false)
    }

    fun playPrevious(context: Context) {
        val allList = tracks
        if (allList.isEmpty()) return
        val currentIndex = allList.indexOfFirst { it.id == _currentTrack.value?.id }
        val prevIndex = if (currentIndex <= 0) allList.size - 1 else currentIndex - 1
        playTrack(context, allList[prevIndex], resumeIfMatches = false)
    }

    private fun saveCurrentTrackId(trackId: String) {
        currentContext?.let {
            val prefs = it.getSharedPreferences("fedebol_music_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("current_track_id", trackId).apply()
        }
    }
}
