package com.example.engine

import com.example.model.*
import kotlin.random.Random

object TrainingEngine {

    fun runWeeklyTraining(club: Club, currentDateString: String, addNews: (String) -> Unit): WeeklyTrainingReport? {
        val results = mutableListOf<PlayerTrainingResult>()
        val random = Random
        
        val attackCoachLevel = club.coaches.firstOrNull { it.speciality == "Ataque" }?.level ?: 0
        val defenseCoachLevel = club.coaches.firstOrNull { it.speciality == "Defensa" }?.level ?: 0
        val goalkeeperCoachLevel = club.coaches.firstOrNull { it.speciality == "Porteros" }?.level ?: 0
        val fitnessCoachLevel = club.coaches.firstOrNull { it.speciality == "Físico" }?.level ?: 0
        val mentalCoachLevel = club.coaches.firstOrNull { it.speciality == "Mental" }?.level ?: 0
        
        club.squad.forEach { player ->
            val increments = mutableMapOf<String, Int>()
            val decrements = mutableMapOf<String, Int>()
            
            var baseChance = when {
                player.age < 21 -> 18
                player.age < 25 -> 12
                player.age < 29 -> 8
                player.age < 33 -> 4
                else -> 1
            }
            
            baseChance += club.trainingFacilities * 2
            
            val perf = player.matchPerformanceLast
            if (perf >= 8.0f) {
                baseChance += 25
            } else if (perf >= 7.0f) {
                baseChance += 12
            } else if (perf < 5.0f) {
                baseChance -= 5
            }
            
            baseChance = baseChance.coerceAtLeast(1)
            
            // Roll Attack & Midfield
            val attackBoost = attackCoachLevel * 4
            if (random.nextInt(100) < (baseChance + attackBoost)) {
                if (player.attributes.attack < 99) {
                    player.attributes.attack += 1
                    increments["attack"] = 1
                }
            }
            if (random.nextInt(100) < (baseChance + attackBoost)) {
                if (player.attributes.midfield < 99) {
                    player.attributes.midfield += 1
                    increments["midfield"] = 1
                }
            }
            
            // Roll Defense
            val defenseBoost = defenseCoachLevel * 4
            if (random.nextInt(100) < (baseChance + defenseBoost)) {
                if (player.attributes.defense < 99) {
                    player.attributes.defense += 1
                    increments["defense"] = 1
                }
            }
            
            // Roll Goalkeeper
            val gkBoost = goalkeeperCoachLevel * 4
            if (random.nextInt(100) < (baseChance + gkBoost)) {
                if (player.attributes.goalkeeper < 99) {
                    player.attributes.goalkeeper += 1
                    increments["goalkeeper"] = 1
                }
            }
            
            // Roll Speed, Stamina, Physical
            val fitnessBoost = fitnessCoachLevel * 4
            if (random.nextInt(100) < (baseChance + fitnessBoost)) {
                if (player.attributes.speed < 99) {
                    player.attributes.speed += 1
                    increments["speed"] = 1
                }
            }
            if (random.nextInt(100) < (baseChance + fitnessBoost)) {
                if (player.attributes.stamina < 99) {
                    player.attributes.stamina += 1
                    increments["stamina"] = 1
                }
            }
            if (random.nextInt(100) < (baseChance + fitnessBoost)) {
                if (player.attributes.physical < 99) {
                    player.attributes.physical += 1
                    increments["physical"] = 1
                }
            }
            
            // Roll Mental
            val mentalBoost = mentalCoachLevel * 4
            if (random.nextInt(100) < (baseChance + mentalBoost)) {
                if (player.attributes.mental < 99) {
                    player.attributes.mental += 1
                    increments["mental"] = 1
                }
            }
            
            // Veteran physical decline
            if (player.age > 33) {
                if (random.nextInt(100) < 3) {
                    val chosen = listOf("speed", "stamina", "physical").random(random)
                    when (chosen) {
                        "speed" -> {
                            if (player.attributes.speed > 10) {
                                player.attributes.speed -= 1
                                decrements["speed"] = 1
                            }
                        }
                        "stamina" -> {
                            if (player.attributes.stamina > 10) {
                                player.attributes.stamina -= 1
                                decrements["stamina"] = 1
                            }
                        }
                        "physical" -> {
                            if (player.attributes.physical > 10) {
                                player.attributes.physical -= 1
                                decrements["physical"] = 1
                            }
                        }
                    }
                }
            }
            
            if (increments.isNotEmpty() || decrements.isNotEmpty()) {
                results.add(
                    PlayerTrainingResult(
                        playerId = player.id,
                        playerName = player.fullName,
                        position = player.position,
                        scoutingLevel = player.scoutingLevel,
                        increments = increments,
                        decrements = decrements
                    )
                )
            }
        }
        
        return if (results.isNotEmpty()) {
            addNews("🏋️ ENTRENAMIENTO: ${results.size} jugadores de tu plantilla mostraron cambios en su rendimiento semanal.")
            WeeklyTrainingReport(
                weekDate = currentDateString,
                results = results
            )
        } else {
            null
        }
    }
}
