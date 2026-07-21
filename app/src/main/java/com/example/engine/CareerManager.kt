package com.example.engine

import com.example.model.Club
import com.example.model.Manager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CareerManager(
    private val managerFlow: MutableStateFlow<Manager>,
    private val clubsFlow: MutableStateFlow<List<Club>>,
    private val storage: GameStorage,
    private val scope: CoroutineScope,
    private val addNews: (String) -> Unit
) {
    fun purchaseLicense(type: String): Boolean {
        val mgr = managerFlow.value
        val success = mgr.purchaseLicense(type)
        if (success) {
            val updated = mgr.copy()
            managerFlow.value = updated // Trigger state flow recomposition
            addNews("🎓 PROFESIONAL: Obtuviste exitosamente la ${type}.")
            scope.launch {
                storage.saveManager(updated)
            }
        }
        return success
    }

    fun hirePrivateAgent(): Boolean {
        val mgr = managerFlow.value
        val success = mgr.hirePrivateAgent()
        if (success) {
            val updated = mgr.copy()
            managerFlow.value = updated // Trigger state flow recomposition
            addNews("💼 NEGOCIACIÓN: Contrataste a un Agente de Representación Privado.")
            scope.launch {
                storage.saveManager(updated)
            }
        }
        return success
    }

    fun investInPRCampaign(): Boolean {
        val mgr = managerFlow.value
        if (mgr.personalWealth >= 3000L && mgr.nationalSummonProgress < 100) {
            mgr.personalWealth -= 3000L
            mgr.nationalSummonProgress = (mgr.nationalSummonProgress + 20).coerceAtMost(100)
            val updated = mgr.copy()
            managerFlow.value = updated
            addNews("💼 RELACIONES PÚBLICAS: Invertiste $3,000 en cabildeo. Tu probabilidad de convocatoria continental sube al ${updated.nationalSummonProgress}%.")
            scope.launch {
                storage.saveManager(updated)
            }
            return true
        }
        return false
    }

    fun acceptNationalSummon(): Boolean {
        val mgr = managerFlow.value
        if (mgr.nationalSummonProgress >= 100 && !mgr.isSummoned) {
            val activeClub = clubsFlow.value.firstOrNull { it.id == mgr.currentClubId }
            val country = activeClub?.country?.uppercase() ?: "ARGENTINA"
            val fictionalNationalTeam = when {
                country.contains("ARGENTINA") -> "Argen-Pampa (SUDAMBOL)"
                country.contains("BRASIL") -> "Samba-FC (SUDAMBOL)"
                country.contains("FRANCIA") -> "Galia-FC (EUROBOL)"
                country.contains("COLOMBIA") -> "Cafeteros-FC (SUDAMBOL)"
                country.contains("CHILE") -> "Cordillera-Azul (SUDAMBOL)"
                country.contains("URUGUAY") -> "OrienteCeleste (SUDAMBOL)"
                country.contains("MEXICO") -> "Pumas-Fict (NORAMBOL)"
                country.contains("PERU") -> "Inca-Andina (SUDAMBOL)"
                else -> "Continental-Fict (SUDAMBOL)"
            }
            mgr.isSummoned = true
            mgr.summonedNationalTeam = fictionalNationalTeam
            mgr.reputation = (mgr.reputation + 15).coerceAtMost(100)
            val updated = mgr.copy()
            managerFlow.value = updated
            addNews("🦁 CONVOCATORIA NACIONAL: ¡Histórico! ${mgr.name} acepta dirigir la ${fictionalNationalTeam} simultáneamente.")
            scope.launch {
                storage.saveManager(updated)
            }
            return true
        }
        return false
    }

    fun resignNationalSummon(): Boolean {
        val mgr = managerFlow.value
        if (mgr.isSummoned) {
            val oldTeam = mgr.summonedNationalTeam ?: "Selección Nacional"
            mgr.isSummoned = false
            mgr.summonedNationalTeam = null
            mgr.nationalSummonProgress = 15
            val updated = mgr.copy()
            managerFlow.value = updated
            addNews("💼 RENUNCIA: Decidiste dar un paso al costado y renunciar a la dirección de la ${oldTeam}.")
            scope.launch {
                storage.saveManager(updated)
            }
            return true
        }
        return false
    }
}
