package com.example.model

import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class Coach(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val speciality: String, // "Ataque", "Defensa", "Porteros", "Físico", "Mental"
    val level: Int,         // 1 - 5 stars
    val salary: Long        // Weekly salary deducted from Club's budget
) {
    val specialityLabel: String
        get() = when (speciality) {
            "Ataque" -> "Entrenador de Ataque"
            "Defensa" -> "Entrenador de Defensa"
            "Porteros" -> "Entrenador de Porteros"
            "Físico" -> "Preparador Físico"
            "Mental" -> "Psicólogo Deportivo"
            else -> "Ayudante Técnico"
        }
}

@JsonClass(generateAdapter = true)
data class PlayerTrainingResult(
    val playerId: String,
    val playerName: String,
    val position: Position,
    val scoutingLevel: Int,
    val increments: Map<String, Int> = emptyMap(), // e.g. "attack" to 1, "speed" to 1
    val decrements: Map<String, Int> = emptyMap()  // e.g. "speed" to 1 (for old players)
)

@JsonClass(generateAdapter = true)
data class WeeklyTrainingReport(
    val weekDate: String,
    val results: List<PlayerTrainingResult> = emptyList()
)
