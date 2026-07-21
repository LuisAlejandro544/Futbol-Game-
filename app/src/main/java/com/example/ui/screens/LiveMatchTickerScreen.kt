package com.example.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.BackgroundMusicPlayer
import com.example.model.League
import com.example.model.MatchEvent
import com.example.model.Club
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LiveMatchTickerScreen(
    league: League?,
    managerClubId: String?,
    onMatchFinished: () -> Unit = {},
    managerClub: Club? = null,
    onSwapPlayers: ((String, String) -> Unit)? = null,
    onReSimulateSecondHalf: (() -> Unit)? = null
) {
    if (league == null || managerClubId == null) return

    val currentFixtureRound = league.fixtures.getOrNull((league.currentRound - 1).coerceAtLeast(0))
    val userMatch = currentFixtureRound?.matches?.firstOrNull { 
        it.homeClubId == managerClubId || it.awayClubId == managerClubId 
    }

    if (userMatch != null) {
        val homeClub = remember(userMatch, league) {
            league.clubs.find { it.id == userMatch.homeClubId || it.name == userMatch.homeClubName }
        }
        val awayClub = remember(userMatch, league) {
            league.clubs.find { it.id == userMatch.awayClubId || it.name == userMatch.awayClubName }
        }

        val context = LocalContext.current
        val playWhistleSound = {
            try {
                val whistleResIds = listOf(
                    com.example.R.raw.whistle0,
                    com.example.R.raw.whistle1,
                    com.example.R.raw.whistle2,
                    com.example.R.raw.whistle3,
                    com.example.R.raw.whistle4,
                    com.example.R.raw.whistle5
                )
                val randomRes = whistleResIds.random()
                val appContext = context.applicationContext
                val mp = MediaPlayer.create(appContext, randomRes)
                if (mp != null) {
                    mp.setOnCompletionListener { mediaPlayer ->
                        mediaPlayer.release()
                    }
                    mp.start()
                } else {
                    playWhistleSoundPool(appContext, randomRes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                playWhistleFallbackTone()
            }
        }

        // Simulation States
        var simPhase by rememberSaveable { mutableStateOf(SimPhase.NOT_STARTED) }
        var currentMin by rememberSaveable { mutableStateOf(0) }
        var playSpeedMultiplier by rememberSaveable { mutableStateOf(1) } // 1x, 2x, 5x, 10x
        var isPaused by rememberSaveable { mutableStateOf(false) }

        // Trigger referee whistle sound on every match phase transition
        LaunchedEffect(simPhase) {
            if (simPhase != SimPhase.NOT_STARTED) {
                playWhistleSound()
            }
            if (simPhase == SimPhase.FINISHED) {
                onMatchFinished()
            }
        }

        // Manage background music pausing/resuming during live match gameplay
        DisposableEffect(simPhase, isPaused) {
            val shouldPauseMusic = (simPhase == SimPhase.FIRST_HALF || simPhase == SimPhase.SECOND_HALF) && !isPaused
            if (shouldPauseMusic) {
                BackgroundMusicPlayer.pause()
            } else {
                BackgroundMusicPlayer.resume(context.applicationContext)
            }
            onDispose {
                // Safely resume background music if the screen is exited or destroyed
                BackgroundMusicPlayer.resume(context.applicationContext)
            }
        }

        // Automatically reset simulation when match changes
        LaunchedEffect(userMatch.id) {
            simPhase = SimPhase.NOT_STARTED
            currentMin = 0
            isPaused = false
        }

        // Timer ticking loop
        LaunchedEffect(simPhase, isPaused, playSpeedMultiplier) {
            if (isPaused) return@LaunchedEffect

            if (simPhase == SimPhase.FIRST_HALF) {
                while (currentMin < 45) {
                    val baseDelay = 500L // 500ms real time = 1 game minute by default
                    delay(baseDelay / playSpeedMultiplier)
                    currentMin++
                    if (currentMin == 45) {
                        simPhase = SimPhase.HALF_TIME_PAUSE
                        isPaused = true
                        break
                    }
                }
            } else if (simPhase == SimPhase.SECOND_HALF) {
                while (currentMin < 90) {
                    val baseDelay = 500L
                    delay(baseDelay / playSpeedMultiplier)
                    currentMin++
                    if (currentMin == 90) {
                        simPhase = SimPhase.FINISHED
                        break
                    }
                }
            }
        }

        // Live calculated stats based on current running minute
        val liveEvents = remember(userMatch.events, currentMin) {
            val eventsList = mutableListOf<MatchEvent>()
            
            // 1. Add official match events up to currentMin
            eventsList.addAll(userMatch.events.filter { it.minute <= currentMin })
            
            // 2. Insert procedural commentaries to fill blank minutes
            val officialMinutes = userMatch.events.map { it.minute }.toSet()
            for (m in 1..currentMin) {
                if (!officialMinutes.contains(m)) {
                    val comment = getDynamicCommentary(m, userMatch.homeClubName, userMatch.awayClubName)
                    if (comment != null) {
                        eventsList.add(MatchEvent(minute = m, description = comment, type = "INFO_GENERIC"))
                    }
                }
            }
            
            // Newest events at the top (sports app style)
            eventsList.sortedByDescending { it.minute }
        }

        val liveHomeGoals = remember(liveEvents) {
            liveEvents.count { it.type == "GOAL_HOME" }
        }

        val liveAwayGoals = remember(liveEvents) {
            liveEvents.count { it.type == "GOAL_AWAY" }
        }

        // Sound Notification when a Goal is scored live!
        LaunchedEffect(liveHomeGoals, liveAwayGoals) {
            if (currentMin > 0) {
                playGoalSound()
            }
        }

        val liveHomeShots = remember(userMatch.homeShots, currentMin) {
            if (currentMin == 0) 0 else ((currentMin.toFloat() / 90f) * userMatch.homeShots).toInt().coerceIn(0, userMatch.homeShots)
        }

        val liveAwayShots = remember(userMatch.awayShots, currentMin) {
            if (currentMin == 0) 0 else ((currentMin.toFloat() / 90f) * userMatch.awayShots).toInt().coerceIn(0, userMatch.awayShots)
        }

        val livePossessionHome = remember(userMatch.possessionHome, currentMin) {
            if (currentMin == 0) 50 else userMatch.possessionHome
        }

        val livePossessionAway = remember(userMatch.possessionAway, currentMin) {
            if (currentMin == 0) 50 else userMatch.possessionAway
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize().background(PitchDarkBg)) {
            val isLandscape = maxWidth > maxHeight
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LiveMatchScoreboard(
                            simPhase = simPhase,
                            currentMin = currentMin,
                            homeClubName = userMatch.homeClubName,
                            awayClubName = userMatch.awayClubName,
                            liveHomeGoals = liveHomeGoals,
                            liveAwayGoals = liveAwayGoals
                        )
                        LiveMatchControls(
                            simPhase = simPhase,
                            isPaused = isPaused,
                            playSpeedMultiplier = playSpeedMultiplier,
                            onStartMatchClick = {
                                simPhase = SimPhase.FIRST_HALF
                                isPaused = false
                            },
                            onPlayPauseClick = { isPaused = !isPaused },
                            onSpeedClick = {
                                playSpeedMultiplier = when (playSpeedMultiplier) {
                                    1 -> 2
                                    2 -> 5
                                    5 -> 10
                                    else -> 1
                                }
                            },
                            onResumeMatchClick = {
                                simPhase = SimPhase.SECOND_HALF
                                isPaused = false
                            },
                            onReplayMatchClick = {
                                currentMin = 0
                                simPhase = SimPhase.FIRST_HALF
                                isPaused = false
                            }
                        )
                        LiveMatchStatsRow(
                            livePossessionHome = livePossessionHome,
                            livePossessionAway = livePossessionAway,
                            liveHomeShots = liveHomeShots,
                            liveAwayShots = liveAwayShots
                        )
                    }

                    Column(
                        modifier = Modifier.weight(0.9f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (simPhase == SimPhase.FINISHED) {
                            LiveMatchPostGameRecap(
                                userMatch = userMatch,
                                managerClubId = managerClubId
                            )
                        }
                        if (simPhase == SimPhase.HALF_TIME_PAUSE && managerClub != null && onSwapPlayers != null && onReSimulateSecondHalf != null) {
                            LiveMatchSubstitutionPanel(
                                managerClub = managerClub,
                                onSwapPlayers = onSwapPlayers,
                                onReSimulateSecondHalf = onReSimulateSecondHalf
                            )
                        }
                        LiveMatchChronicle(
                            simPhase = simPhase,
                            liveEvents = liveEvents,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LiveMatchScoreboard(
                        simPhase = simPhase,
                        currentMin = currentMin,
                        homeClubName = userMatch.homeClubName,
                        awayClubName = userMatch.awayClubName,
                        liveHomeGoals = liveHomeGoals,
                        liveAwayGoals = liveAwayGoals
                    )
                    LiveMatchControls(
                        simPhase = simPhase,
                        isPaused = isPaused,
                        playSpeedMultiplier = playSpeedMultiplier,
                        onStartMatchClick = {
                            simPhase = SimPhase.FIRST_HALF
                            isPaused = false
                        },
                        onPlayPauseClick = { isPaused = !isPaused },
                        onSpeedClick = {
                            playSpeedMultiplier = when (playSpeedMultiplier) {
                                1 -> 2
                                2 -> 5
                                5 -> 10
                                else -> 1
                            }
                        },
                        onResumeMatchClick = {
                            simPhase = SimPhase.SECOND_HALF
                            isPaused = false
                        },
                        onReplayMatchClick = {
                            currentMin = 0
                            simPhase = SimPhase.FIRST_HALF
                            isPaused = false
                        }
                    )
                    if (simPhase == SimPhase.NOT_STARTED && homeClub != null && awayClub != null) {
                        val isUserHome = homeClub.id == managerClubId || homeClub.name == managerClub?.name
                        val myClub = if (isUserHome) homeClub else awayClub
                        val oppClub = if (isUserHome) awayClub else homeClub

                        PreMatchTacticsPanel(
                            managerClub = myClub,
                            opponentClub = oppClub
                        )
                        VerticalPitch2DView(
                            homeClub = homeClub,
                            awayClub = awayClub
                        )
                    }

                    if (simPhase != SimPhase.NOT_STARTED) {
                        LiveMatchStatsRow(
                            livePossessionHome = livePossessionHome,
                            livePossessionAway = livePossessionAway,
                            liveHomeShots = liveHomeShots,
                            liveAwayShots = liveAwayShots
                        )
                    }

                    if (simPhase == SimPhase.FINISHED) {
                        LiveMatchPostGameRecap(
                            userMatch = userMatch,
                            managerClubId = managerClubId
                        )
                    }
                    if (simPhase == SimPhase.HALF_TIME_PAUSE && managerClub != null && onSwapPlayers != null && onReSimulateSecondHalf != null) {
                        LiveMatchSubstitutionPanel(
                            managerClub = managerClub,
                            onSwapPlayers = onSwapPlayers,
                            onReSimulateSecondHalf = onReSimulateSecondHalf
                        )
                    }
                    LiveMatchChronicle(
                        simPhase = simPhase,
                        liveEvents = liveEvents,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Simula una jornada en el botón superior para ver crónicas de partidos vivos.", color = TextSecondary, fontSize = 13.sp)
        }
    }
}

@Composable
fun LiveMatchSubstitutionPanel(
    managerClub: Club,
    onSwapPlayers: (String, String) -> Unit,
    onReSimulateSecondHalf: () -> Unit
) {
    var selectedStarterId by remember { mutableStateOf<String?>(null) }
    var selectedSubstituteId by remember { mutableStateOf<String?>(null) }
    var showSuccessToast by remember { mutableStateOf(false) }
    var successMsg by remember { mutableStateOf("") }

    val starters = remember(managerClub.squad) {
        managerClub.squad.filter { it.isStarter }
    }
    val bench = remember(managerClub.squad) {
        managerClub.squad.filter { !it.isStarter && !it.isInjured }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
        border = BorderStroke(1.dp, GrassEmerald.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "🔄 SUSTITUCIONES DE ENTRETIEMPO",
                    color = GrassEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                if (showSuccessToast) {
                    Text(
                        "¡Cambio Guardado!",
                        color = GlacierBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Selecciona un titular y un suplente para realizar una variante táctica y re-simular la segunda mitad:",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth().height(140.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Starters List
                Column(modifier = Modifier.weight(1f)) {
                    Text("TITULARES", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .background(PitchDarkBg.copy(alpha = 0.5f), shape = RoundedCornerShape(4.dp))
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(starters) { p ->
                            val isSelected = selectedStarterId == p.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected) SoftSapphire else Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable { selectedStarterId = p.id }
                                    .padding(6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(p.lastName, color = if (isSelected) GrassEmerald else TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                    Text("${p.position} | OVR: ${p.getOverallRating()} | ⚡${p.energy}", color = TextSecondary, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }

                // Bench List
                Column(modifier = Modifier.weight(1f)) {
                    Text("BANCA", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .background(PitchDarkBg.copy(alpha = 0.5f), shape = RoundedCornerShape(4.dp))
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(bench) { p ->
                            val isSelected = selectedSubstituteId == p.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected) SoftSapphire else Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable { selectedSubstituteId = p.id }
                                    .padding(6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(p.lastName, color = if (isSelected) GlacierBlue else TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                    Text("${p.position} | OVR: ${p.getOverallRating()} | ⚡${p.energy}", color = TextSecondary, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val starterId = selectedStarterId
                    val subId = selectedSubstituteId
                    if (starterId != null && subId != null) {
                        val starterPlayer = starters.first { it.id == starterId }
                        val subPlayer = bench.first { it.id == subId }
                        onSwapPlayers(starterId, subId)
                        onReSimulateSecondHalf()
                        successMsg = "Cambio: Entra ${subPlayer.lastName}, sale ${starterPlayer.lastName}."
                        selectedStarterId = null
                        selectedSubstituteId = null
                        showSuccessToast = true
                    }
                },
                enabled = selectedStarterId != null && selectedSubstituteId != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GrassEmerald,
                    disabledContainerColor = DarkSteel
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Text(
                    if (selectedStarterId != null && selectedSubstituteId != null) "CONFIRMAR CAMBIO" else "SELECCIONA JUGADORES",
                    color = PitchDarkBg,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            if (successMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(successMsg, color = GlacierBlue, fontSize = 10.sp, fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
fun PreMatchTacticsPanel(
    managerClub: Club,
    opponentClub: Club?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
        border = BorderStroke(1.dp, DarkSteel),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "⚡ TÁCTICA Y ESTRATEGIA PRE-PARTIDO",
                color = NeonAmber,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Formations Row
            Text(text = "FORMACIÓN TÁCTICA:", color = TextSecondary, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
            val formations = listOf("4-4-2", "4-3-3", "3-5-2", "5-3-2", "4-2-3-1")
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                formations.forEach { form ->
                    val active = managerClub.selectedFormation == form
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (active) GrassEmerald else DarkSteel)
                            .clickable { managerClub.selectedFormation = form }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = form,
                            color = if (active) Color.Black else TextPrimary,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tactics Row
            Text(text = "ESTILO TÁCTICO:", color = TextSecondary, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
            val tactics = listOf("Equilibrada", "Agresiva", "Defensiva", "Contraataque", "Posesión", "Presión Alta")
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tactics.forEach { tac ->
                    val active = managerClub.selectedTactic == tac
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (active) NeonAmber else DarkSteel)
                            .clickable { managerClub.selectedTactic = tac }
                            .padding(vertical = 5.dp, horizontal = 1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tac,
                            color = if (active) Color.Black else TextPrimary,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Ratings comparison if opponent available
            if (opponentClub != null) {
                Spacer(modifier = Modifier.height(6.dp))
                val (myDef, myMid, myAtt) = managerClub.getTeamRatings()
                val (oppDef, oppMid, oppAtt) = opponentClub.getTeamRatings()

                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(PitchDarkBg).padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DEFENSA", color = TextSecondary, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        Text("$myDef vs $oppDef", color = if (myDef >= oppDef) GrassEmerald else StatusRed, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("MEDIOCAMPO", color = TextSecondary, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        Text("$myMid vs $oppMid", color = if (myMid >= oppMid) GrassEmerald else StatusRed, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ATAQUE", color = TextSecondary, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        Text("$myAtt vs $oppAtt", color = if (myAtt >= oppAtt) GrassEmerald else StatusRed, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
