package com.example.engine

import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

// Check if there is an active saved game on disk
fun GameEngine.hasSavedGame(): Boolean {
    val managerFile = java.io.File(context.filesDir, "manager.json")
    val clubsDir = java.io.File(context.filesDir, "Clubes")
    val ligasDir = java.io.File(context.filesDir, "Ligas")
    return managerFile.exists() && 
           clubsDir.exists() && (clubsDir.listFiles()?.isNotEmpty() ?: false) &&
           ligasDir.exists() && (ligasDir.listFiles()?.isNotEmpty() ?: false)
}

// Load only saved manager details for metadata display
fun GameEngine.getSavedManagerSync(): Manager? {
    return try {
        val file = java.io.File(context.filesDir, "manager.json")
        if (file.exists()) {
            val encrypted = file.readText()
            val json = CryptoHelper.decrypt(encrypted)
            val moshi = com.squareup.moshi.Moshi.Builder()
                .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                .build()
            moshi.adapter(Manager::class.java).fromJson(json)
        } else null
    } catch (e: Exception) {
        null
    }
}

// Try to load existing game
suspend fun GameEngine.tryLoadGame(): Boolean = withContext(Dispatchers.IO) {
    val loadedClubs = storage.loadClubs()
    val loadedLigas = storage.loadLigas()

    if (loadedClubs.isEmpty() || loadedLigas.isEmpty()) {
        return@withContext false
    }

    _clubs.value = loadedClubs
    _ligas.value = loadedLigas

    // Re-construct squad references to maintain memory integrity
    loadedClubs.forEach { club ->
        val squadPlayers = club.squad.toMutableList()
        club.squad.clear()
        club.squad.addAll(squadPlayers)
    }

    // Load Manager profile
    val loadedManager = storage.loadManager()
    if (loadedManager != null) {
        _manager.value = loadedManager
    }

    // Load Calendar date
    val loadedDateStr = storage.loadCalendarDate()
    if (loadedDateStr != null) {
        try {
            _currentDate.value = LocalDate.parse(loadedDateStr)
        } catch (e: Exception) {
            _currentDate.value = LocalDate.of(2025, 1, 1)
        }
    } else {
        _currentDate.value = LocalDate.of(2025, 1, 1)
    }

    _diaryLines.value = storage.loadDiary()
    _newsLog.value = listOf("Partida cargada con éxito desde el almacenamiento AES fragmentado.")
    _isOnboardingFinished.value = true
    true
}

// Clear all files and reset engine states
suspend fun GameEngine.resetAllData() = withContext(Dispatchers.IO) {
    storage.clearAll()
    _clubs.value = emptyList()
    _ligas.value = emptyList()
    _countries.value = emptyList()
    _manager.value = Manager("Mánager Anónimo")
    _currentDate.value = LocalDate.of(2025, 1, 1)
    _diaryLines.value = emptyList()
    _newsLog.value = emptyList()
    _currentTrainingReport.value = null
    _currentActiveEvent.value = null
    _isOnboardingFinished.value = false
}
