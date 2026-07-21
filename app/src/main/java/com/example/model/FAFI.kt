package com.example.model

import com.squareup.moshi.JsonClass
import kotlin.random.Random

enum class Fafitrait {
    CORRUPTO,       // Allows purchasing license exemptions, takes high global taxes
    AMBICIOSO,      // Increases prize funds but demands more intense schedules
    EXPANSIVO,      // Realistically expands world tournament size (e.g., to 48 or 64 teams)
    TRADICIONALISTA // Keeps classic structures, restricts subsidies, blocks expansions
}

@JsonClass(generateAdapter = true)
data class FafiPresident(
    val name: String,
    val trait: Fafitrait,
    val approvalRating: Int = 75,
    val corruptionIndex: Int = Random.nextInt(0, 100)
)

@JsonClass(generateAdapter = true)
data class FAFI(
    var president: FafiPresident,
    var worldCupSize: Int = 32,
    var transferTaxPercent: Int = 5,
    var currentRuleSet: String = "Estándar M3 - Sin cambios de cuotas",
    var yearsUntilElection: Int = 4
) {
    fun triggerElectionEvent(): String {
        yearsUntilElection = 4
        val names = listOf("Gianni Donati", "Alexander Havel", "Sepp Blather", "Joao Grand", "Vicente Platino")
        val oldTrait = president.trait
        val newTrait = Fafitrait.values().random()
        val newName = names.random()
        president = FafiPresident(
            name = newName,
            trait = newTrait
        )

        // Apply rules based on election outcome
        return when (newTrait) {
            Fafitrait.CORRUPTO -> {
                transferTaxPercent = 12
                worldCupSize = 32
                currentRuleSet = "Exenciones por soborno permitidas en SUDAMBOL. Impuesto de transferencia sube a 12%."
                "El nuevo presidente de FEDEBOL, $newName, es catalogado de Corrupto. ¡El impuesto por fichajes se eleva al 12%!"
            }
            Fafitrait.AMBICIOSO -> {
                transferTaxPercent = 8
                currentRuleSet = "Premios globales de SUDAMBOL y EUROBOL incrementados en 25%. Licencias Pro reciben subsidio parcial."
                "El nuevo presidente de FEDEBOL, $newName, es catalogado de Ambicioso. Los premios de copas continentales aumentan un 25%."
            }
            Fafitrait.EXPANSIVO -> {
                worldCupSize = 64
                transferTaxPercent = 4
                currentRuleSet = "El Mundial Absoluto se expande oficialmente a 64 selecciones. Cupos de SUDAMBOL y EUROBOL ampliados."
                "El expansivo presidente de FEDEBOL, $newName, aprueba la histórica expansión del Mundial Absoluto a 64 selecciones."
            }
            Fafitrait.TRADICIONALISTA -> {
                worldCupSize = 32
                transferTaxPercent = 5
                currentRuleSet = "Formato clásico blindado. Sin subsidios externos. Límites estrictos de visibilidad en copas continentales."
                "El tradicionalista presidente de FEDEBOL, $newName, asume el mando de la federación. ¡Copas clásicas quedan blindadas!"
            }
        }
    }

    companion object {
        fun createDefault(): FAFI {
            return FAFI(
                president = FafiPresident("Alexander Havel", Fafitrait.TRADICIONALISTA)
            )
        }
    }
}
