package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.engine.GameEngine
import com.example.model.*
import com.example.ui.components.HeaderBar
import com.example.ui.components.NavigationSidebar
import com.example.ui.components.ManagerEventDialog
import com.example.ui.components.TrainingReportDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MainDashboard(
    engine: GameEngine,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // UI state collected safely from engine flow state
    val countries by engine.countries.collectAsState()
    val ligas by engine.ligas.collectAsState()
    val clubs by engine.clubs.collectAsState()
    val manager by engine.manager.collectAsState()
    val fafi by engine.fafi.collectAsState()
    val socialFeed by engine.socialFeed.collectAsState()
    val currentLiveMatch by engine.currentLiveMatch.collectAsState()
    val isSimulating by engine.isSimulating.collectAsState()
    val newsLog by engine.newsLog.collectAsState()
    val currentDate by engine.currentDate.collectAsState()
    val diaryLines by engine.diaryLines.collectAsState()
    val currentActiveEvent by engine.currentActiveEvent.collectAsState()
    val currentTrainingReport by engine.currentTrainingReport.collectAsState()

    var activeTab by remember { mutableStateOf<DashboardTab>(DashboardTab.ClubInfo) }
    var selectedPlayerForDetail by remember { mutableStateOf<Player?>(null) }
    var isMatchPlaybackFinished by rememberSaveable { mutableStateOf(true) }

    // Derived states to prevent unnecessary heavy UI calculations
    val managerClub by remember(clubs, manager) {
        derivedStateOf { clubs.firstOrNull { it.id == manager.currentClubId } }
    }
    val currentLeague by remember(ligas, manager) {
        derivedStateOf { ligas.firstOrNull { it.clubs.any { club -> club.id == manager.currentClubId } } }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDarkBg)
    ) {
        // 1. LEFT NAVIGATION PANEL (Compact sidebar navigation optimized for landscape)
        NavigationSidebar(
            activeTab = activeTab,
            onTabSelected = { selectedTab ->
                if (!isMatchPlaybackFinished && selectedTab != DashboardTab.LiveMatch) {
                    Toast.makeText(
                        context,
                        "¡Atención! Tienes un partido en vivo en progreso. Debes finalizarlo antes de continuar.",
                        Toast.LENGTH_LONG
                    ).show()
                    activeTab = DashboardTab.LiveMatch
                } else {
                    activeTab = selectedTab
                    selectedPlayerForDetail = null
                }
            },
            modifier = Modifier
                .width(96.dp)
                .fillMaxHeight()
                .background(SurfaceCarbon)
                .drawBehind {
                    drawLine(
                        color = DarkSteel,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
        )

        // 2. MAIN DETAILS & PANELS (Right side content area)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Header Stats bar
            HeaderBar(
                manager = manager,
                club = managerClub,
                isSimulating = isSimulating,
                currentDate = currentDate,
                onSimulateClick = {
                    if (!isMatchPlaybackFinished) {
                        Toast.makeText(
                            context,
                            "¡Atención! Tienes un partido en vivo en progreso. Debes finalizar el partido en el Simulador Vivo antes de avanzar.",
                            Toast.LENGTH_LONG
                        ).show()
                        activeTab = DashboardTab.LiveMatch
                    } else {
                        coroutineScope.launch {
                            engine.advanceRound()
                            // If our active club has a match in the active round, load its detailed live events
                            val activeClubId = manager.currentClubId
                            if (activeClubId != null) {
                                val activeLg = currentLeague
                                if (activeLg != null) {
                                    val currentFixtures = activeLg.fixtures.getOrNull(activeLg.currentRound - 1)
                                    val clubMatch = currentFixtures?.matches?.firstOrNull { 
                                        it.homeClubId == activeClubId || it.awayClubId == activeClubId 
                                    }
                                    if (clubMatch != null) {
                                        // Set live match ticker view active
                                        engine.advanceWeekRecovery()
                                        activeTab = DashboardTab.LiveMatch
                                        isMatchPlaybackFinished = false
                                    } else {
                                        isMatchPlaybackFinished = true
                                    }
                                }
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Main Dynamic Board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    DashboardTab.ClubInfo -> {
                        val availableCoaches by engine.availableCoachesToHire.collectAsState()
                        ClubAndStandingsScreen(
                            club = managerClub,
                            league = currentLeague,
                            newsLog = newsLog,
                            availableCoaches = availableCoaches,
                            onHireCoach = { coach ->
                                engine.hireCoach(coach.speciality, coach.level, coach.salary, coach.name)
                            },
                            onFireCoach = { coachId ->
                                engine.fireCoach(coachId)
                            }
                        )
                    }
                    DashboardTab.Squad -> {
                        SquadScreen(
                            club = managerClub,
                            selectedPlayer = selectedPlayerForDetail,
                            onPlayerClick = { selectedPlayerForDetail = it },
                            onFormationChange = { formation -> engine.updateClubFormation(formation) },
                            onSetCaptain = { player -> engine.setClubCaptain(player.id) },
                            onSwapPlayers = { starterId, benchId -> engine.swapStarterAndSubstitute(starterId, benchId) },
                            onSellPlayer = { player, fee ->
                                val success = engine.sellPlayerFromUserSquad(player, fee)
                                if (success) {
                                    Toast.makeText(context, "¡Venta realizada! ${player.fullName} vendido por $${fee}.", Toast.LENGTH_SHORT).show()
                                    selectedPlayerForDetail = null
                                } else {
                                    Toast.makeText(context, "No se pudo realizar la venta.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    DashboardTab.Transfers -> {
                        TransfersScreen(
                            engine = engine
                        )
                    }
                    DashboardTab.Calendar -> {
                        CalendarScreen(
                            currentDate = currentDate,
                            manager = manager,
                            ligas = ligas,
                            clubs = clubs
                        )
                    }
                    DashboardTab.LiveMatch -> {
                        LiveMatchTickerScreen(
                            league = currentLeague,
                            managerClubId = manager.currentClubId,
                            onMatchFinished = {
                                isMatchPlaybackFinished = true
                            },
                            managerClub = managerClub,
                            onSwapPlayers = { starterId, benchId -> engine.swapStarterAndSubstitute(starterId, benchId) },
                            onReSimulateSecondHalf = { engine.reSimulateUserMatchSecondHalf() }
                        )
                    }
                    DashboardTab.Social -> {
                        SocialFeedScreen(
                            posts = socialFeed,
                            onDecisionTaken = { postId, choiceIndex ->
                                engine.handleSocialFeedDecision(postId, choiceIndex)
                            }
                        )
                    }
                    DashboardTab.ManagerCareer -> {
                        ManagerCareerScreen(
                            manager = manager,
                            diaryLines = diaryLines,
                            onPurchaseLicense = { licenseType ->
                                engine.purchaseLicense(licenseType)
                            },
                            onHireAgent = {
                                engine.hirePrivateAgent()
                            }
                        )
                    }
                    DashboardTab.FafiFederation -> {
                        FafiFederationScreen(
                            fafi = fafi,
                            manager = manager,
                            onPRCampaignClick = { engine.investInPRCampaign() },
                            onAcceptSummonClick = { engine.acceptNationalSummon() },
                            onResignSummonClick = { engine.resignNationalSummon() }
                        )
                    }
                }
            }
        }
    }

    // Render blocking popup for random manager decision events
    currentActiveEvent?.let { activeEvent ->
        ManagerEventDialog(
            event = activeEvent,
            onOptionSelected = { choiceIndex ->
                engine.resolveActiveEvent(choiceIndex)
            }
        )
    }

    // Render training report dialog
    currentTrainingReport?.let { report ->
        TrainingReportDialog(
            report = report,
            onDismiss = {
                engine._currentTrainingReport.value = null
            }
        )
    }
}
