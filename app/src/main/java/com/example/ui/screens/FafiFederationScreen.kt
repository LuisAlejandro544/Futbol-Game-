package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FAFI
import com.example.model.Fafitrait
import com.example.model.Manager
import com.example.ui.theme.*

@Composable
fun FafiFederationScreen(
    fafi: FAFI,
    manager: Manager,
    onPRCampaignClick: () -> Unit,
    onAcceptSummonClick: () -> Unit,
    onResignSummonClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // FEDEBOL main card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, DarkSteel),
            modifier = Modifier.fillMaxWidth().testTag("fedebol_cabinet_card")
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("GABINETE FEDERAL INTERNACIONAL (FEDEBOL)", color = GrassEmerald, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("La Federación Internacional de Balompié regula de manera global las directrices legislativas, formatos del Mundial Absoluto, finanzas e impuestos de transferencias continentales cada 4 años.", color = TextSecondary, fontSize = 11.sp)
                
                HorizontalDivider(color = DarkSteel)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PRESIDENTE DE FEDEBOL", color = NeonAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(fafi.president.name.uppercase(), color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Rasgo Político: ", color = TextSecondary, fontSize = 11.sp)
                            val badgeCol = when (fafi.president.trait) {
                                Fafitrait.CORRUPTO -> StatusRed
                                Fafitrait.AMBICIOSO -> StatusAmber
                                Fafitrait.EXPANSIVO -> StatusBlue
                                Fafitrait.TRADICIONALISTA -> StatusTeal
                            }
                            Text(
                                text = fafi.president.trait.name,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(badgeCol)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("AÑO DE ELECCIONES", color = TextSecondary, fontSize = 11.sp)
                        Text("${fafi.yearsUntilElection} años para votación", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Impuesto de Fichajes: ${fafi.transferTaxPercent}%", color = TextSecondary, fontSize = 11.sp)
                        Text("Mundial Absoluto: ${fafi.worldCupSize} selecciones", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                HorizontalDivider(color = DarkSteel)

                Text("REGLAMENTO GLOBAL VIGENTE", color = GrassEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Card(
                    colors = CardDefaults.cardColors(containerColor = PitchDarkBg),
                    border = BorderStroke(1.dp, DarkSteel),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = fafi.currentRuleSet,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // NATIONAL SUMMONS CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, GrassEmerald.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("national_summon_card")
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🦁 OFICINA DE CONVOCATORIA NACIONAL (PREVIEW)", color = NeonAmber, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("La máxima distinción de tu carrera: dirigir una selección de tu región. Tu reputación internacional y éxitos incrementan tus posibilidades de convocatoria.", color = TextSecondary, fontSize = 11.sp)
                
                HorizontalDivider(color = DarkSteel)

                if (manager.isSummoned) {
                    // Summoned state
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkEmeraldBg),
                        border = BorderStroke(1.dp, GrassEmerald),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("¡ESTÁS CONVOCADO!", color = GrassEmerald, fontSize = 16.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                            Text(
                                text = "Diriges actualmente a: ${manager.summonedNationalTeam}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Estás recibiendo un sueldo nacional de +$2,500 semanales.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                            
                            Button(
                                onClick = onResignSummonClick,
                                colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag("resign_summon_button")
                            ) {
                                Text("Renunciar a la Selección", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Not summoned state
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Recomendación de Selección:", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${manager.nationalSummonProgress}%", color = if (manager.nationalSummonProgress >= 100) GrassEmerald else NeonAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        LinearProgressIndicator(
                            progress = { manager.nationalSummonProgress.toFloat() / 100f },
                            color = if (manager.nationalSummonProgress >= 100) GrassEmerald else GlacierBlue,
                            trackColor = DarkSteel,
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                        )

                        if (manager.nationalSummonProgress >= 100) {
                            Text("¡Felicidades! Has sido convocado oficialmente debido a tu alto rendimiento continental.", color = GrassEmerald, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Button(
                                onClick = onAcceptSummonClick,
                                colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp).testTag("accept_summon_button")
                            ) {
                                Text("Aceptar Convocatoria Nacional", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        } else {
                            Text("Incursiona en relaciones públicas de la confederación para acelerar tu consideración nacional.", color = TextSecondary, fontSize = 11.sp)
                            Button(
                                onClick = onPRCampaignClick,
                                enabled = manager.personalWealth >= 3000L,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlacierBlue,
                                    disabledContainerColor = DarkSteel
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp).testTag("pr_campaign_button")
                            ) {
                                Text("Lobby e Inversión en Relaciones Públicas ($3,000)", color = if (manager.personalWealth >= 3000L) Color.Black else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // CONTINENTAL CONFEDERATIONS CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, DarkSteel),
            modifier = Modifier.fillMaxWidth().testTag("continental_confederations_card")
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🌎 CONFEDERACIONES CONTINENTALES PROCEDURALES", color = GlacierBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("La estructura de federaciones continentales opera bajo nombres ficticios de forma paralela en la simulación global. (Nota: Estas competiciones se pulirán con calendarios específicos en el futuro).", color = TextSecondary, fontSize = 11.sp)
                
                HorizontalDivider(color = DarkSteel)

                // SUDAMBOL
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🟢 SUDAMBOL (América del Sur)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Regula países como Argentina, Brasil, Colombia, Chile, Uruguay, Paraguay, Ecuador, Perú, Venezuela y Bolivia.", color = TextSecondary, fontSize = 11.sp)
                    Text("• Competición de Clubes: Copa de los Conquistadores (Copa del Sur)", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("• Competición de Selecciones: Copa Continental de las Alturas (Copa del Sur)", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }

                HorizontalDivider(color = DarkSteel.copy(alpha = 0.5f))

                // EUROBOL
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🔵 EUROBOL (Europa)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Regula a Francia en la simulación activa, expandiéndose a otras ligas principales en el futuro.", color = TextSecondary, fontSize = 11.sp)
                    Text("• Competición de Clubes: Euro-Champions de Clubes", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("• Competición de Selecciones: Euro-Copa Ficticia de Naciones", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }

                HorizontalDivider(color = DarkSteel.copy(alpha = 0.5f))

                // NORAMBOL
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🟠 NORAMBOL (Norteamérica y Centro)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Regula países como México, Costa Rica, Panamá, Honduras, El Salvador, Guatemala y Nicaragua.", color = TextSecondary, fontSize = 11.sp)
                    Text("• Competición de Clubes: Copa de Campeones del Norte", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
