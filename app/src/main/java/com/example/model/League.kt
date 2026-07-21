package com.example.model

import com.squareup.moshi.JsonClass
import java.util.UUID
import kotlin.random.Random

enum class LeagueVisibility {
    MAX_DETAIL,      // Level 1: Minutely simulated matches, tactical zone comparison
    MEDIUM_DETAIL,   // Level 2: Simulated with fast statistical probability, controls transfers
    ZERO_DETAIL      // Level 3: Passive national teams, on-demand generation when queried
}

@JsonClass(generateAdapter = true)
data class MatchEvent(
    val minute: Int,
    val description: String,
    val type: String // "GOAL_HOME", "GOAL_AWAY", "YELLOW_HOME", "RED_AWAY", "INFO", "SHUTOUT_HERO"
)

@JsonClass(generateAdapter = true)
data class MatchPlayerStat(
    val playerId: String,
    val playerName: String,
    val clubId: String,
    val goals: Int,
    val assists: Int,
    val rating: Float,
    val isGoalkeeper: Boolean = false,
    val saves: Int = 0,
    val playerSpecialty: String = "",
    val playerFoot: String = "",
    val playerHeightCm: Int = 0,
    val playerPersonality: String = ""
)

@JsonClass(generateAdapter = true)
data class Match(
    val id: String = UUID.randomUUID().toString(),
    val homeClubId: String,
    val homeClubName: String,
    val awayClubId: String,
    val awayClubName: String,
    var played: Boolean = false,
    var homeGoals: Int = 0,
    var awayGoals: Int = 0,
    var possessionHome: Int = 50,
    var possessionAway: Int = 50,
    var homeShots: Int = 0,
    var awayShots: Int = 0,
    var events: List<MatchEvent> = emptyList(),
    var playerStats: List<MatchPlayerStat> = emptyList()
)

@JsonClass(generateAdapter = true)
data class FixtureRound(
    val roundNumber: Int,
    val matches: List<Match>
)

@JsonClass(generateAdapter = true)
data class League(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val country: String,
    var visibility: LeagueVisibility,
    val clubs: List<Club>,
    var fixtures: List<FixtureRound> = emptyList(),
    var currentRound: Int = 0
) {
    fun generateSchedule() {
        val list = clubs.toMutableList()
        if (list.size % 2 != 0) {
            // Cannot build round-robin directly if odd, but our generator uses even clubs
            return
        }

        val numClubs = list.size
        val numRounds = (numClubs - 1) * 2 // Home and Away fixtures
        val rounds = mutableListOf<FixtureRound>()

        // Using Berger Tables / Circle Method for round-robin scheduling
        val halfSize = numClubs / 2
        val clubsList = list.toList()

        for (round in 0 until numRounds) {
            val matches = mutableListOf<Match>()
            val isSecondHalf = round >= (numClubs - 1)
            val effectiveRound = round % (numClubs - 1)

            for (i in 0 until halfSize) {
                val homeIdx = (effectiveRound + i) % (numClubs - 1)
                var awayIdx = (effectiveRound + numClubs - 1 - i) % (numClubs - 1)
                
                if (i == 0) {
                    awayIdx = numClubs - 1
                }

                val home = clubsList[homeIdx]
                val away = clubsList[awayIdx]

                if (!isSecondHalf) {
                    // Match 1: Home vs Away
                    matches.add(Match(homeClubId = home.id, homeClubName = home.name, awayClubId = away.id, awayClubName = away.name))
                } else {
                    // Match 2: Reverse Home/Away
                    matches.add(Match(homeClubId = away.id, homeClubName = away.name, awayClubId = home.id, awayClubName = home.name))
                }
            }
            rounds.add(FixtureRound(roundNumber = round + 1, matches = matches))
        }

        this.fixtures = rounds
        this.currentRound = 0
    }
}
