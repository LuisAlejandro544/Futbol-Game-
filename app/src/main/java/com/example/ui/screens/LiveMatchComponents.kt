package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MatchEvent
import com.example.model.Match
import com.example.model.MatchPlayerStat
import com.example.ui.theme.*

@Composable
fun LiveMatchScoreboard(
    simPhase: SimPhase,
    currentMin: Int,
    homeClubName: String,
    awayClubName: String,
    liveHomeGoals: Int,
    liveAwayGoals: Int,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PitchDarkBg),
        border = BorderStroke(1.dp, GrassEmerald),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Match status & Running minute label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isSimulatingLive = (simPhase == SimPhase.FIRST_HALF || simPhase == SimPhase.SECOND_HALF)
                if (isSimulatingLive) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(StatusGreen)
                    )
                }
                
                val phaseLabel = when (simPhase) {
                    SimPhase.NOT_STARTED -> "PRE-PARTIDO"
                    SimPhase.FIRST_HALF -> "1ER TIEMPO"
                    SimPhase.HALF_TIME_PAUSE -> "ENTRETEMPO"
                    SimPhase.SECOND_HALF -> "2DO TIEMPO"
                    SimPhase.FINISHED -> "FIN DEL PARTIDO"
                }
                
                Text(
                    text = "$phaseLabel - $currentMin'",
                    color = NeonAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(homeClubName.uppercase(), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("LOCAL", color = TextSecondary, fontSize = 9.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(liveHomeGoals.toString(), color = GrassEmerald, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Text("-", color = TextSecondary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(liveAwayGoals.toString(), color = GrassEmerald, fontSize = 32.sp, fontWeight = FontWeight.Black)
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(awayClubName.uppercase(), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("VISITANTE", color = TextSecondary, fontSize = 9.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Match Progression Bar
            LinearProgressIndicator(
                progress = { currentMin.toFloat() / 90f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = GrassEmerald,
                trackColor = DarkSteel
            )
        }
    }
}

@Composable
fun LiveMatchControls(
    simPhase: SimPhase,
    isPaused: Boolean,
    playSpeedMultiplier: Int,
    onStartMatchClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSpeedClick: () -> Unit,
    onResumeMatchClick: () -> Unit,
    onReplayMatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
        border = BorderStroke(1.dp, DarkSteel),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (simPhase) {
                SimPhase.NOT_STARTED -> {
                    Button(
                        onClick = onStartMatchClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("start_match_button").height(34.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PitchDarkBg, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Iniciar Partido", color = PitchDarkBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
                SimPhase.FIRST_HALF, SimPhase.SECOND_HALF -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play / Pause
                        Button(
                            onClick = onPlayPauseClick,
                            colors = ButtonDefaults.buttonColors(containerColor = SoftSapphire),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("pause_play_button").height(34.dp)
                        ) {
                            Icon(
                                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = null,
                                tint = GrassEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isPaused) "Reanudar" else "Pausar", color = TextPrimary, fontSize = 11.sp)
                        }

                        // Speed Multiplier (1x, 2x, 5x, 10x)
                        Button(
                            onClick = onSpeedClick,
                            colors = ButtonDefaults.buttonColors(containerColor = SoftSapphire),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("speed_button").height(34.dp)
                        ) {
                            Icon(Icons.Default.FastForward, contentDescription = null, tint = GrassEmerald, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Velocidad: ${playSpeedMultiplier}x", color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
                SimPhase.HALF_TIME_PAUSE -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
                        Text("¡Descanso! Jugadores al vestuario.", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(bottom = 4.dp))
                        Button(
                            onClick = onResumeMatchClick,
                            colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("resume_match_button").height(34.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PitchDarkBg, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Iniciar Segundo Tiempo", color = PitchDarkBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
                SimPhase.FINISHED -> {
                    Button(
                        onClick = onReplayMatchClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SoftSapphire),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("replay_match_button").height(34.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = GrassEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver Repetición", color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun LiveMatchStatsRow(
    livePossessionHome: Int,
    livePossessionAway: Int,
    liveHomeShots: Int,
    liveAwayShots: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Possession Bar Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, DarkSteel),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("POSESIÓN BALÓN", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("${livePossessionHome}%", color = GrassEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("${livePossessionAway}%", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Shots bar Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, DarkSteel),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("DISPAROS DE ZONA", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("$liveHomeShots r", color = GrassEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("$liveAwayShots r", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LiveMatchChronicle(
    simPhase: SimPhase,
    liveEvents: List<MatchEvent>,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
        border = BorderStroke(1.dp, DarkSteel),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("CRÓNICA DE JUEGO MINUTO A MINUTO (ZONA TÁCTICA)", color = NeonAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            
            if (simPhase == SimPhase.NOT_STARTED) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.SportsFootball, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(28.dp))
                        Text("Presiona 'Iniciar Partido' para comenzar la transmisión en vivo del cotejo.", color = TextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(liveEvents, key = { "${it.minute}_${it.type}_${it.description.hashCode()}" }) { event ->
                        val color = when (event.type) {
                            "GOAL_HOME", "GOAL_AWAY" -> CardGold
                            "SHUTOUT_HERO" -> GrassEmerald
                            "YELLOW", "RED" -> StatusInsecureRed
                            "INFO_GENERIC" -> TextSecondary
                            else -> TextPrimary
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "[Min ${event.minute}']",
                                color = GrassEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(55.dp)
                            )
                            Text(
                                text = event.description,
                                color = color,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveMatchPostGameRecap(
    userMatch: Match,
    managerClubId: String,
    modifier: Modifier = Modifier
) {
    // 1. Determine winning club
    val winningClubId = remember(userMatch) {
        when {
            userMatch.homeGoals > userMatch.awayGoals -> userMatch.homeClubId
            userMatch.awayGoals > userMatch.homeGoals -> userMatch.awayClubId
            else -> null // Draw
        }
    }
    val winningClubName = remember(userMatch, winningClubId) {
        when (winningClubId) {
            userMatch.homeClubId -> userMatch.homeClubName
            userMatch.awayClubId -> userMatch.awayClubName
            else -> "Ninguno (Empate)"
        }
    }

    // 2. Find the MVP of the winning team (non-goalkeeper for now, as requested)
    val winningMvp = remember(userMatch.playerStats, winningClubId) {
        if (winningClubId != null) {
            userMatch.playerStats
                .filter { it.clubId == winningClubId && !it.isGoalkeeper }
                .maxByOrNull { it.rating }
        } else {
            // In case of draw, overall non-goalkeeper MVP of the match
            userMatch.playerStats
                .filter { !it.isGoalkeeper }
                .maxByOrNull { it.rating }
        }
    }

    // 3. Find our own team's stats to still display team performance
    val myTeamStats = remember(userMatch.playerStats) {
        userMatch.playerStats.filter { it.clubId == managerClubId }
            .sortedByDescending { it.rating }
    }
    val myMvp = myTeamStats.firstOrNull()

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
        border = BorderStroke(2.dp, CardGold),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = CardGold,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "REPORTE DE RENDIMIENTO & MVP",
                    color = CardGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))

            // Section 1: Winner's MVP (As requested)
            if (winningMvp != null) {
                Text(
                    text = if (winningClubId != null) "👑 MVP DEL EQUIPO GANADOR ($winningClubName)" else "👑 MVP DEL ENCUENTRO (Empate)",
                    color = NeonAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PitchDarkBg)
                        .border(1.5.dp, CardGold, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = winningMvp.playerName,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Club: ${if (winningMvp.clubId == userMatch.homeClubId) userMatch.homeClubName else userMatch.awayClubName}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CardGold)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "NOTA: ${winningMvp.rating}",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("⚽", fontSize = 12.sp)
                                Text(
                                    text = "${winningMvp.goals} ${if (winningMvp.goals == 1) "Gol" else "Goles"}",
                                    color = if (winningMvp.goals > 0) StatusRed else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("👟", fontSize = 12.sp)
                                Text(
                                    text = "${winningMvp.assists} ${if (winningMvp.assists == 1) "Asistencia" else "Asistencias"}",
                                    color = if (winningMvp.assists > 0) StatusTeal else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (winningMvp.playerSpecialty.isNotEmpty() || winningMvp.playerFoot.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (winningMvp.playerSpecialty.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(NeonAmber.copy(alpha = 0.15f))
                                            .border(1.dp, NeonAmber, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(winningMvp.playerSpecialty, color = NeonAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                val footText = if (winningMvp.playerFoot.isNotEmpty()) "🦶 Pie: ${winningMvp.playerFoot}" else ""
                                val heightText = if (winningMvp.playerHeightCm > 0) " | 📏 ${winningMvp.playerHeightCm}cm" else ""
                                if (footText.isNotEmpty()) {
                                    Text(
                                        text = "$footText$heightText",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No se determinó el MVP del encuentro.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 2: Manager's own squad top performers
            if (myMvp != null) {
                Text(
                    text = "📋 DESEMPEÑO DE TU EQUIPO",
                    color = GlacierBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    myTeamStats.take(3).forEach { playerStat ->
                        val isGK = playerStat.isGoalkeeper
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(PitchDarkBg)
                                .border(1.dp, if (playerStat.playerId == myMvp.playerId) CardGold.copy(alpha = 0.3f) else DarkSteel, RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = playerStat.playerName,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (playerStat.playerId == myMvp.playerId) {
                                        Text("⭐", fontSize = 10.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    if (isGK) {
                                        Text(
                                            text = "🧤 ${playerStat.saves} ${if (playerStat.saves == 1) "Parada" else "Paradas"}",
                                            color = GlacierBlue,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Text(
                                            text = "⚽ ${playerStat.goals} G",
                                            color = if (playerStat.goals > 0) StatusRed else TextSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "👟 ${playerStat.assists} A",
                                            color = if (playerStat.assists > 0) StatusTeal else TextSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (playerStat.rating >= 7.5f) GrassEmerald else DarkSteel)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = playerStat.rating.toString(),
                                    color = if (playerStat.rating >= 7.5f) Color.Black else TextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No se encontraron estadísticas del equipo para este encuentro.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun PitchPlayerCard(
    player: com.example.model.Player,
    isCaptain: Boolean,
    isHomeTeam: Boolean,
    modifier: Modifier = Modifier
) {
    val positionColor = when (player.position) {
        com.example.model.Position.GK -> PositionOrangeGK
        com.example.model.Position.DEF -> StatusBlue
        com.example.model.Position.MID -> StatusTeal
        com.example.model.Position.ATT -> StatusRed
    }
    val cardBorderColor = if (isHomeTeam) GrassEmerald else NeonAmber

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = SurfaceCarbon.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, cardBorderColor),
        shadowElevation = 4.dp,
        modifier = modifier.width(62.dp)
    ) {
        Column(
            modifier = Modifier.padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = player.position.name,
                    color = Color.White,
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(positionColor)
                        .padding(horizontal = 2.dp, vertical = 0.5.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isCaptain) {
                        Text("⭐", fontSize = 7.sp)
                    }
                    Text(
                        text = "${player.getOverallRating()}",
                        color = NeonAmber,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Text(
                text = player.lastName.ifEmpty { player.firstName },
                color = TextPrimary,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 1.dp)
            )
            Text(
                text = "${player.heightCm}cm • ${player.preferredFoot.take(3)}",
                color = TextSecondary,
                fontSize = 6.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 1.dp)
            )
        }
    }
}

@Composable
fun VerticalPitch2DView(
    homeClub: com.example.model.Club,
    awayClub: com.example.model.Club,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf("AMBOS") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCarbon)
            .border(1.dp, GrassEmerald, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        // Mode Selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🏟️ CANCHA 2D - ALINEACIONES",
                color = NeonAmber,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(
                    "AMBOS" to "Ambos",
                    "MI_EQUIPO" to homeClub.name.take(8),
                    "RIVAL" to awayClub.name.take(8)
                ).forEach { (mode, label) ->
                    val selected = viewMode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (selected) GrassEmerald else DarkSteel)
                            .clickable { viewMode = mode }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (selected) Color.Black else TextPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2D Pitch Field Container
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E3A1E),
                            Color(0xFF2E5A2E),
                            Color(0xFF1E3A1E)
                        )
                    )
                )
                .border(1.dp, GrassEmerald.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
        ) {
            // Pitch canvas line markings
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val lineCol = Color.White.copy(alpha = 0.35f)
                val stroke = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())

                // Pitch outer border
                drawRect(
                    color = lineCol,
                    style = stroke
                )

                // Halfway Line
                drawLine(
                    color = lineCol,
                    start = androidx.compose.ui.geometry.Offset(0f, size.height / 2f),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2f),
                    strokeWidth = 2.dp.toPx()
                )

                // Center Circle
                drawCircle(
                    color = lineCol,
                    radius = size.width * 0.18f,
                    center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f),
                    style = stroke
                )

                // Top Goal Box
                drawRect(
                    color = lineCol,
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.25f, 0f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.50f, size.height * 0.15f),
                    style = stroke
                )

                // Bottom Goal Box
                drawRect(
                    color = lineCol,
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.25f, size.height * 0.85f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.50f, size.height * 0.15f),
                    style = stroke
                )
            }

            // Render Players on Pitch
            val homeStarters = remember(homeClub.squad) {
                val s = homeClub.squad.filter { it.isStarter }
                if (s.isEmpty()) homeClub.squad.take(11) else s.take(11)
            }
            val awayStarters = remember(awayClub.squad) {
                val s = awayClub.squad.filter { it.isStarter }
                if (s.isEmpty()) awayClub.squad.take(11) else s.take(11)
            }

            if (viewMode == "AMBOS" || viewMode == "MI_EQUIPO") {
                // Home team players (Bottom Half or Full Field)
                val homeOffsets = getFormationSlotOffsets(
                    formation = homeClub.selectedFormation,
                    isTopHalf = false,
                    isFullField = (viewMode == "MI_EQUIPO")
                )
                homeStarters.forEachIndexed { idx, player ->
                    val offset = homeOffsets.getOrElse(idx) { androidx.compose.ui.geometry.Offset(0.5f, 0.8f) }
                    Box(
                        modifier = Modifier.offset(
                            x = (maxWidth * offset.x) - 31.dp,
                            y = (maxHeight * offset.y) - 22.dp
                        )
                    ) {
                        PitchPlayerCard(
                            player = player,
                            isCaptain = (player.id == homeClub.captainPlayerId),
                            isHomeTeam = true
                        )
                    }
                }
            }

            if (viewMode == "AMBOS" || viewMode == "RIVAL") {
                // Away team players (Top Half or Full Field)
                val awayOffsets = getFormationSlotOffsets(
                    formation = awayClub.selectedFormation,
                    isTopHalf = true,
                    isFullField = (viewMode == "RIVAL")
                )
                awayStarters.forEachIndexed { idx, player ->
                    val offset = awayOffsets.getOrElse(idx) { androidx.compose.ui.geometry.Offset(0.5f, 0.2f) }
                    Box(
                        modifier = Modifier.offset(
                            x = (maxWidth * offset.x) - 31.dp,
                            y = (maxHeight * offset.y) - 22.dp
                        )
                    ) {
                        PitchPlayerCard(
                            player = player,
                            isCaptain = (player.id == awayClub.captainPlayerId),
                            isHomeTeam = false
                        )
                    }
                }
            }
        }
    }
}

fun getFormationSlotOffsets(
    formation: String,
    isTopHalf: Boolean,
    isFullField: Boolean
): List<androidx.compose.ui.geometry.Offset> {
    if (isFullField) {
        return listOf(
            androidx.compose.ui.geometry.Offset(0.50f, 0.90f),
            androidx.compose.ui.geometry.Offset(0.15f, 0.74f), androidx.compose.ui.geometry.Offset(0.38f, 0.74f), androidx.compose.ui.geometry.Offset(0.62f, 0.74f), androidx.compose.ui.geometry.Offset(0.85f, 0.74f),
            androidx.compose.ui.geometry.Offset(0.15f, 0.48f), androidx.compose.ui.geometry.Offset(0.38f, 0.48f), androidx.compose.ui.geometry.Offset(0.62f, 0.48f), androidx.compose.ui.geometry.Offset(0.85f, 0.48f),
            androidx.compose.ui.geometry.Offset(0.35f, 0.18f), androidx.compose.ui.geometry.Offset(0.65f, 0.18f)
        )
    }

    return if (!isTopHalf) {
        when (formation) {
            "4-3-3" -> listOf(
                androidx.compose.ui.geometry.Offset(0.50f, 0.92f),
                androidx.compose.ui.geometry.Offset(0.15f, 0.81f), androidx.compose.ui.geometry.Offset(0.38f, 0.81f), androidx.compose.ui.geometry.Offset(0.62f, 0.81f), androidx.compose.ui.geometry.Offset(0.85f, 0.81f),
                androidx.compose.ui.geometry.Offset(0.22f, 0.68f), androidx.compose.ui.geometry.Offset(0.50f, 0.68f), androidx.compose.ui.geometry.Offset(0.78f, 0.68f),
                androidx.compose.ui.geometry.Offset(0.20f, 0.55f), androidx.compose.ui.geometry.Offset(0.50f, 0.55f), androidx.compose.ui.geometry.Offset(0.80f, 0.55f)
            )
            "3-5-2" -> listOf(
                androidx.compose.ui.geometry.Offset(0.50f, 0.92f),
                androidx.compose.ui.geometry.Offset(0.25f, 0.81f), androidx.compose.ui.geometry.Offset(0.50f, 0.81f), androidx.compose.ui.geometry.Offset(0.75f, 0.81f),
                androidx.compose.ui.geometry.Offset(0.12f, 0.68f), androidx.compose.ui.geometry.Offset(0.31f, 0.68f), androidx.compose.ui.geometry.Offset(0.50f, 0.68f), androidx.compose.ui.geometry.Offset(0.69f, 0.68f), androidx.compose.ui.geometry.Offset(0.88f, 0.68f),
                androidx.compose.ui.geometry.Offset(0.35f, 0.55f), androidx.compose.ui.geometry.Offset(0.65f, 0.55f)
            )
            "5-3-2" -> listOf(
                androidx.compose.ui.geometry.Offset(0.50f, 0.92f),
                androidx.compose.ui.geometry.Offset(0.10f, 0.81f), androidx.compose.ui.geometry.Offset(0.30f, 0.81f), androidx.compose.ui.geometry.Offset(0.50f, 0.81f), androidx.compose.ui.geometry.Offset(0.70f, 0.81f), androidx.compose.ui.geometry.Offset(0.90f, 0.81f),
                androidx.compose.ui.geometry.Offset(0.22f, 0.68f), androidx.compose.ui.geometry.Offset(0.50f, 0.68f), androidx.compose.ui.geometry.Offset(0.78f, 0.68f),
                androidx.compose.ui.geometry.Offset(0.35f, 0.55f), androidx.compose.ui.geometry.Offset(0.65f, 0.55f)
            )
            "4-2-3-1" -> listOf(
                androidx.compose.ui.geometry.Offset(0.50f, 0.92f),
                androidx.compose.ui.geometry.Offset(0.15f, 0.82f), androidx.compose.ui.geometry.Offset(0.38f, 0.82f), androidx.compose.ui.geometry.Offset(0.62f, 0.82f), androidx.compose.ui.geometry.Offset(0.85f, 0.82f),
                androidx.compose.ui.geometry.Offset(0.35f, 0.70f), androidx.compose.ui.geometry.Offset(0.65f, 0.70f),
                androidx.compose.ui.geometry.Offset(0.20f, 0.58f), androidx.compose.ui.geometry.Offset(0.50f, 0.58f), androidx.compose.ui.geometry.Offset(0.80f, 0.58f),
                androidx.compose.ui.geometry.Offset(0.50f, 0.51f)
            )
            else -> listOf(
                androidx.compose.ui.geometry.Offset(0.50f, 0.92f),
                androidx.compose.ui.geometry.Offset(0.15f, 0.81f), androidx.compose.ui.geometry.Offset(0.38f, 0.81f), androidx.compose.ui.geometry.Offset(0.62f, 0.81f), androidx.compose.ui.geometry.Offset(0.85f, 0.81f),
                androidx.compose.ui.geometry.Offset(0.15f, 0.68f), androidx.compose.ui.geometry.Offset(0.38f, 0.68f), androidx.compose.ui.geometry.Offset(0.62f, 0.68f), androidx.compose.ui.geometry.Offset(0.85f, 0.68f),
                androidx.compose.ui.geometry.Offset(0.35f, 0.55f), androidx.compose.ui.geometry.Offset(0.65f, 0.55f)
            )
        }
    } else {
        when (formation) {
            "4-3-3" -> listOf(
                androidx.compose.ui.geometry.Offset(0.50f, 0.08f),
                androidx.compose.ui.geometry.Offset(0.85f, 0.19f), androidx.compose.ui.geometry.Offset(0.62f, 0.19f), androidx.compose.ui.geometry.Offset(0.38f, 0.19f), androidx.compose.ui.geometry.Offset(0.15f, 0.19f),
                androidx.compose.ui.geometry.Offset(0.78f, 0.32f), androidx.compose.ui.geometry.Offset(0.50f, 0.32f), androidx.compose.ui.geometry.Offset(0.22f, 0.32f),
                androidx.compose.ui.geometry.Offset(0.80f, 0.45f), androidx.compose.ui.geometry.Offset(0.50f, 0.45f), androidx.compose.ui.geometry.Offset(0.20f, 0.45f)
            )
            "3-5-2" -> listOf(
                androidx.compose.ui.geometry.Offset(0.50f, 0.08f),
                androidx.compose.ui.geometry.Offset(0.75f, 0.19f), androidx.compose.ui.geometry.Offset(0.50f, 0.19f), androidx.compose.ui.geometry.Offset(0.25f, 0.19f),
                androidx.compose.ui.geometry.Offset(0.88f, 0.32f), androidx.compose.ui.geometry.Offset(0.69f, 0.32f), androidx.compose.ui.geometry.Offset(0.50f, 0.32f), androidx.compose.ui.geometry.Offset(0.31f, 0.32f), androidx.compose.ui.geometry.Offset(0.12f, 0.32f),
                androidx.compose.ui.geometry.Offset(0.65f, 0.45f), androidx.compose.ui.geometry.Offset(0.35f, 0.45f)
            )
            "5-3-2" -> listOf(
                androidx.compose.ui.geometry.Offset(0.50f, 0.08f),
                androidx.compose.ui.geometry.Offset(0.90f, 0.19f), androidx.compose.ui.geometry.Offset(0.70f, 0.19f), androidx.compose.ui.geometry.Offset(0.50f, 0.19f), androidx.compose.ui.geometry.Offset(0.30f, 0.19f), androidx.compose.ui.geometry.Offset(0.10f, 0.19f),
                androidx.compose.ui.geometry.Offset(0.78f, 0.32f), androidx.compose.ui.geometry.Offset(0.50f, 0.32f), androidx.compose.ui.geometry.Offset(0.22f, 0.32f),
                androidx.compose.ui.geometry.Offset(0.65f, 0.45f), androidx.compose.ui.geometry.Offset(0.35f, 0.45f)
            )
            "4-2-3-1" -> listOf(
                androidx.compose.ui.geometry.Offset(0.50f, 0.08f),
                androidx.compose.ui.geometry.Offset(0.85f, 0.18f), androidx.compose.ui.geometry.Offset(0.62f, 0.18f), androidx.compose.ui.geometry.Offset(0.38f, 0.18f), androidx.compose.ui.geometry.Offset(0.15f, 0.18f),
                androidx.compose.ui.geometry.Offset(0.65f, 0.30f), androidx.compose.ui.geometry.Offset(0.35f, 0.30f),
                androidx.compose.ui.geometry.Offset(0.80f, 0.42f), androidx.compose.ui.geometry.Offset(0.50f, 0.42f), androidx.compose.ui.geometry.Offset(0.20f, 0.42f),
                androidx.compose.ui.geometry.Offset(0.50f, 0.48f)
            )
            else -> listOf(
                androidx.compose.ui.geometry.Offset(0.50f, 0.08f),
                androidx.compose.ui.geometry.Offset(0.85f, 0.19f), androidx.compose.ui.geometry.Offset(0.62f, 0.19f), androidx.compose.ui.geometry.Offset(0.38f, 0.19f), androidx.compose.ui.geometry.Offset(0.15f, 0.19f),
                androidx.compose.ui.geometry.Offset(0.85f, 0.32f), androidx.compose.ui.geometry.Offset(0.62f, 0.32f), androidx.compose.ui.geometry.Offset(0.38f, 0.32f), androidx.compose.ui.geometry.Offset(0.15f, 0.32f),
                androidx.compose.ui.geometry.Offset(0.65f, 0.45f), androidx.compose.ui.geometry.Offset(0.35f, 0.45f)
            )
        }
    }
}
