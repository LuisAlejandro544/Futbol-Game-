package com.example.engine

import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

enum class ModCategory {
    GAMEPLAY,         // Match speed, goal frequency, injury mechanics
    ROSTER,           // Custom teams / JSON leagues
    INTERFACE_THEME,  // UI Color themes (EA Sports FIFA Neon, Retro Arcade 98)
    COMMENTARY        // Custom commentary voices & style
}

@JsonClass(generateAdapter = true)
data class GameMod(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val author: String = "Comunidad FEDEBOL",
    val description: String,
    val category: ModCategory,
    var isEnabled: Boolean = false,
    val configJson: String = "{}"
)

object ModEngine {

    val defaultMods = listOf(
        GameMod(
            id = "mod_fifa_gameplay",
            name = "⚡ FIFA Ultra Speed & High Scoring Mod",
            author = "EA Sports Fan Club",
            description = "Aumenta la velocidad de simulación de partidos 3x y eleva la frecuencia de goles espectaculares (+45%).",
            category = ModCategory.GAMEPLAY,
            isEnabled = false
        ),
        GameMod(
            id = "mod_theme_fifa_neon",
            name = "🎨 Interfaz FIFA EA Sports Neon Cyan",
            author = "UI Modder Guild",
            description = "Reemplaza la interfaz gráfica por un tema futurista cian neón al estilo FIFA Ultimate Team.",
            category = ModCategory.INTERFACE_THEME,
            isEnabled = true
        ),
        GameMod(
            id = "mod_commentary_epic",
            name = "🎙️ Comentarios Emisora Sudamericana Clásica",
            author = "Radio Gol FM",
            description = "Agrega narraciones con modismos sudamericanos, gritos de gol festivos y euforia de hinchada.",
            category = ModCategory.COMMENTARY,
            isEnabled = true
        ),
        GameMod(
            id = "mod_roster_legends",
            name = "⚽ Plantillas Legendarias All-Stars (JSON Mod)",
            author = "FEDEBOL Modding",
            description = "Habilita la importación y edición de archivos de ligas y plantillas históricas en formato JSON.",
            category = ModCategory.ROSTER,
            isEnabled = false
        )
    )

    private val _installedMods = MutableStateFlow<List<GameMod>>(defaultMods)
    val installedMods: StateFlow<List<GameMod>> = _installedMods

    private val _activeMatchSpeedMultiplier = MutableStateFlow(1.0f)
    val activeMatchSpeedMultiplier: StateFlow<Float> = _activeMatchSpeedMultiplier

    private val _activeGoalMultiplier = MutableStateFlow(1.0f)
    val activeGoalMultiplier: StateFlow<Float> = _activeGoalMultiplier

    fun toggleMod(modId: String) {
        val currentList = _installedMods.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == modId }
        if (index != -1) {
            val mod = currentList[index]
            currentList[index] = mod.copy(isEnabled = !mod.isEnabled)
            _installedMods.value = currentList
            recalculateModEffects()
        }
    }

    fun installCustomMod(newMod: GameMod) {
        val currentList = _installedMods.value.toMutableList()
        currentList.add(newMod)
        _installedMods.value = currentList
        recalculateModEffects()
    }

    private fun recalculateModEffects() {
        var speed = 1.0f
        var goals = 1.0f
        _installedMods.value.filter { it.isEnabled }.forEach { mod ->
            if (mod.id == "mod_fifa_gameplay") {
                speed *= 3.0f
                goals *= 1.45f
            }
        }
        _activeMatchSpeedMultiplier.value = speed
        _activeGoalMultiplier.value = goals
    }
}
