package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameEngine
import com.example.engine.getSavedManagerSync
import com.example.ui.theme.*

@Composable
fun SavedGamesMenuScreen(
    engine: GameEngine,
    onBack: () -> Unit,
    onLoadGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savedManager = remember { engine.getSavedManagerSync() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDarkBg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxHeight()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceCarbon, RoundedCornerShape(10.dp))
                        .testTag("saved_games_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = GlacierBlue
                    )
                }

                Text(
                    text = "PARTIDAS GUARDADAS",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            HorizontalDivider(color = DarkSteel)

            if (savedManager != null) {
                // Display the saved game slot
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                    border = BorderStroke(1.dp, GlacierBlue.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBox,
                                    contentDescription = null,
                                    tint = GlacierBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = savedManager.name,
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Badge(
                                containerColor = GlacierBlue.copy(alpha = 0.2f),
                                contentColor = GlacierBlue
                            ) {
                                Text("SLOT 1", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
                            }
                        }

                        HorizontalDivider(color = DarkSteel)

                        // Saved game info fields
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Reputación", color = TextSecondary, fontSize = 11.sp)
                                Text("${savedManager.reputation} / 100", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Fondos Personales", color = TextSecondary, fontSize = 11.sp)
                                Text("${savedManager.personalWealth} monedas", color = CardGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onLoadGame,
                            colors = ButtonDefaults.buttonColors(containerColor = GlacierBlue, contentColor = PitchDarkBg),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_load_selected_game")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("REANUDAR CARRERA", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No hay partidas guardadas",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
