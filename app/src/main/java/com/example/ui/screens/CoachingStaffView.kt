package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.model.Club
import com.example.model.Coach
import com.example.engine.GameSettings
import com.example.ui.theme.*

@Composable
fun CoachingStaffView(
    club: Club?,
    availableCoaches: List<Coach>,
    onHireCoach: (Coach) -> Unit,
    onFireCoach: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left Column: Current Coaching Staff
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, DarkSteel),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "CUERPO TÉCNICO CONTRATADO",
                    color = GrassEmerald,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                val currentCoaches = club?.coaches ?: emptyList()
                val totalWeeklyWages = currentCoaches.sumOf { it.salary }
                
                Text(
                    text = "Sueldos semanales: ${GameSettings.formatMoney(totalWeeklyWages)} (Se deducen del presupuesto del club)",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (currentCoaches.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes entrenadores contratados en este momento.\n\nContrata especialistas en el panel de reclutamiento para aumentar radicalmente las probabilidades de progresión estadística semanal.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(currentCoaches, key = { it.id }) { coach ->
                            CurrentCoachItem(coach = coach, onFire = { onFireCoach(coach.id) })
                        }
                    }
                }
            }
        }

        // Right Column: Recruit Coaches
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, DarkSteel),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "MERCADO DE ENTRENADORES",
                    color = NeonAmber,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "El mercado de postulantes cambia semanalmente. Límite: un (1) especialista de cada área técnica.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (availableCoaches.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay candidatos disponibles esta semana.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(availableCoaches, key = { it.id }) { coach ->
                            val alreadyHiredSpeciality = club?.coaches?.any { it.speciality == coach.speciality } ?: false
                            val canAfford = (club?.budget ?: 0L) >= coach.salary
                            
                            RecruitCoachItem(
                                coach = coach,
                                alreadyHiredSpeciality = alreadyHiredSpeciality,
                                canAfford = canAfford,
                                onHire = { onHireCoach(coach) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentCoachItem(coach: Coach, onFire: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PitchDarkBg),
        border = BorderStroke(1.dp, DarkSteel),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = coach.name,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = coach.specialityLabel,
                        color = GlacierBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onFire,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = StatusRed),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    border = BorderStroke(1.dp, StatusRed),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Despedir", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nivel:", color = TextSecondary, fontSize = 11.sp)
                    Text("★".repeat(coach.level), color = NeonAmber, fontSize = 12.sp)
                }
                Text(
                    text = "${GameSettings.formatMoney(coach.salary)}/sem",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RecruitCoachItem(
    coach: Coach,
    alreadyHiredSpeciality: Boolean,
    canAfford: Boolean,
    onHire: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PitchDarkBg),
        border = BorderStroke(1.dp, DarkSteel),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = coach.name,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = coach.specialityLabel,
                        color = GlacierBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onHire,
                    enabled = !alreadyHiredSpeciality && canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GrassEmerald,
                        contentColor = PitchDarkBg,
                        disabledContainerColor = DarkSteel,
                        disabledContentColor = TextSecondary
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = if (alreadyHiredSpeciality) "Ocupado" else "Contratar",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Calidad:", color = TextSecondary, fontSize = 11.sp)
                    Text("★".repeat(coach.level) + "☆".repeat(5 - coach.level), color = NeonAmber, fontSize = 12.sp)
                }
                Text(
                    text = "${GameSettings.formatMoney(coach.salary)}/sem",
                    color = if (canAfford) GrassEmerald else StatusRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
