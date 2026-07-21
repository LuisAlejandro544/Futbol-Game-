package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun EnterNameScreen(
    inputName: String,
    onValueChange: (String) -> Unit,
    isSimulating: Boolean,
    onInitializeUniverse: (useFictionalNames: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var useFictionalNames by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDarkBg)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, GrassEmerald),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .widthIn(max = 500.dp)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SportsFootball,
                    contentDescription = null,
                    tint = GrassEmerald,
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = "FEDEBOL MANAGER",
                    color = GrassEmerald,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Simulador de gestión de fútbol latinoamericano y mundial procedural hiperrealista.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(color = DarkSteel)

                OutlinedTextField(
                    value = inputName,
                    onValueChange = onValueChange,
                    label = { Text("Nombre del Mánager", color = GrassEmerald) },
                    textStyle = TextStyle(color = TextPrimary, fontSize = 16.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GrassEmerald,
                        unfocusedBorderColor = DarkSteel,
                        focusedLabelColor = GrassEmerald,
                        unfocusedLabelColor = TextSecondary,
                        cursorColor = GrassEmerald
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manager_name_input")
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Selector de modo de nombres
                Text(
                    text = "Base de Datos de Clubes",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Option 1: Inspired by reality
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!useFictionalNames) GrassEmerald.copy(alpha = 0.15f) else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (!useFictionalNames) GrassEmerald else DarkSteel,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { useFictionalNames = false }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = !useFictionalNames,
                                onClick = { useFictionalNames = false },
                                colors = RadioButtonDefaults.colors(selectedColor = GrassEmerald, unselectedColor = TextSecondary)
                            )
                            Column {
                                Text(
                                    text = "Inspirados en la Realidad",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Nombres nostálgicos inspirados en el fútbol real (ej: Boca de Buenos Aires, River de Núñez)",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Option 2: Purely Fictional / Procedural
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (useFictionalNames) GrassEmerald.copy(alpha = 0.15f) else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (useFictionalNames) GrassEmerald else DarkSteel,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { useFictionalNames = true }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = useFictionalNames,
                                onClick = { useFictionalNames = true },
                                colors = RadioButtonDefaults.colors(selectedColor = GrassEmerald, unselectedColor = TextSecondary)
                            )
                            Column {
                                Text(
                                    text = "100% Procedural / Ficticios",
                                    color = GrassEmerald,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Clubes con nombres totalmente aleatorios y de fantasía regional. ¡Opción segura anti-demandas!",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { onInitializeUniverse(useFictionalNames) },
                    enabled = inputName.trim().isNotEmpty() && !isSimulating,
                    colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald, contentColor = PitchDarkBg),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("init_universe_btn")
                ) {
                    if (isSimulating) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PitchDarkBg)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CREANDO MATRICES...", fontWeight = FontWeight.Bold, color = PitchDarkBg)
                    } else {
                        Text("CREAR UNIVERSO PROCEDURAL", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerDetailsDialog(
    player: Player,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, NeonAmber),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(400.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        player.fullName,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "RAT: ${player.getOverallRating()}",
                        color = NeonAmber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    "Posición: ${player.position.name} • Edad: ${player.age} años",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                HorizontalDivider(color = DarkSteel)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ataque", color = TextSecondary, fontSize = 12.sp)
                        Text(player.attributes.attack.toString(), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Defensa", color = TextSecondary, fontSize = 12.sp)
                        Text(player.attributes.defense.toString(), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Mediocampo", color = TextSecondary, fontSize = 12.sp)
                        Text(player.attributes.midfield.toString(), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Físico", color = TextSecondary, fontSize = 12.sp)
                        Text(player.attributes.physical.toString(), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Velocidad", color = TextSecondary, fontSize = 12.sp)
                        Text(player.attributes.speed.toString(), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Arquero", color = TextSecondary, fontSize = 12.sp)
                        Text(player.attributes.goalkeeper.toString(), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = DarkSteel)
                Text("Fidelidad y Psicología:", color = GlacierBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Lealtad Profesional", color = TextSecondary, fontSize = 11.sp)
                        Text("${player.loyalty}%", color = if (player.loyalty >= 75) GrassEmerald else if (player.loyalty >= 40) StatusAmber else StatusRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Aprecio al Club", color = TextSecondary, fontSize = 11.sp)
                        Text("${player.clubAppreciation}%", color = GlacierBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (player.traits.isNotEmpty()) {
                    HorizontalDivider(color = DarkSteel)
                    Text("Rasgos Especiales:", color = GrassEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    player.traits.forEach { trait ->
                        Text("✨ ${trait.displayName}: ${trait.description}", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cerrar", color = GrassEmerald)
                    }
                }
            }
        }
    }
}
