package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.engine.GameMod
import com.example.engine.ModCategory
import com.example.engine.ModEngine
import com.example.ui.theme.*

@Composable
fun ModManagerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val installedMods by ModEngine.installedMods.collectAsState()
    val activeSpeed by ModEngine.activeMatchSpeedMultiplier.collectAsState()
    val activeGoalMult by ModEngine.activeGoalMultiplier.collectAsState()

    var selectedFilterCategory by remember { mutableStateOf<ModCategory?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newModName by remember { mutableStateOf("") }
    var newModDesc by remember { mutableStateOf("") }
    var newModCategory by remember { mutableStateOf(ModCategory.GAMEPLAY) }

    val filteredMods = if (selectedFilterCategory == null) {
        installedMods
    } else {
        installedMods.filter { it.category == selectedFilterCategory }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDarkBg)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .background(SurfaceCarbon, RoundedCornerShape(10.dp))
                            .testTag("mod_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = GlacierBlue
                        )
                    }

                    Column {
                        Text(
                            text = "GESTOR DE MODS Y PERSONALIZACIÓN",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Modifica la jugabilidad, añade interfaces y carga contenidos personalizados",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CREAR MOD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Engine Status Bar
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                border = BorderStroke(1.dp, GlacierBlue.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Motor Modding Active: ${installedMods.count { it.isEnabled }} Activos de ${installedMods.size}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GlacierBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("⚡ Velocidad: ${activeSpeed}x", color = GlacierBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonAmber.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("⚽ Furia Gol: ${String.format("%.0f", activeGoalMult * 100)}%", color = NeonAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Category filter row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChipCategory(
                    label = "TODOS",
                    isSelected = selectedFilterCategory == null,
                    onClick = { selectedFilterCategory = null },
                    modifier = Modifier.weight(1f)
                )
                FilterChipCategory(
                    label = "JUGABILIDAD",
                    isSelected = selectedFilterCategory == ModCategory.GAMEPLAY,
                    onClick = { selectedFilterCategory = ModCategory.GAMEPLAY },
                    modifier = Modifier.weight(1f)
                )
                FilterChipCategory(
                    label = "PLANTILLAS/JSON",
                    isSelected = selectedFilterCategory == ModCategory.ROSTER,
                    onClick = { selectedFilterCategory = ModCategory.ROSTER },
                    modifier = Modifier.weight(1f)
                )
                FilterChipCategory(
                    label = "INTERFAZ",
                    isSelected = selectedFilterCategory == ModCategory.INTERFACE_THEME,
                    onClick = { selectedFilterCategory = ModCategory.INTERFACE_THEME },
                    modifier = Modifier.weight(1f)
                )
            }

            // Installed Mods List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMods, key = { it.id }) { mod ->
                    ModCardItem(
                        mod = mod,
                        onToggle = { ModEngine.toggleMod(mod.id) }
                    )
                }
            }
        }

        // Dialog for creating a custom user mod definition
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                containerColor = SurfaceCarbon,
                title = {
                    Text(
                        text = "🛠️ CREAR Y CARGAR NUEVO MOD",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newModName,
                            onValueChange = { newModName = it },
                            label = { Text("Nombre del Mod") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GlacierBlue),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newModDesc,
                            onValueChange = { newModDesc = it },
                            label = { Text("Descripción de cambios") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GlacierBlue),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Categoría:", color = TextSecondary, fontSize = 12.sp)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ModCategory.values().forEach { cat ->
                                val selected = newModCategory == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selected) GlacierBlue else PitchDarkBg)
                                        .clickable { newModCategory = cat }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat.name.take(4),
                                        color = if (selected) PitchDarkBg else TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newModName.isNotBlank()) {
                                ModEngine.installCustomMod(
                                    GameMod(
                                        name = newModName,
                                        author = "Usuario Creador",
                                        description = newModDesc.ifBlank { "Mod personalizado creado por el usuario." },
                                        category = newModCategory,
                                        isEnabled = true
                                    )
                                )
                                showCreateDialog = false
                                newModName = ""
                                newModDesc = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald)
                    ) {
                        Text("INSTALAR Y ACTIVAR")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("CANCELAR", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun FilterChipCategory(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) GlacierBlue else SurfaceCarbon)
            .border(1.dp, if (isSelected) GlacierBlue else DarkSteel, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) PitchDarkBg else TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ModCardItem(
    mod: GameMod,
    onToggle: () -> Unit
) {
    val categoryColor = when (mod.category) {
        ModCategory.GAMEPLAY -> NeonAmber
        ModCategory.ROSTER -> GrassEmerald
        ModCategory.INTERFACE_THEME -> GlacierBlue
        ModCategory.COMMENTARY -> StatusTeal
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (mod.isEnabled) SurfaceCarbon else PitchDarkBg.copy(alpha = 0.7f)
        ),
        border = BorderStroke(
            1.dp,
            if (mod.isEnabled) categoryColor.copy(alpha = 0.6f) else DarkSteel
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(categoryColor.copy(alpha = 0.15f))
                            .border(1.dp, categoryColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = mod.category.name,
                            color = categoryColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = mod.name,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mod.description,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Autor: ${mod.author}",
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = mod.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PitchDarkBg,
                    checkedTrackColor = categoryColor,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = DarkSteel
                )
            )
        }
    }
}
