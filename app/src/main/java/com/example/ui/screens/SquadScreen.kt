package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SportsFootball
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Club
import com.example.model.Player
import com.example.model.Position
import com.example.ui.components.ProceduralPlayerFace
import com.example.engine.GameSettings
import com.example.ui.theme.*

@Composable
fun SquadScreen(
    club: Club?,
    selectedPlayer: Player?,
    onPlayerClick: (Player) -> Unit,
    onFormationChange: (String) -> Unit,
    onSetCaptain: (Player) -> Unit,
    onSwapPlayers: (String, String) -> Unit
) {
    if (club == null) return

    val currencySymbol by GameSettings.currencySymbol.collectAsState()
    val isAbbreviationEnabled by GameSettings.isAbbreviationEnabled.collectAsState()
    var showSwapDialog by remember { mutableStateOf(false) }

    val formations = listOf("4-4-2", "4-3-3", "3-5-2", "5-3-2", "4-2-3-1")

    val formationDetails = mapOf(
        "4-4-2" to "Balance estándar táctico sin modificadores.",
        "4-3-3" to "Presión ofensiva (+10% Ataque, -5% Defensa)",
        "3-5-2" to "Dominio de posesión (+15% Medio, -10% Defensa)",
        "5-3-2" to "Cerrojo defensivo (+15% Defensa, -10% Ataque)",
        "4-2-3-1" to "Transición equilibrada moderna (+5% Medio, +5% Ataque)"
    )

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left pane: Tactical Setup + Squad Roster
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, DarkSteel),
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Tactical Selection Header
                Text(
                    text = "CONFIGURACIÓN TÁCTICA",
                    color = NeonAmber,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = PitchDarkBg),
                    border = BorderStroke(1.dp, DarkSteel),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            formations.forEach { form ->
                                val isActive = club.selectedFormation == form
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                                        .background(if (isActive) GrassEmerald else DarkSteel)
                                        .clickable { onFormationChange(form) }
                                        .padding(vertical = 6.dp, horizontal = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = form,
                                        color = if (isActive) Color.Black else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = formationDetails[club.selectedFormation] ?: "",
                            color = GlacierBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ESTILO TÁCTICO",
                            color = NeonAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        val tactics = listOf("Equilibrada", "Agresiva", "Defensiva", "Contraataque", "Posesión", "Presión Alta")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            tactics.forEach { tac ->
                                val isTacActive = club.selectedTactic == tac
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                        .background(if (isTacActive) NeonAmber else DarkSteel)
                                        .clickable { club.selectedTactic = tac }
                                        .padding(vertical = 4.dp, horizontal = 1.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tac,
                                        color = if (isTacActive) Color.Black else TextPrimary,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = DarkSteel, modifier = Modifier.padding(bottom = 10.dp))

                Text(
                    text = "PLANTILLA PROFESIONAL",
                    color = GrassEmerald,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val starters = club.squad.filter { it.isStarter }
                val substitutes = club.squad.filter { !it.isStarter }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        Text(
                            text = "TITULARES (${starters.size})",
                            color = NeonAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }
                    items(starters, key = { it.id }) { player ->
                        val isSelected = selectedPlayer?.id == player.id
                        val isCaptain = player.id == club.captainPlayerId
                        val cardBg = if (isSelected) DarkSteel else SurfaceCarbon.copy(alpha = 0.5f)
                        val borderCol = if (isSelected) GrassEmerald else if (isCaptain) NeonAmber else DarkSteel

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                .background(cardBg)
                                .border(1.dp, borderCol, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                .clickable { onPlayerClick(player) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ProceduralPlayerFace(player = player, size = 32.dp)
                                Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val badgeBg = when (player.position) {
                                        Position.GK -> PositionOrangeGK
                                        Position.DEF -> StatusBlue
                                        Position.MID -> StatusTeal
                                        Position.ATT -> StatusRed
                                    }
                                    Text(
                                        text = player.position.name,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                            .background(badgeBg)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = player.fullName,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (isCaptain) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Capitán",
                                            tint = NeonAmber,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Edad: ${player.age} | Energía: ${player.energy}% | Moral: ${player.moral}%",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                            
                            // Visual display of overall rating
                            Text(
                                text = player.getOverallRating().toString(),
                                color = if (player.getOverallRating() >= 80) NeonAmber else GrassEmerald,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    item {
                        Text(
                            text = "SUPLENTES EN BANCA (${substitutes.size})",
                            color = GlacierBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )
                    }
                    items(substitutes, key = { it.id }) { player ->
                        val isSelected = selectedPlayer?.id == player.id
                        val isCaptain = player.id == club.captainPlayerId
                        val cardBg = if (isSelected) DarkSteel else SurfaceCarbon.copy(alpha = 0.5f)
                        val borderCol = if (isSelected) GrassEmerald else if (isCaptain) NeonAmber else DarkSteel

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                .background(cardBg)
                                .border(1.dp, borderCol, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                .clickable { onPlayerClick(player) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ProceduralPlayerFace(player = player, size = 32.dp)
                                Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val badgeBg = when (player.position) {
                                        Position.GK -> PositionOrangeGK
                                        Position.DEF -> StatusBlue
                                        Position.MID -> StatusTeal
                                        Position.ATT -> StatusRed
                                    }
                                    Text(
                                        text = player.position.name,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                            .background(badgeBg)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = player.fullName,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (isCaptain) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Capitán",
                                            tint = NeonAmber,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Edad: ${player.age} | Energía: ${player.energy}% | Moral: ${player.moral}%",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                            
                            // Visual display of overall rating
                            Text(
                                text = player.getOverallRating().toString(),
                                color = if (player.getOverallRating() >= 80) NeonAmber else GrassEmerald,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Right pane: Player Detailed Scouting Report & Attributes
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, DarkSteel),
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        ) {
            if (selectedPlayer != null) {
                val isCaptainOfClub = selectedPlayer.id == club.captainPlayerId

                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ProceduralPlayerFace(player = selectedPlayer, size = 52.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(selectedPlayer.fullName.uppercase(), color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    if (isCaptainOfClub) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Capitán",
                                            tint = NeonAmber,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text("Nacionalidad: ${selectedPlayer.country} | Edad: ${selectedPlayer.age} años", color = TextSecondary, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "📏 ${selectedPlayer.heightCm} cm | ⚖️ ${selectedPlayer.weightKg} kg | 🦶 Pie: ${selectedPlayer.preferredFoot}",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                            .background(NeonAmber.copy(alpha = 0.15f))
                                            .border(1.dp, NeonAmber, androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(selectedPlayer.specialty, color = NeonAmber, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                            .background(GlacierBlue.copy(alpha = 0.15f))
                                            .border(1.dp, GlacierBlue, androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(selectedPlayer.personality, color = GlacierBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (selectedPlayer.position == Position.GK) {
                                        Box(
                                            modifier = Modifier
                                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                                .background(GlacierBlue.copy(alpha = 0.2f))
                                                .border(1.dp, GlacierBlue, androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("🧤 Atajadas: ${selectedPlayer.saves}", color = GlacierBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                                .background(StatusRed.copy(alpha = 0.2f))
                                                .border(1.dp, StatusRed, androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("⚽ Goles: ${selectedPlayer.goals}", color = StatusRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                                .background(StatusTeal.copy(alpha = 0.2f))
                                                .border(1.dp, StatusTeal, androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("👟 Asistencias: ${selectedPlayer.assists}", color = StatusTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                            .background(NeonAmber.copy(alpha = 0.2f))
                                            .border(1.dp, NeonAmber, androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("⭐ Rendimiento: ${selectedPlayer.matchPerformanceLast}", color = NeonAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                
                                val perfValue = selectedPlayer.matchPerformanceLast
                                val (bonusText, bonusColor, bonusBg) = when {
                                    perfValue >= 8.0f -> Triple(
                                        "🚀 MOTIVACIÓN EXCELENTE: +25% prob. de entrenamiento por nivel mundial (${perfValue})",
                                        GrassEmerald,
                                        GrassEmerald.copy(alpha = 0.12f)
                                    )
                                    perfValue >= 7.0f -> Triple(
                                        "📈 MOTIVACIÓN ALTA: +12% prob. de entrenamiento por gran desempeño (${perfValue})",
                                        GlacierBlue,
                                        GlacierBlue.copy(alpha = 0.12f)
                                    )
                                    perfValue < 5.0f -> Triple(
                                        "⚠️ MOTIVACIÓN COMPROMETIDA: -5% prob. de entrenamiento por bajo nivel (${perfValue})",
                                        StatusRed,
                                        StatusRed.copy(alpha = 0.12f)
                                    )
                                    else -> Triple(
                                        "☕ MOTIVACIÓN ESTÁNDAR: Sin modificadores de rendimiento de partido (${perfValue})",
                                        TextSecondary,
                                        DarkSteel.copy(alpha = 0.3f)
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                                        .background(bonusBg)
                                        .border(1.dp, bonusColor.copy(alpha = 0.5f), androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = bonusText,
                                        color = bonusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                            if (selectedPlayer.isInjured) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                                        .background(StatusRed.copy(alpha = 0.15f))
                                        .border(1.dp, StatusRed, androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "🚑 REPORTE MÉDICO: LESIONADO",
                                            color = StatusRed,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Diagnóstico: ${if (selectedPlayer.injuryName.isNotEmpty()) selectedPlayer.injuryName else "Lesión Muscular"}",
                                            color = TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Baja estimada: ${selectedPlayer.injuryDurationWeeks} semana(s)",
                                            color = TextSecondary,
                                            fontSize = 10.5.sp
                                        )
                                        Text(
                                            text = "⚠️ Nota: Al recibir el alta médica, 1 o 2 estadísticas pueden mermarse levemente.",
                                            color = NeonAmber,
                                            fontSize = 9.5.sp
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "OVR: ${selectedPlayer.getOverallRating()}",
                                color = NeonAmber,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Captain designation action button
                        Spacer(modifier = Modifier.height(10.dp))
                        if (isCaptainOfClub) {
                            Button(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = DarkSteel,
                                    disabledContentColor = NeonAmber
                                ),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("👑 CAPITÁN Y LÍDER DEL EQUIPO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { onSetCaptain(selectedPlayer) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SurfaceCarbon,
                                    contentColor = TextPrimary
                                ),
                                border = BorderStroke(1.dp, NeonAmber),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ) {
                                Text("👑 DESIGNAR COMO LÍDER / CAPITÁN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showSwapDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedPlayer.isStarter) StatusBlue else StatusTeal,
                                contentColor = Color.White
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsFootball,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedPlayer.isStarter) "🔄 REEMPLAZAR (MANDAR A BANCA)" else "🔄 ASCENDER A TITULAR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        HorizontalDivider(color = DarkSteel, modifier = Modifier.padding(vertical = 10.dp))

                        Text("REPORTE DE OJEO (Descubierto: ${selectedPlayer.scoutingLevel}%)", color = GrassEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Fidelity and psychological metrics
                    item {
                        Text("ESTADO DE FIDELIDAD Y COMPROMISO", color = GlacierBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PitchDarkBg),
                            border = BorderStroke(1.dp, DarkSteel),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Loyalty Row
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Lealtad Profesional", color = TextSecondary, fontSize = 11.sp)
                                        Text("${selectedPlayer.loyalty}%", color = if (selectedPlayer.loyalty >= 75) GrassEmerald else if (selectedPlayer.loyalty >= 40) StatusAmber else StatusRed, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { selectedPlayer.loyalty / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
                                        color = if (selectedPlayer.loyalty >= 75) GrassEmerald else if (selectedPlayer.loyalty >= 40) StatusAmber else StatusRed,
                                        trackColor = DarkSteel
                                    )
                                }

                                // Club Appreciation Row
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Aprecio al Club", color = TextSecondary, fontSize = 11.sp)
                                        Text("${selectedPlayer.clubAppreciation}%", color = GlacierBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { selectedPlayer.clubAppreciation / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
                                        color = GlacierBlue,
                                        trackColor = DarkSteel
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = DarkSteel, modifier = Modifier.padding(vertical = 10.dp))
                    }

                    // Attributes matrix
                    item {
                        Text("ATRIBUTOS TÉCNICOS Y FÍSICOS", color = GrassEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AttributeRow("Ataque / Remate", selectedPlayer.getScoutedAttributeString("attack", selectedPlayer.attributes.attack))
                            AttributeRow("Defensa / Marcaje", selectedPlayer.getScoutedAttributeString("defense", selectedPlayer.attributes.defense))
                            AttributeRow("Mediocampo / Pase", selectedPlayer.getScoutedAttributeString("midfield", selectedPlayer.attributes.midfield))
                            AttributeRow("Velocidad Base", selectedPlayer.getScoutedAttributeString("speed", selectedPlayer.attributes.speed))
                            AttributeRow("Fuerza Física", selectedPlayer.getScoutedAttributeString("physical", selectedPlayer.attributes.physical))
                            AttributeRow("Estilo Mental", selectedPlayer.getScoutedAttributeString("mental", selectedPlayer.attributes.mental))
                            AttributeRow("Habilidad Arquero", selectedPlayer.getScoutedAttributeString("goalkeeper", selectedPlayer.attributes.goalkeeper))
                        }
                        HorizontalDivider(color = DarkSteel, modifier = Modifier.padding(vertical = 10.dp))
                    }

                    // Inmutable traits
                    item {
                        Text("RASGOS PSICOLÓGICOS Y FÍSICOS", color = NeonAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        if (selectedPlayer.traits.isEmpty()) {
                            Text("Ningún rasgo especial detectado.", color = TextSecondary, fontSize = 12.sp)
                        } else {
                            selectedPlayer.traits.forEach { trait ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = PitchDarkBg),
                                    border = StrokeBorderDefault(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(trait.displayName, color = GrassEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(trait.description, color = TextPrimary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = DarkSteel, modifier = Modifier.padding(vertical = 10.dp))
                    }

                    // Contract
                    item {
                        Text("CONDICIONES CONTRACTUALES", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Valor de Mercado:", color = TextSecondary, fontSize = 12.sp)
                            Text(GameSettings.formatMoney(selectedPlayer.marketValue), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Salario Semanal:", color = TextSecondary, fontSize = 12.sp)
                            Text("${GameSettings.formatMoney(selectedPlayer.salary)}/sem", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SportsFootball, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Selecciona un futbolista para auditar su perfil técnico", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (showSwapDialog && selectedPlayer != null) {
        PlayerSwapDialog(
            club = club,
            selectedPlayer = selectedPlayer,
            onDismiss = { showSwapDialog = false },
            onSwapPlayers = onSwapPlayers
        )
    }
}

@Composable
fun AttributeRow(label: String, valStr: String) {
    // Check if we can parse a numeric value or average from range
    val parsedValue: Float? = remember(valStr) {
        val trimmed = valStr.trim()
        if (trimmed == "??" || trimmed.contains("?")) {
            null
        } else if (trimmed.contains("-")) {
            val parts = trimmed.split("-")
            val low = parts.getOrNull(0)?.trim()?.toFloatOrNull()
            val high = parts.getOrNull(1)?.trim()?.toFloatOrNull()
            if (low != null && high != null) {
                (low + high) / 2f
            } else {
                null
            }
        } else {
            trimmed.toFloatOrNull()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = TextSecondary, fontSize = 11.sp)
            Text(
                text = valStr,
                color = if (parsedValue != null) TextPrimary else TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        if (parsedValue != null) {
            LinearProgressIndicator(
                progress = { parsedValue / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
                color = if (parsedValue >= 75) GrassEmerald else if (parsedValue >= 50) NeonAmber else Color.Red,
                trackColor = DarkSteel
            )
        } else {
            // Unrevealed scouting state shows a dashed empty bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp))
                    .background(DarkSteel)
            )
        }
    }
}

@Composable
private fun StrokeBorderDefault() = BorderStroke(1.dp, DarkSteel)
