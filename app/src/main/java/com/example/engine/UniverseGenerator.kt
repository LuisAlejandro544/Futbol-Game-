package com.example.engine

import com.example.model.*
import kotlin.random.Random

object UniverseGenerator {

    fun initializeUniverse(managerName: String, useFictionalNames: Boolean = false): Triple<List<Country>, List<League>, List<Club>> {
        // Generate countries (Latin America + France)
        val generatedCountries = Country.generateUniverse()
        val generatedLigas = mutableListOf<League>()
        val allClubs = mutableListOf<Club>()

        // Generate 6 clubs for each country
        generatedCountries.forEachIndexed { countryIndex, country ->
            val clubList = mutableListOf<Club>()

            // 6 clubs per country
            repeat(6) { clubIndex ->
                val club = Club.generateProcedural(country, clubIndex, minStars = 2, maxStars = 4, useFictionalNames = useFictionalNames)
                clubList.add(club)
                allClubs.add(club)
            }

            val leagueName = "Liga Profesional de ${country.name}"
            val league = League(
                name = leagueName,
                country = country.name,
                visibility = LeagueVisibility.ZERO_DETAIL, // Zero detail by default; active selected league gets MAX_DETAIL
                clubs = clubList
            )
            league.generateSchedule()
            generatedLigas.add(league)
        }
        return Triple(generatedCountries, generatedLigas, allClubs)
    }

    fun startCareerWithCustomClub(
        customClubName: String,
        countryName: String,
        stadiumCapacity: Int,
        budget: Long,
        currentClubs: List<Club>,
        currentLigas: List<League>
    ): Pair<List<Club>, List<League>> {
        val starPower = 3
        val baseWage = (starPower * 90_000L).toLong()
        val fanBase = (stadiumCapacity * 1.8).toLong()
        val ticketPrice = 12

        val customClub = Club(
            name = customClubName,
            country = countryName,
            budget = budget,
            wageBudget = baseWage,
            stadiumCapacity = stadiumCapacity,
            fanBaseSize = fanBase,
            ticketPrice = ticketPrice,
            trainingFacilities = starPower,
            youthAcademy = starPower
        )

        // Generate squad of 18 players
        val minRating = 45
        val maxRating = 72
        repeat(2) { customClub.squad.add(Player.generateProcedural(countryName, Position.GK, minRating, maxRating)) }
        repeat(6) { customClub.squad.add(Player.generateProcedural(countryName, Position.DEF, minRating, maxRating)) }
        repeat(6) { customClub.squad.add(Player.generateProcedural(countryName, Position.MID, minRating, maxRating)) }
        repeat(4) { customClub.squad.add(Player.generateProcedural(countryName, Position.ATT, minRating, maxRating)) }

        var updatedClubs = currentClubs
        val league = currentLigas.firstOrNull { it.country == countryName }
        val updatedLigas = currentLigas.toMutableList()

        if (league != null) {
            val updatedLeagueClubs = league.clubs.toMutableList()
            if (updatedLeagueClubs.isNotEmpty()) {
                val replacedClub = updatedLeagueClubs.removeAt(updatedLeagueClubs.size - 1)
                updatedClubs = currentClubs.filter { it.id != replacedClub.id } + customClub
            } else {
                updatedClubs = currentClubs + customClub
            }
            updatedLeagueClubs.add(customClub)

            val index = currentLigas.indexOf(league)
            updatedLigas[index] = league.copy(clubs = updatedLeagueClubs)
            updatedLigas[index].generateSchedule() // Re-generate schedule for new custom club
        } else {
            updatedClubs = currentClubs + customClub
        }

        return Pair(updatedClubs, updatedLigas)
    }
}
