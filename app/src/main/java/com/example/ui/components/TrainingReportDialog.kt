package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.PlayerTrainingResult
import com.example.model.WeeklyTrainingReport
import com.example.ui.theme.*

@Composable
fun TrainingReportDialog(
    report: WeeklyTrainingReport,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .padding(16.dp)
                .testTag("training_report_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(2.dp, GrassEmerald)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "Entrenamiento",
                        tint = GrassEmerald,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "INFORME DE ENTRENAMIENTO SEMANAL",
                            color = GrassEmerald,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Fecha del reporte: ${report.weekDate}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                HorizontalDivider(color = DarkSteel, modifier = Modifier.padding(bottom = 12.dp))

                // Explanatory Banner about Performance / MVP
                Surface(
                    color = PitchDarkBg,
                    border = BorderStroke(1.dp, DarkSteel),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GlacierBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "💡 El rendimiento en los partidos (MVP, notas altas) otorga un multiplicador de motivación que incrementa notablemente las probabilidades de asimilar el entrenamiento.",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Scrollable List of Player Updates
                Box(modifier = Modifier.weight(1f)) {
                    if (report.results.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ningún jugador mostró variaciones significativas esta semana.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(report.results, key = { it.playerId }) { result ->
                                PlayerTrainingResultCard(result)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald, contentColor = PitchDarkBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("dismiss_training_dialog_btn")
                ) {
                    Text(text = "Entendido, Continuar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun PlayerTrainingResultCard(result: PlayerTrainingResult) {
    val scouting = result.scoutingLevel

    Card(
        colors = CardDefaults.cardColors(containerColor = PitchDarkBg),
        border = BorderStroke(1.dp, if (result.decrements.isNotEmpty()) StatusRed.copy(alpha = 0.5f) else DarkSteel),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Player name and position
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (result.position.name) {
                                    "GK" -> StatusTeal.copy(alpha = 0.15f)
                                    "DEF" -> StatusBlue.copy(alpha = 0.15f)
                                    "MID" -> GrassEmerald.copy(alpha = 0.15f)
                                    else -> StatusRed.copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = result.position.name,
                            color = when (result.position.name) {
                                "GK" -> StatusTeal
                                "DEF" -> StatusBlue
                                "MID" -> GrassEmerald
                                else -> StatusRed
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = result.playerName,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Scouting badge
                Text(
                    text = "Ojeo: ${scouting}%",
                    color = if (scouting >= 80) GrassEmerald else if (scouting >= 40) NeonAmber else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display changes depending on scouting level
            when {
                scouting >= 80 -> {
                    // Full visibility
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        result.increments.forEach { (attr, inc) ->
                            val label = translateAttribute(attr)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🟢", fontSize = 10.sp)
                                Text(text = "+$inc $label", color = GrassEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        result.decrements.forEach { (attr, dec) ->
                            val label = translateAttribute(attr)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🔴", fontSize = 10.sp)
                                Text(text = "-$dec $label (Decline Físico)", color = StatusRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                scouting >= 40 -> {
                    // Medium visibility (fuzzy areas)
                    val fuzzyIncrements = mutableListOf<String>()
                    val fuzzyDecrements = mutableListOf<String>()

                    if (result.increments.containsKey("attack") || result.increments.containsKey("midfield")) {
                        fuzzyIncrements.add("Mejoró técnica de balón, ataque y distribución")
                    }
                    if (result.increments.containsKey("defense") || result.increments.containsKey("goalkeeper")) {
                        fuzzyIncrements.add("Consolidó destrezas defensivas y cobertura de espacios")
                    }
                    if (result.increments.containsKey("speed") || result.increments.containsKey("stamina") || result.increments.containsKey("physical")) {
                        fuzzyIncrements.add("Mostró optimizaciones de fuerza, velocidad o resistencia")
                    }
                    if (result.increments.containsKey("mental")) {
                        fuzzyIncrements.add("Consiguió mayor concentración y templanza mental")
                    }

                    if (result.decrements.containsKey("speed") || result.decrements.containsKey("stamina") || result.decrements.containsKey("physical")) {
                        fuzzyDecrements.add("Evidencia fatiga o declive en su condición física general")
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        fuzzyIncrements.forEach { text ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🟡", fontSize = 10.sp)
                                Text(text = text, color = GlacierBlue, fontSize = 12.sp)
                            }
                        }
                        fuzzyDecrements.forEach { text ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🟠", fontSize = 10.sp)
                                Text(text = text, color = StatusAmber, fontSize = 12.sp)
                            }
                        }
                    }
                }
                else -> {
                    // Low visibility
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceCarbon.copy(alpha = 0.5f))
                            .border(1.dp, DarkSteel, RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text("❓", fontSize = 11.sp)
                        Text(
                            text = "Se registra progreso en su ficha técnica, pero el nivel de ojeo (${scouting}%) es demasiado bajo para discernir estadísticas exactas.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

fun translateAttribute(attr: String): String {
    return when (attr) {
        "attack" -> "Tiro / Remate"
        "defense" -> "Defensa / Marcaje"
        "midfield" -> "Mediocampo / Pase"
        "speed" -> "Velocidad"
        "stamina" -> "Resistencia"
        "goalkeeper" -> "Portería / Atajada"
        "mental" -> "Estilo Mental / Presión"
        "physical" -> "Fuerza Física"
        else -> attr.replaceFirstChar { it.uppercase() }
    }
}
