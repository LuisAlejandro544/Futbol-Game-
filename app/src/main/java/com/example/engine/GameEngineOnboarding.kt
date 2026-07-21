package com.example.engine

import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

suspend fun GameEngine.initializeUniverse(managerName: String, useFictionalNames: Boolean = false) = withContext(Dispatchers.Default) {
    _isSimulating.value = true
    _isOnboardingFinished.value = false
    addNews("Inicializando generación de universo de fútbol procedural...")

    // Clear previous state
    storage.clearAll()

    // Call modular universe generator
    val (generatedCountries, generatedLigas, allClubs) = UniverseGenerator.initializeUniverse(managerName, useFictionalNames)
    _countries.value = generatedCountries
    _ligas.value = generatedLigas
    _clubs.value = allClubs

    // Initial placeholder manager
    _manager.value = Manager(name = managerName, personalWealth = 12_000L)

    _isSimulating.value = false
    addNews("¡Universo procedural generado exitosamente! Selecciona tu equipo o crea uno desde cero.")
}

suspend fun GameEngine.startCareerWithSelectedClub(managerName: String, selectedClubId: String) = withContext(Dispatchers.Default) {
    _isSimulating.value = true
    addNews("Asignando cargo directivo al mánager...")

    val allClubs = _clubs.value
    val selectedClub = allClubs.firstOrNull { it.id == selectedClubId }
    if (selectedClub != null) {
        val mgr = Manager(
            name = managerName,
            personalWealth = 15_000L,
            currentClubId = selectedClub.id,
            currentClubName = selectedClub.name
        )
        _manager.value = mgr

        // Set chosen country's league as MAX_DETAIL
        _ligas.value.forEach { league ->
            if (league.country == selectedClub.country) {
                league.visibility = LeagueVisibility.MAX_DETAIL
            } else {
                league.visibility = LeagueVisibility.ZERO_DETAIL
            }
        }

        // Generate initial Social Feed
        val posts = mutableListOf<SocialPost>()
        posts.add(SocialPost(handle = "@FEDEBOL_Oficial", authorName = "FEDEBOL", content = "Bienvenidos a una nueva temporada del fútbol profesional latinoamericano bajo la regulación de FEDEBOL. Juego Limpio ante todo.", likes = 1200, reposts = 340, timeAgo = "1h"))
        posts.add(SocialPost(handle = "@Hinchas_${selectedClub.name.replace(" ", "")}", authorName = "Fans", content = "¡Bienvenido nuestro nuevo director técnico ${mgr.name}! Esperamos grandes resultados.", likes = 750, reposts = 220, timeAgo = "5m"))
        _socialFeed.value = posts

        // Save batch to disk
        addNews("Guardando estado del universo deportivo en disco seguro...")
        _currentDate.value = LocalDate.of(2025, 1, 1)
        storage.saveManager(mgr)
        storage.saveCalendarDate("2025-01-01")
        storage.saveClubsBatch(allClubs)
        storage.saveLigasBatch(_ligas.value)
        storage.savePlayersBatch(allClubs.flatMap { it.squad })

        logToDiary("📝 [2025-01-01] ¡Día de firma! Asumo formalmente la dirección técnica del club ${selectedClub.name}. Un nuevo reto futbolístico comienza hoy.")

        _isOnboardingFinished.value = true
    }
    _isSimulating.value = false
}

suspend fun GameEngine.startCareerWithCustomClub(
    managerName: String,
    customClubName: String,
    countryName: String,
    stadiumCapacity: Int,
    budget: Long
) = withContext(Dispatchers.Default) {
    _isSimulating.value = true
    addNews("Fundando club deportivo: ${customClubName}...")

    val (updatedClubs, updatedLigas) = UniverseGenerator.startCareerWithCustomClub(
        customClubName = customClubName,
        countryName = countryName,
        stadiumCapacity = stadiumCapacity,
        budget = budget,
        currentClubs = _clubs.value,
        currentLigas = _ligas.value
    )
    _clubs.value = updatedClubs
    _ligas.value = updatedLigas

    // Set chosen country's league as MAX_DETAIL
    _ligas.value.forEach { l ->
        if (l.country == countryName) {
            l.visibility = LeagueVisibility.MAX_DETAIL
        } else {
            l.visibility = LeagueVisibility.ZERO_DETAIL
        }
    }

    val customClub = updatedClubs.first { it.name == customClubName }
    val mgr = Manager(
        name = managerName,
        personalWealth = 15_000L,
        currentClubId = customClub.id,
        currentClubName = customClub.name
    )
    _manager.value = mgr

    // Social feed setup
    val posts = mutableListOf<SocialPost>()
    posts.add(SocialPost(handle = "@FEDEBOL_Oficial", authorName = "FEDEBOL", content = "El nuevo club ${customClub.name} ha sido formalmente admitido en la Liga de ${countryName} por FEDEBOL.", likes = 1800, reposts = 420, timeAgo = "1h"))
    posts.add(SocialPost(handle = "@Hinchas_${customClub.name.replace(" ", "")}", authorName = "Fundadores", content = "¡Un hito histórico! Iniciamos nuestra aventura desde cero bajo las órdenes de ${mgr.name}.", likes = 990, reposts = 310, timeAgo = "1m"))
    _socialFeed.value = posts

    // Save everything to disk
    addNews("Guardando datos del nuevo club y jugadores...")
    _currentDate.value = LocalDate.of(2025, 1, 1)
    storage.saveManager(mgr)
    storage.saveCalendarDate("2025-01-01")
    storage.saveClubsBatch(_clubs.value)
    storage.saveLigasBatch(_ligas.value)
    storage.savePlayersBatch(_clubs.value.flatMap { it.squad })

    logToDiary("📝 [2025-01-01] ¡Día histórico! Se funda el club deportivo ${customClubName} en ${countryName}. Asumo las riendas del proyecto como Mánager Fundador para escribir nuestra propia leyenda.")

    _isOnboardingFinished.value = true
    _isSimulating.value = false
}
