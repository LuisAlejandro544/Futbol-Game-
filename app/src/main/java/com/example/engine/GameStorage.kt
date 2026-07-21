package com.example.engine

import android.content.Context
import android.util.Log
import com.example.model.Club
import com.example.model.League
import com.example.model.Player
import com.example.model.Manager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GameStorage(private val context: Context) {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val clubAdapter = moshi.adapter(Club::class.java)
    private val leagueAdapter = moshi.adapter(League::class.java)
    private val playerAdapter = moshi.adapter(Player::class.java)
    private val managerAdapter = moshi.adapter(Manager::class.java)

    // Lazy load: Reads folders dynamically
    private val clubsDir = File(context.filesDir, "Clubes").apply { mkdirs() }
    private val ligasDir = File(context.filesDir, "Ligas").apply { mkdirs() }
    private val jugadoresDir = File(context.filesDir, "Jugadores").apply { mkdirs() }
    private val modsDir = File(context.filesDir, "Mods").apply { mkdirs() }
    private val managerFile = File(context.filesDir, "manager.json")
    private val calendarFile = File(context.filesDir, "calendar.txt")
    private val diaryFile = File(context.filesDir, "diario_manager.txt")

    // Check for mods in /Mods directory to overwrite names or rules
    fun getActiveMods(): List<File> {
        if (!modsDir.exists()) return emptyList()
        return modsDir.listFiles { _, name -> name.endsWith(".json") }?.toList() ?: emptyList()
    }

    // Save clubs asynchronously in batch (RAM state to disk)
    suspend fun saveClubsBatch(clubs: List<Club>) = withContext(Dispatchers.IO) {
        clubs.forEach { club ->
            try {
                val json = clubAdapter.toJson(club)
                val encrypted = CryptoHelper.encrypt(json)
                val clubFile = File(clubsDir, "${club.id}.json")
                clubFile.writeText(encrypted)
            } catch (e: Exception) {
                Log.e("GameStorage", "Failed to save club ${club.name}", e)
            }
        }
    }

    // Load clubs lazily
    suspend fun loadClubs(): List<Club> = withContext(Dispatchers.IO) {
        val loadedClubs = mutableListOf<Club>()
        
        // Priority to Mods folder if custom club files exist there
        val moddedClubsFiles = modsDir.listFiles { _, name -> name.startsWith("club_") && name.endsWith(".json") }
        if (moddedClubsFiles != null && moddedClubsFiles.isNotEmpty()) {
            moddedClubsFiles.forEach { file ->
                try {
                    val json = file.readText() // Mods are usually raw JSON
                    clubAdapter.fromJson(json)?.let { loadedClubs.add(it) }
                } catch (e: Exception) {
                    Log.e("GameStorage", "Failed to parse modded club file: ${file.name}", e)
                }
            }
        }

        // Standard load
        val files = clubsDir.listFiles { _, name -> name.endsWith(".json") }
        if (files != null) {
            files.forEach { file ->
                try {
                    // Avoid duplicating if modded file overwrote it
                    val encrypted = file.readText()
                    val json = CryptoHelper.decrypt(encrypted)
                    clubAdapter.fromJson(json)?.let { club ->
                        if (loadedClubs.none { it.id == club.id }) {
                            loadedClubs.add(club)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("GameStorage", "Failed to load club from ${file.name}", e)
                }
            }
        }
        loadedClubs
    }

    suspend fun saveLigasBatch(ligas: List<League>) = withContext(Dispatchers.IO) {
        ligas.forEach { league ->
            try {
                val json = leagueAdapter.toJson(league)
                val encrypted = CryptoHelper.encrypt(json)
                val leagueFile = File(ligasDir, "${league.id}.json")
                leagueFile.writeText(encrypted)
            } catch (e: Exception) {
                Log.e("GameStorage", "Failed to save league ${league.name}", e)
            }
        }
    }

    suspend fun loadLigas(): List<League> = withContext(Dispatchers.IO) {
        val loadedLigas = mutableListOf<League>()
        val files = ligasDir.listFiles { _, name -> name.endsWith(".json") }
        if (files != null) {
            files.forEach { file ->
                try {
                    val encrypted = file.readText()
                    val json = CryptoHelper.decrypt(encrypted)
                    leagueAdapter.fromJson(json)?.let { loadedLigas.add(it) }
                } catch (e: Exception) {
                    Log.e("GameStorage", "Failed to load league from ${file.name}", e)
                }
            }
        }
        loadedLigas
    }

    suspend fun savePlayersBatch(players: List<Player>) = withContext(Dispatchers.IO) {
        players.forEach { player ->
            try {
                val json = playerAdapter.toJson(player)
                val encrypted = CryptoHelper.encrypt(json)
                val playerFile = File(jugadoresDir, "${player.id}.json")
                playerFile.writeText(encrypted)
            } catch (e: Exception) {
                Log.e("GameStorage", "Failed to save player ${player.fullName}", e)
            }
        }
    }

    suspend fun loadPlayers(): List<Player> = withContext(Dispatchers.IO) {
        val loadedPlayers = mutableListOf<Player>()
        val files = jugadoresDir.listFiles { _, name -> name.endsWith(".json") }
        if (files != null) {
            files.forEach { file ->
                try {
                    val encrypted = file.readText()
                    val json = CryptoHelper.decrypt(encrypted)
                    playerAdapter.fromJson(json)?.let { loadedPlayers.add(it) }
                } catch (e: Exception) {
                    Log.e("GameStorage", "Failed to load player from ${file.name}", e)
                }
            }
        }
        loadedPlayers
    }

    // Clean all state
    suspend fun clearAll() = withContext(Dispatchers.IO) {
        clubsDir.deleteRecursively()
        ligasDir.deleteRecursively()
        jugadoresDir.deleteRecursively()
        clubsDir.mkdirs()
        ligasDir.mkdirs()
        jugadoresDir.mkdirs()
        if (managerFile.exists()) managerFile.delete()
        if (calendarFile.exists()) calendarFile.delete()
        if (diaryFile.exists()) diaryFile.delete()
    }

    suspend fun saveManager(manager: Manager) = withContext(Dispatchers.IO) {
        try {
            val json = managerAdapter.toJson(manager)
            val encrypted = CryptoHelper.encrypt(json)
            managerFile.writeText(encrypted)
        } catch (e: Exception) {
            Log.e("GameStorage", "Failed to save manager ${manager.name}", e)
        }
    }

    suspend fun loadManager(): Manager? = withContext(Dispatchers.IO) {
        try {
            if (managerFile.exists()) {
                val encrypted = managerFile.readText()
                val json = CryptoHelper.decrypt(encrypted)
                managerAdapter.fromJson(json)
            } else null
        } catch (e: Exception) {
            Log.e("GameStorage", "Failed to load manager", e)
            null
        }
    }

    suspend fun saveCalendarDate(dateStr: String) = withContext(Dispatchers.IO) {
        try {
            val encrypted = CryptoHelper.encrypt(dateStr)
            calendarFile.writeText(encrypted)
        } catch (e: Exception) {
            Log.e("GameStorage", "Failed to save calendar date", e)
        }
    }

    suspend fun loadCalendarDate(): String? = withContext(Dispatchers.IO) {
        try {
            if (calendarFile.exists()) {
                val encrypted = calendarFile.readText()
                CryptoHelper.decrypt(encrypted)
            } else null
        } catch (e: Exception) {
            Log.e("GameStorage", "Failed to load calendar date", e)
            null
        }
    }

    suspend fun appendToDiary(entry: String) = withContext(Dispatchers.IO) {
        try {
            diaryFile.appendText("$entry\n")
        } catch (e: Exception) {
            Log.e("GameStorage", "Failed to append to diary", e)
        }
    }

    suspend fun loadDiary(): List<String> = withContext(Dispatchers.IO) {
        try {
            if (diaryFile.exists()) {
                diaryFile.readLines()
            } else emptyList()
        } catch (e: Exception) {
            Log.e("GameStorage", "Failed to load diary", e)
            emptyList()
        }
    }
}
