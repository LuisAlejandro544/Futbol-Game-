package com.example.engine

import com.example.model.*
import kotlin.random.Random

object MatchStatisticsHelper {

    fun List<Player>.randomWeighted(
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

    fun distributeGoalsAndAssists(club: Club, numGoals: Int): List<Pair<Player, Player?>> {
        val results = mutableListOf<Pair<Player, Player?>>()
        val squad = club.squad
        if (squad.isEmpty()) return results

        repeat(numGoals) {
            val scorer = squad.randomWeighted(12, 6, 1, 0)
            val hasAssist = Random.nextFloat() < 0.70f
            val assister = if (hasAssist) {
                squad.filter { it.id != scorer.id }.randomWeighted(5, 12, 3, 1)
            } else {
                null
            }
            results.add(Pair(scorer, assister))
        }
        return results
    }

    fun assignPlayerRatingsAndStats(
         match: Match,
         home: Club,
         away: Club,
         homeGoals: Int,
         awayGoals: Int,
         homeGoalsList: List<Pair<Player, Player?>>,
         awayGoalsList: List<Pair<Player, Player?>>
     ) {
        val homeSaves = (match.awayShots - awayGoals).coerceAtLeast(0)
        val awaySaves = (match.homeShots - homeGoals).coerceAtLeast(0)

         val homeStats = home.squad.map { player ->
             val playerMatchGoals = homeGoalsList.count { it.first.id == player.id }
             val playerMatchAssists = homeGoalsList.count { it.second?.id == player.id }
             val playerMatchSaves = if (player.position == Position.GK) homeSaves else 0
             
             var rating = 6.0f + Random.nextDouble(-0.5, 1.5).toFloat()
             if (homeGoals > awayGoals) rating += 0.5f
             if (homeGoals < awayGoals) rating -= 0.5f
             
             val overallRatio = player.getOverallRating().toFloat() / 75f
             rating += (overallRatio - 1.0f) * 1.2f
             
             rating += playerMatchGoals * 1.2f
             rating += playerMatchAssists * 0.8f
             
             if (player.position == Position.GK) {
                 if (awayGoals == 0) rating += 1.5f
                 else rating -= (awayGoals * 0.3f)
                 rating += playerMatchSaves * 0.2f
             } else if (player.position == Position.DEF) {
                 if (awayGoals == 0) rating += 0.8f
             }
             
             val finalRating = rating.coerceIn(3.0f, 10.0f)
             val roundedRating = (Math.round(finalRating * 10f) / 10f)
             player.goals += playerMatchGoals
             player.assists += playerMatchAssists
             player.saves += playerMatchSaves
             player.matchPerformanceLast = roundedRating
             
             MatchPlayerStat(
                 playerId = player.id,
                 playerName = player.fullName,
                 clubId = home.id,
                 goals = playerMatchGoals,
                 assists = playerMatchAssists,
                 rating = roundedRating,
                 isGoalkeeper = player.position == Position.GK,
                 saves = playerMatchSaves,
                 playerSpecialty = player.specialty,
                 playerFoot = player.preferredFoot,
                 playerHeightCm = player.heightCm,
                 playerPersonality = player.personality
             )
         }

         val awayStats = away.squad.map { player ->
             val playerMatchGoals = awayGoalsList.count { it.first.id == player.id }
             val playerMatchAssists = awayGoalsList.count { it.second?.id == player.id }
             val playerMatchSaves = if (player.position == Position.GK) awaySaves else 0
             
             var rating = 6.0f + Random.nextDouble(-0.5, 1.5).toFloat()
             if (awayGoals > homeGoals) rating += 0.5f
             if (awayGoals < homeGoals) rating -= 0.5f
             
             val overallRatio = player.getOverallRating().toFloat() / 75f
             rating += (overallRatio - 1.0f) * 1.2f
             
             rating += playerMatchGoals * 1.2f
             rating += playerMatchAssists * 0.8f
             
             if (player.position == Position.GK) {
                 if (homeGoals == 0) rating += 1.5f
                 else rating -= (homeGoals * 0.3f)
                 rating += playerMatchSaves * 0.2f
             } else if (player.position == Position.DEF) {
                 if (homeGoals == 0) rating += 0.8f
             }
             
             val finalRating = rating.coerceIn(3.0f, 10.0f)
             val roundedRating = (Math.round(finalRating * 10f) / 10f)
             player.goals += playerMatchGoals
             player.assists += playerMatchAssists
             player.saves += playerMatchSaves
             player.matchPerformanceLast = roundedRating
             
             MatchPlayerStat(
                 playerId = player.id,
                 playerName = player.fullName,
                 clubId = away.id,
                 goals = playerMatchGoals,
                 assists = playerMatchAssists,
                 rating = roundedRating,
                 isGoalkeeper = player.position == Position.GK,
                 saves = playerMatchSaves,
                 playerSpecialty = player.specialty,
                 playerFoot = player.preferredFoot,
                 playerHeightCm = player.heightCm,
                 playerPersonality = player.personality
             )
         }

         match.playerStats = homeStats + awayStats
    }

    fun applyMatchResultsToClubs(home: Club, away: Club, homeGoals: Int, awayGoals: Int, reduceEnergy: Boolean = true) {
        home.played++
        away.played++
        home.goalsFor += homeGoals
        home.goalsAgainst += awayGoals
        away.goalsFor += awayGoals
        away.goalsAgainst += homeGoals

        when {
            homeGoals > awayGoals -> {
                home.wins++
                home.points += 3
                away.losses++
            }
            homeGoals < awayGoals -> {
                away.wins++
                away.points += 3
                home.losses++
            }
            else -> {
                home.draws++
                home.points += 1
                away.draws++
                away.points += 1
            }
        }

        if (reduceEnergy) {
            home.squad.forEach { player ->
                player.energy = (player.energy - Random.nextInt(5, 12)).coerceAtLeast(15)
                val injuryProb = if (player.traits.contains(Trait.CUERPO_DE_CRISTAL)) 0.06f else 0.02f
                if (Random.nextFloat() < injuryProb) {
                    player.isInjured = true
                    player.injuryDurationWeeks = Random.nextInt(1, 4)
                }
            }
            away.squad.forEach { player ->
                player.energy = (player.energy - Random.nextInt(5, 12)).coerceAtLeast(15)
                val injuryProb = if (player.traits.contains(Trait.CUERPO_DE_CRISTAL)) 0.06f else 0.02f
                if (Random.nextFloat() < injuryProb) {
                    player.isInjured = true
                    player.injuryDurationWeeks = Random.nextInt(1, 4)
                }
            }
        } else {
            home.squad.forEach { player ->
                val injuryProb = if (player.traits.contains(Trait.CUERPO_DE_CRISTAL)) 0.06f else 0.02f
                if (Random.nextFloat() < injuryProb) {
                    player.isInjured = true
                    player.injuryDurationWeeks = Random.nextInt(1, 4)
                }
            }
            away.squad.forEach { player ->
                val injuryProb = if (player.traits.contains(Trait.CUERPO_DE_CRISTAL)) 0.06f else 0.02f
                if (Random.nextFloat() < injuryProb) {
                    player.isInjured = true
                    player.injuryDurationWeeks = Random.nextInt(1, 4)
                }
            }
        }
    }
}
