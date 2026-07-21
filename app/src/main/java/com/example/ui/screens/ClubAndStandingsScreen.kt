package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Club
import com.example.model.League
import com.example.model.Coach
import com.example.engine.GameSettings
import com.example.ui.theme.*

@Composable
fun ClubAndStandingsScreen(
    club: Club?,
    league: League?,
    newsLog: List<String>,
    availableCoaches: List<Coach> = emptyList(),
    onHireCoach: (Coach) -> Unit = {},
    onFireCoach: (String) -> Unit = {}
) {
    val currencySymbol by GameSettings.currencySymbol.collectAsState()
    val isAbbreviationEnabled by GameSettings.isAbbreviationEnabled.collectAsState()

    // Resolve upcoming fixture round match for the manager's club
    val nextMatch = remember(league, club) {
        if (league != null && club != null) {
            val currentRoundIndex = league.currentRound
            val fixtures = league.fixtures
            if (currentRoundIndex < fixtures.size) {
                fixtures[currentRoundIndex].matches.firstOrNull { 
                    it.homeClubId == club.id || it.awayClubId == club.id 
                }
            } else {
                null
            }
        } else {
            null
        }
    }

    val opponentClub = remember(league, nextMatch, club) {
        if (league != null && nextMatch != null && club != null) {
            val opponentId = if (nextMatch.homeClubId == club.id) nextMatch.awayClubId else nextMatch.homeClubId
            league.clubs.firstOrNull { it.id == opponentId }
        } else {
            null
        }
    }

    var showCoachingStaff by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Tab Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { showCoachingStaff = false },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (!showCoachingStaff) GrassEmerald.copy(alpha = 0.15f) else Color.Transparent,
                    contentColor = if (!showCoachingStaff) GrassEmerald else TextSecondary
                ),
                border = BorderStroke(1.dp, if (!showCoachingStaff) GrassEmerald else DarkSteel),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Text(
                    text = "Tabla y Rivales",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = { showCoachingStaff = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (showCoachingStaff) GrassEmerald.copy(alpha = 0.15f) else Color.Transparent,
                    contentColor = if (showCoachingStaff) GrassEmerald else TextSecondary
                ),
                border = BorderStroke(1.dp, if (showCoachingStaff) GrassEmerald else DarkSteel),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Text(
                    text = "Cuerpo Técnico 💼",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (!showCoachingStaff) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: League Standings Table
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                    border = BorderStroke(1.dp, DarkSteel),
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = league?.name ?: "LIGA SIN CONFIGURAR",
                            color = GrassEmerald,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkSteel)
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Club", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                            Text("PJ", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center)
                            Text("PG", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center)
                            Text("PE", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center)
                            Text("PP", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center)
                            Text("GF:GC", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                            Text("Pts", color = NeonAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                        }

                        val sortedClubs = remember(league?.clubs, league?.currentRound) {
                            league?.clubs?.sortedWith(compareByDescending<Club> { it.points }.thenByDescending { it.goalDifference }) ?: emptyList()
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(sortedClubs, key = { it.id }) { item ->
                                val isUserClub = item.id == club?.id
                                val rowBg = if (isUserClub) DarkSteel.copy(alpha = 0.5f) else Color.Transparent

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(rowBg)
                                        .padding(vertical = 6.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.name,
                                        color = if (isUserClub) GrassEmerald else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isUserClub) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.weight(2f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(item.played.toString(), color = TextPrimary, fontSize = 11.sp, modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center)
                                    Text(item.wins.toString(), color = TextPrimary, fontSize = 11.sp, modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center)
                                    Text(item.draws.toString(), color = TextPrimary, fontSize = 11.sp, modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center)
                                    Text(item.losses.toString(), color = TextPrimary, fontSize = 11.sp, modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center)
                                    Text("${item.goalsFor}:${item.goalsAgainst}", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                                    Text(
                                        item.points.toString(),
                                        color = NeonAmber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(0.5f),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }

                // Right Column: Club Stats, Upcoming Rival Preview, and Live Logging
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Upcoming Rival Preview Card
                    if (opponentClub != null) {
                        val (oppDef, oppMid, oppAtt) = opponentClub.getTeamRatings()

                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                            border = BorderStroke(1.dp, NeonAmber),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "PRÓXIMO ENCUENTRO ⚔️",
                                    color = NeonAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = opponentClub.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Formación Rival: ${opponentClub.selectedFormation}",
                                    color = GlacierBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Defense progress bar
                                RivalRatingRow(label = "Defensa Rival", value = oppDef, color = StatusBlue)
                                Spacer(modifier = Modifier.height(6.dp))
                                // Midfield progress bar
                                RivalRatingRow(label = "Medio Rival", value = oppMid, color = StatusTeal)
                                Spacer(modifier = Modifier.height(6.dp))
                                // Attack progress bar
                                RivalRatingRow(label = "Ataque Rival", value = oppAtt, color = StatusRed)
                            }
                        }
                    }

                    // 2. Infrastructure Card
                    if (club != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                            border = BorderStroke(1.dp, DarkSteel),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("ESTRUCTURA DEPORTIVA", color = GrassEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Estadio:", color = TextSecondary, fontSize = 12.sp)
                                    Text("${club.stadiumCapacity} locales (Ticket: ${GameSettings.formatMoney(club.ticketPrice.toLong())})", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Entrenamiento:", color = TextSecondary, fontSize = 12.sp)
                                    Text("★".repeat(club.trainingFacilities) + "☆".repeat(5 - club.trainingFacilities), color = NeonAmber, fontSize = 11.sp)
                                }
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Cantera (Academia):", color = TextSecondary, fontSize = 12.sp)
                                    Text("★".repeat(club.youthAcademy) + "☆".repeat(5 - club.youthAcademy), color = NeonAmber, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // 3. News Ticker
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                        border = BorderStroke(1.dp, DarkSteel),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("BITÁCORA DEL UNIVERSO", color = NeonAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(newsLog, key = { it.hashCode() }) { news ->
                                    Text(
                                        text = "• $news",
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(bottom = 4.dp),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            CoachingStaffView(
                club = club,
                availableCoaches = availableCoaches,
                onHireCoach = onHireCoach,
                onFireCoach = onFireCoach
            )
        }
    }
}

@Composable
fun RivalRatingRow(label: String, value: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = TextSecondary, fontSize = 11.sp)
            Text(
                text = value.toString(),
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
            color = color,
            trackColor = DarkSteel
        )
    }
}
