package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Manager
import com.example.ui.theme.*

@Composable
fun ManagerCareerScreen(
    manager: Manager,
    diaryLines: List<String>,
    onPurchaseLicense: (String) -> Unit,
    onHireAgent: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("CONTABILIDAD Y LICENCIAS PERSONALES", color = GrassEmerald, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text("Tus ahorros acumulados por salarios. Úsalos para mejorar tu perfil profesional.", color = TextSecondary, fontSize = 11.sp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Capital Screen
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                border = BorderStroke(1.dp, DarkSteel),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("BILLETERA PERSONAL", color = NeonAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$${String.format("%,d", manager.personalWealth)}",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text("Separado del presupuesto del club. Úsalo para comprar licencias habilitantes y contratar representación.", color = TextSecondary, fontSize = 11.sp)
                    
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("REPRESENTACIÓN PRIVADA", color = GrassEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    if (manager.hasPrivateAgent) {
                        Text("Habilitado: Agente Privado (★".repeat(manager.agentQuality) + "☆".repeat(5 - manager.agentQuality) + ")", color = TextPrimary, fontSize = 12.sp)
                        Text("Tu agente busca automáticamente mejores ofertas en ligas de mayor categoría.", color = TextSecondary, fontSize = 11.sp)
                    } else {
                        Text("Actualmente te auto-representas.", color = TextSecondary, fontSize = 12.sp)
                        Button(
                            onClick = onHireAgent,
                            enabled = manager.personalWealth >= 8000L,
                            colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald, contentColor = PitchDarkBg),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hire_agent_btn")
                        ) {
                            Text("Contratar Agente ($8,000)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Training Licensing screen
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                border = BorderStroke(1.dp, DarkSteel),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("PROGRAMA DE LICENCIAS CONTINENTALES", color = NeonAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Tu nivel actual: ${manager.license}", color = GrassEmerald, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))

                    val licenses = listOf(
                        Pair("Licencia B", 5000L),
                        Pair("Licencia A", 15000L),
                        Pair("Licencia Pro", 40000L)
                    )

                    licenses.forEach { (lic, cost) ->
                        val isOwned = when (lic) {
                            "Licencia B" -> manager.license == "Licencia B" || manager.license == "Licencia A" || manager.license == "Licencia Pro"
                            "Licencia A" -> manager.license == "Licencia A" || manager.license == "Licencia Pro"
                            "Licencia Pro" -> manager.license == "Licencia Pro"
                            else -> false
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isOwned) GrassEmerald.copy(alpha = 0.1f) else PitchDarkBg)
                                .border(1.dp, if (isOwned) GrassEmerald else DarkSteel, RoundedCornerShape(6.dp))
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(lic, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Reputación: +${if (lic == "Licencia B") 10 else if (lic == "Licencia A") 20 else 35}", color = TextSecondary, fontSize = 9.sp)
                            }
                            
                            if (isOwned) {
                                Text("Comprado", color = GrassEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Button(
                                    onClick = { onPurchaseLicense(lic) },
                                    enabled = manager.personalWealth >= cost,
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonAmber, contentColor = PitchDarkBg),
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    modifier = Modifier
                                        .height(28.dp)
                                        .testTag("buy_lic_${lic.replace(" ", "_").lowercase()}")
                                ) {
                                    Text("Comprar $${String.format("%,d", cost)}", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Manager's Plain-text Diary
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, DarkSteel),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("DIARIO DE BITÁCORA DEL MÁNAGER (.txt)", color = GlacierBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Hitos históricos escritos directamente como texto plano para auditoría deportiva.", color = TextSecondary, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(8.dp))

                if (diaryLines.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay entradas en la bitácora aún. Juega partidos para registrar hitos.", color = TextSecondary, fontSize = 11.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(diaryLines.reversed()) { line ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PitchDarkBg)
                                    .border(1.dp, DarkSteel, RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Text(line, color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}
