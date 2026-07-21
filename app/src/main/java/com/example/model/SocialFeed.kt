package com.example.model

import com.squareup.moshi.JsonClass
import java.util.UUID
import kotlin.random.Random

@JsonClass(generateAdapter = true)
data class SocialPost(
    val id: String = UUID.randomUUID().toString(),
    val handle: String,          // e.g. "@Kaelen_G7", "@FalkPress", "@AethelFans"
    val authorName: String,      // e.g. "Kaelen Gomez", "Deporte Diario", "La Hinchada"
    val content: String,
    val likes: Int,
    val reposts: Int,
    val timeAgo: String,
    val isDecisionTrigger: Boolean = false,
    val affectedPlayerId: String? = null,
    val decisionOptions: List<String> = emptyList()
)

object SocialFeedGenerator {
    private val fanHandles = listOf("@FansUnidos", "@LaCurvaFiel", "@HinchaReal", "@GoolFafi", "@FutbolTotal")
    private val fanNames = listOf("Fans Unidos", "La Curva Fiel", "Socio Real", "Fafi Goles", "Fútbol Total")
    
    private val pressHandles = listOf("@DeporteDiario", "@FafiGlobal", "@FutbolAnalitico", "@TribunaSport")
    private val pressNames = listOf("Deporte Diario", "FAFI Global", "Fútbol Analítico", "Tribuna Sport")

    fun generatePostForEvent(eventType: String, clubName: String, playerName: String? = null, playerId: String? = null): SocialPost {
        val random = Random
        val likes = random.nextInt(50, 5000)
        val reposts = (likes * random.nextFloat() * 0.4f).toInt()
        val timeAgo = "${random.nextInt(5, 59)}m"

        return when (eventType) {
            "WIN" -> {
                val handle = fanHandles.random(random)
                val author = fanNames[fanHandles.indexOf(handle)]
                val text = listOf(
                    "¡Increíble partido del $clubName! Así se juega al fútbol. El vestuario está con todo.",
                    "Qué delicia ver ganar al $clubName hoy. Esto pinta para campeones.",
                    "Tres puntos fundamentales hoy. ¡Fútbol de alta costura! #Vamos$clubName"
                ).random(random)
                SocialPost(handle = handle, authorName = author, content = text, likes = likes, reposts = reposts, timeAgo = timeAgo)
            }
            "LOSS" -> {
                val handle = fanHandles.random(random)
                val author = fanNames[fanHandles.indexOf(handle)]
                val text = listOf(
                    "Vergonzoso lo de hoy de $clubName. El planteamiento táctico fue inexistente.",
                    "¿A qué estamos jugando? No corren, no defienden. Urgen cambios tácticos inmediatos.",
                    "Derrota dura. Así no se puede ascender ni competir en este universo."
                ).random(random)
                SocialPost(handle = handle, authorName = author, content = text, likes = likes, reposts = reposts, timeAgo = timeAgo)
            }
            "EGO_CLASH" -> {
                // Superstar ego conflict in social network!
                val pName = playerName ?: "El jugador"
                val handle = "@${pName.replace(" ", "")}"
                val text = "A veces el esfuerzo en los entrenamientos no basta cuando los favoritismos mandan en el vestuario. No vine aquí a sentarme en el banco... 🤐🤐 #Respeto"
                SocialPost(
                    handle = handle,
                    authorName = pName,
                    content = text,
                    likes = likes * 4,
                    reposts = reposts * 4,
                    timeAgo = "1h",
                    isDecisionTrigger = true,
                    affectedPlayerId = playerId,
                    decisionOptions = listOf(
                        "Multar con 2 semanas de sueldo y relegar al filial",
                        "Tener una charla privada y prometer titularidad",
                        "Ignorar públicamente para no alimentar la prensa"
                    )
                )
            }
            "DISCIPLINE_BREACH" -> {
                val pName = playerName ?: "El jugador"
                val handle = pressHandles.random(random)
                val author = pressNames[pressHandles.indexOf(handle)]
                val text = "🚨 EXCLUSIVA: Captan a $pName de fiesta a altas horas de la madrugada previo al entrenamiento decisivo del $clubName. ¿Falta de liderazgo del mánager?"
                SocialPost(
                    handle = handle,
                    authorName = author,
                    content = text,
                    likes = likes * 5,
                    reposts = reposts * 5,
                    timeAgo = "2h",
                    isDecisionTrigger = true,
                    affectedPlayerId = playerId,
                    decisionOptions = listOf(
                        "Multar al jugador por conducta indisciplinada",
                        "Defender al jugador públicamente ante la prensa",
                        "Suspender su contrato temporalmente (Trial Lock)"
                    )
                )
            }
            else -> {
                val handle = pressHandles.random(random)
                val author = pressNames[pressHandles.indexOf(handle)]
                val text = "Se reporta alta volatilidad macroeconómica en las ligas del país. Los clubes modestos enfrentarán deudas."
                SocialPost(handle = handle, authorName = author, content = text, likes = likes, reposts = reposts, timeAgo = timeAgo)
            }
        }
    }
}
