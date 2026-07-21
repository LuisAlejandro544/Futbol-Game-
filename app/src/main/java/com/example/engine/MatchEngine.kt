package com.example.engine

import com.example.model.*
import kotlin.random.Random

object MatchEngine {

    private fun List<Player>.randomWeighted(
        attWeight: Int,
        midWeight: Int,
        defWeight: Int,
        gkWeight: Int
    ): Player {
        val weights = map { player ->
            when (player.position) {
                Position.ATT -> attWeight
                Position.MID -> midWeight
                Position.DEF -> defWeight
                Position.GK -> gkWeight
            }
        }
        val totalWeight = weights.sum()
        if (totalWeight <= 0) return this.random()
        
        var roll = Random.nextInt(totalWeight)
        for (i in indices) {
            roll -= weights[i]
            if (roll < 0) return this[i]
        }
        return this.random()
    }

    // Moved helper functions to MatchStatisticsHelper to achieve a modular and clean architecture

    // Real Match Engine algorithm based on zone tactical comparison
    suspend fun simulateMatch(
        match: Match,
        home: Club,
        away: Club,
        visibility: LeagueVisibility
    ) {
        val random = Random
        
        // Calculate dynamic tactical rating vectors (Attack, Midfield, Defense)
        val (homeDef, homeMid, homeAtt) = home.getTeamRatings()
        val (awayDef, awayMid, awayAtt) = away.getTeamRatings()

        // Home advantage modifier (+5% general efficiency)
        val homeAdvantage = 1.05f

        val recordedHomeGoals = mutableListOf<Pair<Player, Player?>>()
        val recordedAwayGoals = mutableListOf<Pair<Player, Player?>>()

        if (visibility == LeagueVisibility.MAX_DETAIL) {
            // LEVEL 1: Detailed minute-by-minute tactical zone simulation
            val eventsList = mutableListOf<MatchEvent>()
            var homeGoals = 0
            var awayGoals = 0
            var homeShots = 0
            var awayShots = 0
            var homePossessionPercent = 50

            val playerYellowCards = mutableMapOf<String, Int>()
            val redCardedPlayers = mutableSetOf<String>()
            var homeRedCards = 0
            var awayRedCards = 0

            eventsList.add(MatchEvent(1, "¡Comienza el encuentro en el estadio de ${home.name}! Capacidad: ${home.stadiumCapacity} aficionados.", "INFO"))

            // Simulate match blocks (representing key tactical minutes)
            val criticalMinutes = listOf(15, 30, 45, 60, 75, 90)
            criticalMinutes.forEach { min ->
                // Apply red card strength penalties (20% reduction per red card)
                val homeRedPenalty = (1.0 - 0.20 * homeRedCards).coerceIn(0.4, 1.0)
                val awayRedPenalty = (1.0 - 0.20 * awayRedCards).coerceIn(0.4, 1.0)

                val effHomeMid = homeMid * homeAdvantage * homeRedPenalty
                val effAwayMid = awayMid * awayRedPenalty
                val totalMid = (effHomeMid + effAwayMid).coerceAtLeast(1.0)
                homePossessionPercent = ((effHomeMid / totalMid) * 100).toInt().coerceIn(25, 75)

                val effHomeAtt = homeAtt * homeAdvantage * homeRedPenalty
                val effAwayAtt = awayAtt * awayRedPenalty
                val effHomeDef = homeDef * homeAdvantage * homeRedPenalty
                val effAwayDef = awayDef * awayRedPenalty

                // Decide which team dominates midfield control to initiate attack
                val attackRoll = random.nextFloat() * 100
                if (attackRoll < homePossessionPercent) {
                    // Home Attack vs Away Defense
                    homeShots++
                    val attackVal = effHomeAtt * random.nextDouble(0.7, 1.3)
                    val defenseVal = effAwayDef * random.nextDouble(0.7, 1.3)

                    if (attackVal > defenseVal) {
                        // Check if GK triggers "Hero" saving throws
                        val awayGK = away.squad.firstOrNull { it.position == Position.GK && !redCardedPlayers.contains(it.id) }
                        val gkPower = (awayGK?.attributes?.goalkeeper ?: 45) * random.nextDouble(0.8, 1.2)
                        
                        // Trait trigger check!
                        val hasHeroGK = awayGK?.traits?.contains(Trait.HEROE_BAJO_PALOS) == true
                        val thresholdMultiplier = if (hasHeroGK) 1.25f else 1.0f

                        if (gkPower * thresholdMultiplier > attackVal) {
                            if (hasHeroGK) {
                                eventsList.add(MatchEvent(min, "¡PARADÓN EXTRAORDINARIO de ${awayGK?.fullName}! El guardameta activa 'Héroe Bajo Palos' y bloquea el misil.", "SHUTOUT_HERO"))
                            } else {
                                eventsList.add(MatchEvent(min, "Disparo potente de ${home.squad.filter { it.position == Position.ATT && !redCardedPlayers.contains(it.id) }.randomOrNull()?.lastName ?: "delantero"}, pero el guardameta ataja seguro.", "INFO"))
                            }
                        } else {
                            homeGoals++
                            // Generate goalscorer & assister dynamically among non-expelled starters
                            val startersOnPitch = home.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                            val scorer = if (startersOnPitch.isNotEmpty()) startersOnPitch.randomWeighted(12, 6, 1, 0) else home.squad.random()
                            
                            val assister = if (random.nextFloat() < 0.70f) {
                                val eligibleAssisters = startersOnPitch.filter { it.id != scorer.id }
                                if (eligibleAssisters.isNotEmpty()) eligibleAssisters.randomWeighted(5, 12, 3, 1) else null
                            } else null
                            recordedHomeGoals.add(scorer to assister)

                            val commentary = if (assister != null) {
                                "⚽ ¡GOOOOOL DE ${home.name}! Excelente definición de ${scorer.fullName} tras una asistencia magistral de ${assister.fullName}."
                            } else {
                                "⚽ ¡GOOOOOL DE ${home.name}! ¡GOLAZO individual de ${scorer.fullName}! Superó a la defensa y remató con potencia al ángulo."
                            }
                            eventsList.add(MatchEvent(min, commentary, "GOAL_HOME"))
                        }
                    } else {
                        eventsList.add(MatchEvent(min, "Contraataque peligroso del ${home.name} cortado magníficamente por la defensa rival.", "INFO"))
                    }
                } else {
                    // Away Attack vs Home Defense
                    awayShots++
                    val attackVal = effAwayAtt * random.nextDouble(0.7, 1.3)
                    val defenseVal = effHomeDef * random.nextDouble(0.7, 1.3)

                    if (attackVal > defenseVal) {
                        val homeGK = home.squad.firstOrNull { it.position == Position.GK && !redCardedPlayers.contains(it.id) }
                        val gkPower = (homeGK?.attributes?.goalkeeper ?: 45) * random.nextDouble(0.8, 1.2)
                        val hasHeroGK = homeGK?.traits?.contains(Trait.HEROE_BAJO_PALOS) == true
                        val thresholdMultiplier = if (hasHeroGK) 1.25f else 1.0f

                        if (gkPower * thresholdMultiplier > attackVal) {
                            if (hasHeroGK) {
                                eventsList.add(MatchEvent(min, "¡SALVADA MONUMENTAL! El arquero local ${homeGK?.fullName} vuela bloqueando un disparo cantado gracias a su rasgo 'Héroe Bajo Palos'.", "SHUTOUT_HERO"))
                            } else {
                                eventsList.add(MatchEvent(min, "Remate de cabeza peligroso del ${away.name}, pero el arquero local desvía a córner.", "INFO"))
                            }
                        } else {
                            awayGoals++
                            // Generate goalscorer & assister dynamically among non-expelled starters
                            val startersOnPitch = away.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                            val scorer = if (startersOnPitch.isNotEmpty()) startersOnPitch.randomWeighted(12, 6, 1, 0) else away.squad.random()
                            
                            val assister = if (random.nextFloat() < 0.70f) {
                                val eligibleAssisters = startersOnPitch.filter { it.id != scorer.id }
                                if (eligibleAssisters.isNotEmpty()) eligibleAssisters.randomWeighted(5, 12, 3, 1) else null
                            } else null
                            recordedAwayGoals.add(scorer to assister)

                            val commentary = if (assister != null) {
                                "⚽ ¡GOOOOOL DE ${away.name}! Excelente definición de ${scorer.fullName} tras una asistencia magistral de ${assister.fullName}."
                            } else {
                                "⚽ ¡GOOOOOL DE ${away.name}! ¡GOLAZO individual de ${scorer.fullName}! Superó a la defensa y remató con potencia al ángulo."
                            }
                            eventsList.add(MatchEvent(min, commentary, "GOAL_AWAY"))
                        }
                    } else {
                        eventsList.add(MatchEvent(min, "Balón largo del ${away.name} que se pierde por la línea de fondo por falta de coordinación.", "INFO"))
                    }
                }

                // Random booking card check (15% chance per critical segment)
                if (random.nextFloat() < 0.15f) {
                    val bookingTeam = if (random.nextBoolean()) home else away
                    val eligiblePlayers = bookingTeam.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                    if (eligiblePlayers.isNotEmpty()) {
                        val bookedPlayer = eligiblePlayers.random()
                        val currentYellows = (playerYellowCards[bookedPlayer.id] ?: 0) + 1
                        playerYellowCards[bookedPlayer.id] = currentYellows

                        if (currentYellows >= 2) {
                            redCardedPlayers.add(bookedPlayer.id)
                            if (bookingTeam.id == home.id) homeRedCards++ else awayRedCards++
                            
                            eventsList.add(MatchEvent(
                                min,
                                "🟥 ¡EXPULSIÓN! ${bookedPlayer.fullName} recibe su segunda tarjeta amarilla y es expulsado. ${bookingTeam.name} se queda con ${11 - (if (bookingTeam.id == home.id) homeRedCards else awayRedCards)} jugadores.",
                                "RED"
                            ))
                        } else {
                            eventsList.add(MatchEvent(
                                min,
                                "🟨 Tarjeta amarilla para ${bookedPlayer.fullName} del ${bookingTeam.name} de juego fuerte.",
                                "YELLOW"
                            ))
                        }
                    }
                }

                // Random injury check (8% chance per critical segment)
                if (random.nextFloat() < 0.08f) {
                    val injuryTeam = if (random.nextBoolean()) home else away
                    val eligiblePlayers = injuryTeam.squad.filter { it.isStarter && !it.isInjured && !redCardedPlayers.contains(it.id) }
                    if (eligiblePlayers.isNotEmpty()) {
                        val injuredPlayer = eligiblePlayers.random()
                        val injuryNames = listOf(
                            "Esguince de Tobillo",
                            "Rotura Fibrilar de Isquiotibiales",
                            "Sobrecarga Muscular",
                            "Contusión de Rodilla",
                            "Distensión de Ligamentos"
                        )
                        val injuryName = injuryNames.random(random)
                        val weeks = random.nextInt(1, 4) // 1 to 3 weeks
                        injuredPlayer.applyInjury(weeks, injuryName)

                        eventsList.add(MatchEvent(
                            min,
                            "🚑 ¡ALERTA MÉDICA! ${injuredPlayer.fullName} del ${injuryTeam.name} sufre una lesión ($injuryName) y debe ser retirado ($weeks sem. de baja).",
                            "INJURY"
                        ))
                    }
                }

                // Energy depletion at milestones (at halftime block 45, and final whistle block 90)
                if (min == 45) {
                    val activeHomeStarters = home.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                    activeHomeStarters.forEach { p ->
                        val loss = if (p.traits.contains(Trait.PULMON_INFINITO)) random.nextInt(6, 10) else random.nextInt(10, 16)
                        p.energy = (p.energy - loss).coerceIn(15, 100)
                    }
                    val activeAwayStarters = away.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                    activeAwayStarters.forEach { p ->
                        val loss = if (p.traits.contains(Trait.PULMON_INFINITO)) random.nextInt(6, 10) else random.nextInt(10, 16)
                        p.energy = (p.energy - loss).coerceIn(15, 100)
                    }
                }

                if (min == 90) {
                    val activeHomeStarters = home.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                    activeHomeStarters.forEach { p ->
                        val loss = if (p.traits.contains(Trait.PULMON_INFINITO)) random.nextInt(6, 10) else random.nextInt(10, 16)
                        p.energy = (p.energy - loss).coerceIn(15, 100)
                    }
                    val activeAwayStarters = away.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                    activeAwayStarters.forEach { p ->
                        val loss = if (p.traits.contains(Trait.PULMON_INFINITO)) random.nextInt(6, 10) else random.nextInt(10, 16)
                        p.energy = (p.energy - loss).coerceIn(15, 100)
                    }
                }
            }

            eventsList.add(MatchEvent(90, "¡Pitido final! El colegiado decreta el término del cotejo. Marcador final: ${home.name} $homeGoals - $awayGoals ${away.name}.", "INFO"))

            // Apply results to Match Object
            match.played = true
            match.homeGoals = homeGoals
            match.awayGoals = awayGoals
            match.possessionHome = homePossessionPercent
            match.possessionAway = 100 - homePossessionPercent
            match.homeShots = homeShots
            match.awayShots = awayShots
            match.events = eventsList

            // Apply to Club Standings (reduceEnergy = false because we already processed it on 45/90 blocks)
            MatchStatisticsHelper.applyMatchResultsToClubs(home, away, homeGoals, awayGoals, reduceEnergy = false)
            MatchStatisticsHelper.assignPlayerRatingsAndStats(match, home, away, homeGoals, awayGoals, recordedHomeGoals, recordedAwayGoals)

        } else {
            // LEVEL 2 & 3: Fast Probability Statistical simulation (No minute-by-minute calculations)
            val homeStrength = (homeDef + homeMid + homeAtt) * homeAdvantage
            val awayStrength = (awayDef + awayMid + awayAtt).toFloat()

            val homeExpected = (homeStrength / (homeStrength + awayStrength)) * 3.0f + random.nextFloat() * 1.5f
            val awayExpected = (awayStrength / (homeStrength + awayStrength)) * 3.0f + random.nextFloat() * 1.5f

            val homeGoals = homeExpected.toInt().coerceAtLeast(0)
            val awayGoals = awayExpected.toInt().coerceAtLeast(0)

            match.played = true
            match.homeGoals = homeGoals
            match.awayGoals = awayGoals
            match.homeShots = homeGoals + random.nextInt(2, 8)
            match.awayShots = awayGoals + random.nextInt(2, 8)
            match.possessionHome = if (homeStrength > awayStrength) 55 else 45
            match.possessionAway = 100 - match.possessionHome

            val homeGoalsList = MatchStatisticsHelper.distributeGoalsAndAssists(home, homeGoals)
            val awayGoalsList = MatchStatisticsHelper.distributeGoalsAndAssists(away, awayGoals)

            MatchStatisticsHelper.applyMatchResultsToClubs(home, away, homeGoals, awayGoals, reduceEnergy = true)
            MatchStatisticsHelper.assignPlayerRatingsAndStats(match, home, away, homeGoals, awayGoals, homeGoalsList, awayGoalsList)
        }
    }

