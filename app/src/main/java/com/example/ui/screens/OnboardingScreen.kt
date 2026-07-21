package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.engine.GameEngine
import com.example.engine.initializeUniverse
import com.example.engine.startCareerWithCustomClub
import com.example.engine.startCareerWithSelectedClub
import com.example.engine.tryLoadGame
import com.example.model.*
import com.example.ui.MainDashboard
import kotlinx.coroutines.launch

enum class SetupPhase {
    MAIN_MENU, ENTER_NAME, CHOOSE_CLUB, HELP_MENU, OPTIONS, SAVED_GAMES
}

@Composable
fun OnboardingScreen(
    engine: GameEngine,
    modifier: Modifier = Modifier
) {
    val isOnboardingFinished by engine.isOnboardingFinished.collectAsState()
    val isSimulating by engine.isSimulating.collectAsState()
    val countries by engine.countries.collectAsState()
    val clubs by engine.clubs.collectAsState()
    val manager by engine.manager.collectAsState()

    var phase by remember { mutableStateOf(SetupPhase.MAIN_MENU) }
    var inputName by remember { mutableStateOf("Luis") }

    // Onboarding Browser states
    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var selectedClub by remember { mutableStateOf<Club?>(null) }
    var selectedPlayerForDetail by remember { mutableStateOf<Player?>(null) }
    
    // Custom Club Creation states
    var showCustomClubDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Automatically transition to CHOOSE_CLUB if universe is newly generated during onboarding
    LaunchedEffect(countries) {
        if (countries.isNotEmpty() && !isOnboardingFinished && phase == SetupPhase.ENTER_NAME) {
            phase = SetupPhase.CHOOSE_CLUB
            if (selectedCountry == null) {
                selectedCountry = countries.firstOrNull()
            }
        }
    }

    // Keep country-club selection in sync
    LaunchedEffect(selectedCountry) {
        selectedCountry?.let { country ->
            val countryClubs = clubs.filter { it.country == country.name }
            selectedClub = countryClubs.firstOrNull()
        }
    }

    if (!isOnboardingFinished) {
        when (phase) {
            SetupPhase.MAIN_MENU -> {
                MainMenuScreen(
                    engine = engine,
                    onNavigateToPhase = { phase = it },
                    onContinueGame = {
                        coroutineScope.launch {
                            engine.tryLoadGame()
                        }
                    },
                    modifier = modifier
                )
            }
            SetupPhase.ENTER_NAME -> {
                EnterNameScreen(
                    inputName = inputName,
                    onValueChange = { inputName = it },
                    isSimulating = isSimulating,
                    onInitializeUniverse = { useFictional ->
                        coroutineScope.launch {
                            engine.initializeUniverse(inputName, useFictional)
                        }
                    },
                    modifier = modifier
                )
            }
            SetupPhase.CHOOSE_CLUB -> {
                UniverseSelectionBrowser(
                    manager = manager,
                    countries = countries,
                    clubs = clubs,
                    selectedCountry = selectedCountry,
                    onSelectCountry = { selectedCountry = it },
                    selectedClub = selectedClub,
                    onSelectClub = { selectedClub = it },
                    isSimulating = isSimulating,
                    onFundClubClick = {
                        showCustomClubDialog = true
                    },
                    onChooseClubAndStart = {
                        selectedClub?.let { club ->
                            coroutineScope.launch {
                                engine.startCareerWithSelectedClub(inputName, club.id)
                            }
                        }
                    },
                    onSelectPlayerForDetail = { selectedPlayerForDetail = it },
                    modifier = modifier
                )

                // Dialogs
                selectedPlayerForDetail?.let { player ->
                    PlayerDetailsDialog(
                        player = player,
                        onDismissRequest = { selectedPlayerForDetail = null }
                    )
                }

                if (showCustomClubDialog) {
                    CustomClubCreationDialog(
                        initialClubName = "${inputName} FC",
                        countryName = selectedCountry?.name,
                        onDismissRequest = { showCustomClubDialog = false },
                        onFundClub = { name, capacity, budget ->
                            selectedCountry?.let { country ->
                                coroutineScope.launch {
                                    showCustomClubDialog = false
                                    engine.startCareerWithCustomClub(
                                        managerName = inputName,
                                        customClubName = name,
                                        countryName = country.name,
                                        stadiumCapacity = capacity,
                                        budget = budget
                                    )
                                }
                            }
                        }
                    )
                }
            }
            SetupPhase.HELP_MENU -> {
                HelpMenuScreen(
                    onBack = { phase = SetupPhase.MAIN_MENU },
                    modifier = modifier
                )
            }
            SetupPhase.OPTIONS -> {
                OptionsMenuScreen(
                    engine = engine,
                    onBack = { phase = SetupPhase.MAIN_MENU },
                    modifier = modifier
                )
            }
            SetupPhase.SAVED_GAMES -> {
                SavedGamesMenuScreen(
                    engine = engine,
                    onBack = { phase = SetupPhase.MAIN_MENU },
                    onLoadGame = {
                        coroutineScope.launch {
                            engine.tryLoadGame()
                        }
                    },
                    modifier = modifier
                )
            }
        }
    } else {
        MainDashboard(
            engine = engine,
            modifier = modifier
        )
    }
}
