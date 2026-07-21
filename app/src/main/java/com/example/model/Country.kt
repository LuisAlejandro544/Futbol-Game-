package com.example.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Country(
    val name: String,
    val economyFactor: Float,   // Multiplier for club budgets, player pricing, and ticket sales (0.3 to 2.5)
    val academyFactor: Float,   // Multiplier for youth development and average potential of new talent (0.3 to 2.5)
    val selectionPower: Int     // Football prestige index (10 to 95)
) {
    companion object {
        fun generateUniverse(): List<Country> {
            return listOf(
                Country("Argentina 🇦🇷", 1.0f, 2.1f, 88),
                Country("Brasil 🇧🇷", 1.3f, 2.4f, 91),
                Country("Francia 🇫🇷", 2.2f, 2.0f, 93),
                Country("México 🇲🇽", 1.4f, 1.1f, 76),
                Country("Colombia 🇨🇴", 0.8f, 1.5f, 78),
                Country("Chile 🇨🇱", 0.9f, 1.2f, 74),
                Country("Uruguay 🇺🇾", 0.7f, 2.2f, 82),
                Country("Paraguay 🇵🇾", 0.6f, 1.4f, 73),
                Country("Ecuador 🇪🇨", 0.7f, 1.5f, 77),
                Country("Perú 🇵🇪", 0.7f, 1.1f, 72),
                Country("Venezuela 🇻🇪", 0.4f, 0.9f, 68),
                Country("Bolivia 🇧🇴", 0.4f, 0.8f, 64),
                Country("España 🇪🇸", 2.3f, 2.3f, 92),
                Country("Alemania 🇩🇪", 2.4f, 2.4f, 94),
                Country("Japón 🇯🇵", 2.1f, 1.8f, 84),
                Country("EE.UU. 🇺🇸", 2.5f, 1.6f, 81),
                Country("Noruega 🇳🇴", 2.0f, 1.9f, 83),
                Country("Costa Rica 🇨🇷", 0.8f, 1.1f, 70),
                Country("Panamá 🇵🇦", 0.9f, 0.8f, 66),
                Country("Honduras 🇭🇳", 0.5f, 1.0f, 65),
                Country("El Salvador 🇸🇻", 0.5f, 0.9f, 63),
                Country("Guatemala 🇬🇹", 0.6f, 0.9f, 64),
                Country("Nicaragua 🇳🇮", 0.5f, 0.7f, 60)
            )
        }
    }
}
