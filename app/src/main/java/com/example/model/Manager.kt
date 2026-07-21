package com.example.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Manager(
    val name: String,
    var personalWealth: Long = 15_000L,        // Weekly earnings, completely separate from Club
    var license: String = "Licencia C",        // Licencia C, B, A, Pro
    var reputation: Int = 10,                  // 0 - 100
    var currentClubId: String? = null,
    var currentClubName: String? = "Desempleado",
    var hasPrivateAgent: Boolean = false,
    var agentQuality: Int = 0,                 // 1 to 5 stars
    var summonedNationalTeam: String? = null,
    var isSummoned: Boolean = false,
    var nationalSummonProgress: Int = 15
) {
    fun canAffordLicense(type: String): Boolean {
        return when (type) {
            "Licencia B" -> personalWealth >= 5_000L
            "Licencia A" -> personalWealth >= 15_000L
            "Licencia Pro" -> personalWealth >= 40_000L
            else -> false
        }
    }

    fun purchaseLicense(type: String): Boolean {
        if (!canAffordLicense(type)) return false
        
        when (type) {
            "Licencia B" -> {
                personalWealth -= 5_000L
                license = "Licencia B"
                reputation += 10
            }
            "Licencia A" -> {
                personalWealth -= 15_000L
                license = "Licencia A"
                reputation += 20
            }
            "Licencia Pro" -> {
                personalWealth -= 40_000L
                license = "Licencia Pro"
                reputation += 35
            }
        }
        return true
    }

    fun hirePrivateAgent(): Boolean {
        if (personalWealth >= 8_000L && !hasPrivateAgent) {
            personalWealth -= 8_000L
            hasPrivateAgent = true
            agentQuality = 3
            return true
        }
        return false
    }

    fun upgradeAgent(): Boolean {
        if (hasPrivateAgent && agentQuality < 5 && personalWealth >= 10_000L) {
            personalWealth -= 10_000L
            agentQuality += 1
            return true
        }
        return false
    }
}
