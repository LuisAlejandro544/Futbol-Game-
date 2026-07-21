package com.example.engine

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class SocialFeedManager(
    private val socialFeedFlow: MutableStateFlow<List<SocialPost>>,
    private val managerFlow: MutableStateFlow<Manager>,
    private val clubsFlow: MutableStateFlow<List<Club>>,
    private val addNews: (String) -> Unit
) {
    // Handles Manager social network feedback decisions
    fun handleSocialFeedDecision(postId: String, decisionIndex: Int) {
        val list = socialFeedFlow.value.toMutableList()
        val postIdx = list.indexOfFirst { it.id == postId }
        if (postIdx != -1) {
            val post = list[postIdx]
            val affectedPlayerId = post.affectedPlayerId
            val decisionSelected = post.decisionOptions.getOrNull(decisionIndex) ?: "Ignorar"

            val mgr = managerFlow.value
            val currentClubs = clubsFlow.value

            if (affectedPlayerId != null) {
                val player = currentClubs.flatMap { it.squad }.firstOrNull { it.id == affectedPlayerId }
                if (player != null) {
                    when (decisionIndex) {
                        0 -> { // Hard line / Fine / Relegate
                            player.moral = (player.moral - 25).coerceAtLeast(5)
                            mgr.reputation += 3
                            addNews("Mánager tomó mano dura: ${player.fullName} sancionado. Hinchas aplauden la disciplina.")
                        }
                        1 -> { // Conversational / Promise
                            player.moral = (player.moral + 15).coerceIn(0, 100)
                            mgr.reputation -= 2
                            addNews("Mánager prometió concesiones a ${player.fullName}. El jugador está más calmado, la prensa lo cataloga de blando.")
                        }
                        2 -> { // Ignore / Normal
                            player.moral = (player.moral - 5).coerceAtLeast(0)
                            addNews("Mánager ignoró la crisis. Menor repercusión pública.")
                        }
                    }
                }
            }

            // Remove decision trigger, replace content with result
            list[postIdx] = post.copy(
                isDecisionTrigger = false,
                content = "${post.content}\n\n[Decisión Tomada: $decisionSelected]"
            )
            socialFeedFlow.value = list
            managerFlow.value = mgr
        }
    }

    // Dynamic Microblogging generator for player egos & disciplines
    fun generateRandomSocialCrisis() {
        val random = Random
        val currentClubs = clubsFlow.value
        val mgr = managerFlow.value

        val activeClub = currentClubs.firstOrNull { it.id == mgr.currentClubId }
        if (activeClub != null && activeClub.squad.isNotEmpty()) {
            val roll = random.nextFloat()
            val newFeed = socialFeedFlow.value.toMutableList()

            when {
                roll < 0.15f -> {
                    // Trigger Superstar Ego conflict if benched or simply randomly
                    val superStar = activeClub.squad.firstOrNull { it.traits.contains(Trait.EGO_DE_SUPERESTRELLA) }
                    if (superStar != null) {
                        val crisisPost = SocialFeedGenerator.generatePostForEvent("EGO_CLASH", activeClub.name, superStar.fullName, superStar.id)
                        newFeed.add(0, crisisPost)
                        addNews("⚠️ ALERTA INTERNA: Conflicto en el vestuario con ${superStar.fullName} publicado en la red social.")
                    }
                }
                roll < 0.30f -> {
                    // Trigger Night out Discipline breach
                    val rowdyPlayer = activeClub.squad.randomOrNull()
                    if (rowdyPlayer != null) {
                        val breachPost = SocialFeedGenerator.generatePostForEvent("DISCIPLINE_BREACH", activeClub.name, rowdyPlayer.fullName, rowdyPlayer.id)
                        newFeed.add(0, breachPost)
                        addNews("⚠️ ESCÁNDALO DE PRENSA: Comportamiento indisciplinado reportado de ${rowdyPlayer.fullName}.")
                    }
                }
                else -> {
                    // Standard fan posts
                    val latestMatch = activeClub.played > 0
                    if (latestMatch) {
                        val won = activeClub.wins > 0 // simple placeholder for latest match result
                        val eventType = if (won) "WIN" else "LOSS"
                        val fanPost = SocialFeedGenerator.generatePostForEvent(eventType, activeClub.name)
                        newFeed.add(0, fanPost)
                    }
                }
            }

            // Trim social feed to keep RAM and performance optimal (max 30 posts)
            if (newFeed.size > 30) {
                socialFeedFlow.value = newFeed.take(30)
            } else {
                socialFeedFlow.value = newFeed
            }
        }
    }
}