    fun reSimulateSecondHalf(match: Match, home: Club, away: Club) {
        val random = Random
        
        // 1. Revert previous match standings impact
        val origHomeGoals = match.homeGoals
        val origAwayGoals = match.awayGoals
        
        home.played = (home.played - 1).coerceAtLeast(0)
        away.played = (away.played - 1).coerceAtLeast(0)
        home.goalsFor = (home.goalsFor - origHomeGoals).coerceAtLeast(0)
        home.goalsAgainst = (home.goalsAgainst - origAwayGoals).coerceAtLeast(0)
        away.goalsFor = (away.goalsFor - origAwayGoals).coerceAtLeast(0)
        away.goalsAgainst = (away.goalsAgainst - origHomeGoals).coerceAtLeast(0)
        
        when {
            origHomeGoals > origAwayGoals -> {
                home.wins = (home.wins - 1).coerceAtLeast(0)
                home.points = (home.points - 3).coerceAtLeast(0)
                away.losses = (away.losses - 1).coerceAtLeast(0)
            }
            origHomeGoals < origAwayGoals -> {
                away.wins = (away.wins - 1).coerceAtLeast(0)
                away.points = (away.points - 3).coerceAtLeast(0)
                home.losses = (home.losses - 1).coerceAtLeast(0)
            }
            else -> {
                home.draws = (home.draws - 1).coerceAtLeast(0)
                home.points = (home.points - 1).coerceAtLeast(0)
                away.draws = (away.draws - 1).coerceAtLeast(0)
                away.points = (away.points - 1).coerceAtLeast(0)
            }
        }
        
        // Revert player goals, assists, saves assigned during previous simulation
        match.playerStats.forEach { stat ->
            val player = home.squad.firstOrNull { it.id == stat.playerId } ?: away.squad.firstOrNull { it.id == stat.playerId }
            if (player != null) {
                player.goals = (player.goals - stat.goals).coerceAtLeast(0)
                player.assists = (player.assists - stat.assists).coerceAtLeast(0)
                player.saves = (player.saves - stat.saves).coerceAtLeast(0)
            }
        }

        // Restore second half stamina energy that was deducted in previous simulation
        val restoreLoss = 12 // average estimated second half loss to restore before re-simulating
        home.squad.forEach { p ->
            if (p.isStarter) {
                p.energy = (p.energy + restoreLoss).coerceAtMost(100)
            }
        }
        away.squad.forEach { p ->
            if (p.isStarter) {
                p.energy = (p.energy + restoreLoss).coerceAtMost(100)
            }
        }
        
        // 2. Extract first-half events (minute <= 45)
        val firstHalfEvents = match.events.filter { it.minute <= 45 }.toMutableList()
        val firstHalfHomeGoals = firstHalfEvents.count { it.type == "GOAL_HOME" }
        val firstHalfAwayGoals = firstHalfEvents.count { it.type == "GOAL_AWAY" }
        
        var homeGoals = firstHalfHomeGoals
        var awayGoals = firstHalfAwayGoals
        var homeShots = match.homeShots / 2
        var awayShots = match.awayShots / 2
        
        val recordedHomeGoals = mutableListOf<Pair<Player, Player?>>()
        val recordedAwayGoals = mutableListOf<Pair<Player, Player?>>()
        
        val playerYellowCards = mutableMapOf<String, Int>()
        val redCardedPlayers = mutableSetOf<String>()
        
        firstHalfEvents.forEach { ev ->
            if (ev.type == "GOAL_HOME") {
                val scorer = home.squad.firstOrNull { ev.description.contains(it.lastName) || ev.description.contains(it.fullName) }
                    ?: home.squad.filter { it.isStarter }.randomOrNull() ?: home.squad.random()
                val assister = home.squad.firstOrNull { it.id != scorer.id && (ev.description.contains(it.lastName) || ev.description.contains(it.fullName)) }
                recordedHomeGoals.add(scorer to assister)
            } else if (ev.type == "GOAL_AWAY") {
                val scorer = away.squad.firstOrNull { ev.description.contains(it.lastName) || ev.description.contains(it.fullName) }
                    ?: away.squad.filter { it.isStarter }.randomOrNull() ?: away.squad.random()
                val assister = away.squad.firstOrNull { it.id != scorer.id && (ev.description.contains(it.lastName) || ev.description.contains(it.fullName)) }
                recordedAwayGoals.add(scorer to assister)
            } else if (ev.type == "YELLOW") {
                val carded = (home.squad + away.squad).firstOrNull { ev.description.contains(it.lastName) || ev.description.contains(it.fullName) }
                if (carded != null) {
                    playerYellowCards[carded.id] = (playerYellowCards[carded.id] ?: 0) + 1
                }
            } else if (ev.type == "RED") {
                val carded = (home.squad + away.squad).firstOrNull { ev.description.contains(it.lastName) || ev.description.contains(it.fullName) }
                if (carded != null) {
                    redCardedPlayers.add(carded.id)
                }
            }
        }
        
        // 3. Re-calculate team ratings with the CURRENT roster (including substitutions)
        val (homeDef, homeMid, homeAtt) = home.getTeamRatings()
        val (awayDef, awayMid, awayAtt) = away.getTeamRatings()
        val homeAdvantage = 1.05f
        
        var homeRedCards = home.squad.count { redCardedPlayers.contains(it.id) }
        var awayRedCards = away.squad.count { redCardedPlayers.contains(it.id) }
        
        val secondHalfEvents = mutableListOf<MatchEvent>()
        val criticalMinutes = listOf(60, 75, 90)
        
        criticalMinutes.forEach { min ->
            val homeRedPenalty = (1.0 - 0.20 * homeRedCards).coerceIn(0.4, 1.0)
            val awayRedPenalty = (1.0 - 0.20 * awayRedCards).coerceIn(0.4, 1.0)
            
            val effHomeMid = homeMid * homeAdvantage * homeRedPenalty
            val effAwayMid = awayMid * awayRedPenalty
            val totalMid = (effHomeMid + effAwayMid).coerceAtLeast(1.0)
            val homePossessionPercent = ((effHomeMid / totalMid) * 100).toInt().coerceIn(25, 75)
            
            val effHomeAtt = homeAtt * homeAdvantage * homeRedPenalty
            val effAwayAtt = awayAtt * awayRedPenalty
            val effHomeDef = homeDef * homeAdvantage * homeRedPenalty
            val effAwayDef = awayDef * awayRedPenalty
            
            val attackRoll = random.nextFloat() * 100
            if (attackRoll < homePossessionPercent) {
                homeShots++
                val attackVal = effHomeAtt * random.nextDouble(0.7, 1.3)
                val defenseVal = effAwayDef * random.nextDouble(0.7, 1.3)
                
                if (attackVal > defenseVal) {
                    val awayGK = away.squad.firstOrNull { it.position == Position.GK && !redCardedPlayers.contains(it.id) }
                    val gkPower = (awayGK?.attributes?.goalkeeper ?: 45) * random.nextDouble(0.8, 1.2)
                    val hasHeroGK = awayGK?.traits?.contains(Trait.HEROE_BAJO_PALOS) == true
                    val thresholdMultiplier = if (hasHeroGK) 1.25f else 1.0f
                    
                    if (gkPower * thresholdMultiplier > attackVal) {
                        if (hasHeroGK) {
                            secondHalfEvents.add(MatchEvent(min, "¡PARADÓN EXTRAORDINARIO de ${awayGK?.fullName}! El guardameta activa 'Héroe Bajo Palos' y bloquea el misil.", "SHUTOUT_HERO"))
                        } else {
                            secondHalfEvents.add(MatchEvent(min, "Disparo potente, pero el guardameta de ${away.name} ataja seguro.", "INFO"))
                        }
                    } else {
                        homeGoals++
                        val startersOnPitch = home.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                        val scorer = if (startersOnPitch.isNotEmpty()) startersOnPitch.randomWeighted(12, 6, 1, 0) else home.squad.random()
                        
                        val eligibleAssisters = startersOnPitch.filter { it.id != scorer.id }
                        val assister = if (random.nextFloat() < 0.70f && eligibleAssisters.isNotEmpty()) {
                            eligibleAssisters.randomWeighted(5, 12, 3, 1)
                        } else null
                        
                        recordedHomeGoals.add(scorer to assister)
                        
                        val commentary = if (assister != null) {
                            "⚽ ¡GOOOOOL DE ${home.name}! Excelente definición de ${scorer.fullName} tras una asistencia magistral de ${assister.fullName}."
                        } else {
                            "⚽ ¡GOOOOOL DE ${home.name}! ¡GOLAZO individual de ${scorer.fullName}! Superó a la defensa y remató con potencia al ángulo."
                        }
                        secondHalfEvents.add(MatchEvent(min, commentary, "GOAL_HOME"))
                    }
                } else {
                    secondHalfEvents.add(MatchEvent(min, "Contraataque peligroso del ${home.name} cortado magníficamente por la defensa rival.", "INFO"))
                }
            } else {
                awayShots++
                val attackVal = effAwayAtt * random.nextDouble(0.7, 1.3)
                val defenseVal = effHomeDef * random.nextDouble(0.7, 1.3)
                
                if (attackVal > defenseVal) {
                    val homeGK = home.squad.firstOrNull { it.position == Position.GK && !redCardedPlayers.contains(it.id) }
                    val gkPower = (homeGK?.attributes?.goalkeeper ?: 45) * random.nextDouble(0.8, 1.2)
                    val hasHeroGK = homeGK?.traits?.contains(Trait.HEROE_BAJO_PALOS) == true
                    val thresholdMultiplier = if (hasHeroGK) 1.25f else 1.0f
                    
                    if (gkPower * thresholdMultiplier > attackVal) {
                        if (hasHeroGK) {
                            secondHalfEvents.add(MatchEvent(min, "¡SALVADA MONUMENTAL! El arquero local ${homeGK?.fullName} vuela bloqueando un disparo cantado gracias a su rasgo 'Héroe Bajo Palos'.", "SHUTOUT_HERO"))
                        } else {
                            secondHalfEvents.add(MatchEvent(min, "Remate de cabeza de ${away.name} que el arquero local desvía con apuros.", "INFO"))
                        }
                    } else {
                        awayGoals++
                        val startersOnPitch = away.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                        val scorer = if (startersOnPitch.isNotEmpty()) startersOnPitch.randomWeighted(12, 6, 1, 0) else away.squad.random()
                        
                        val eligibleAssisters = startersOnPitch.filter { it.id != scorer.id }
                        val assister = if (random.nextFloat() < 0.70f && eligibleAssisters.isNotEmpty()) {
                            eligibleAssisters.randomWeighted(5, 12, 3, 1)
                        } else null
                        
                        recordedAwayGoals.add(scorer to assister)
                        
                        val commentary = if (assister != null) {
                            "⚽ ¡GOOOOOL DE ${away.name}! Excelente definición de ${scorer.fullName} tras una asistencia magistral de ${assister.fullName}."
                        } else {
                            "⚽ ¡GOOOOOL DE ${away.name}! ¡GOLAZO individual de ${scorer.fullName}! Superó a la defensa y remató con potencia al ángulo."
                        }
                        secondHalfEvents.add(MatchEvent(min, commentary, "GOAL_AWAY"))
                    }
                } else {
                    secondHalfEvents.add(MatchEvent(min, "Balón largo del ${away.name} que se pierde por la línea lateral.", "INFO"))
                }
            }
            
            // Random card check (15% chance per critical segment)
            if (random.nextFloat() < 0.15f) {
                val bookingTeam = if (random.nextBoolean()) home else away
                val eligiblePlayers = bookingTeam.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                if (eligiblePlayers.isNotEmpty()) {
                    val bookedPlayer = eligiblePlayers.random()
                    val currentYellows = (playerYellowCards[bookedPlayer.id] ?: 0) + 1
                    playerYellowCards[bookedPlayer.id] = currentYellows
                    
                    if (currentYellows >= 2) {
                        redCardedPlayers.add(bookedPlayer.id)
                        if (bookingTeam.id == home.id) homeRedCards++ else awayRedCards++
                        
                        secondHalfEvents.add(MatchEvent(
                            min,
                            "🟥 ¡EXPULSIÓN! ${bookedPlayer.fullName} recibe su segunda tarjeta amarilla y es expulsado. ${bookingTeam.name} se queda con ${11 - (if (bookingTeam.id == home.id) homeRedCards else awayRedCards)} jugadores.",
                            "RED"
                        ))
                    } else {
                        secondHalfEvents.add(MatchEvent(
                            min,
                            "🟨 Tarjeta amarilla para ${bookedPlayer.fullName} del ${bookingTeam.name} de juego fuerte.",
                            "YELLOW"
                        ))
                    }
                }
            }

            // Energy depletion at minute 90 for the players on the field
            if (min == 90) {
                val activeHomeStarters = home.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                activeHomeStarters.forEach { p ->
                    val loss = if (p.traits.contains(Trait.PULMON_INFINITO)) random.nextInt(6, 10) else random.nextInt(10, 16)
                    p.energy = (p.energy - loss).coerceIn(15, 100)
                }
                val activeAwayStarters = away.squad.filter { it.isStarter && !redCardedPlayers.contains(it.id) }
                activeAwayStarters.forEach { p ->
                    val loss = if (p.traits.contains(Trait.PULMON_INFINITO)) random.nextInt(6, 10) else random.nextInt(10, 16)
                    p.energy = (p.energy - loss).coerceIn(15, 100)
                }
            }
        }
        
        secondHalfEvents.add(MatchEvent(90, "¡Pitido final! El colegiado decreta el término del cotejo. Marcador final: ${home.name} $homeGoals - $awayGoals ${away.name}.", "INFO"))
        
        // 4. Update match properties
        match.homeGoals = homeGoals
        match.awayGoals = awayGoals
        match.homeShots = homeShots
        match.awayShots = awayShots
        match.events = firstHalfEvents + secondHalfEvents
        
        // 5. Re-apply new final match results to Club standings
        MatchStatisticsHelper.applyMatchResultsToClubs(home, away, homeGoals, awayGoals, reduceEnergy = false)
        MatchStatisticsHelper.assignPlayerRatingsAndStats(match, home, away, homeGoals, awayGoals, recordedHomeGoals + recordedAwayGoals, recordedAwayGoals)
    }
}
