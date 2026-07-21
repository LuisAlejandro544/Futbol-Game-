package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.BackgroundMusicPlayer
import com.example.engine.MusicTrack
import com.example.engine.GameEngine
import com.example.engine.GameSettings
import com.example.engine.resetAllData
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OptionsMenuScreen(
    engine: GameEngine,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTrack by BackgroundMusicPlayer.currentTrack.collectAsState()
    val isPlaying by BackgroundMusicPlayer.isPlaying.collectAsState()
    val volume by BackgroundMusicPlayer.volume.collectAsState()
    val isMusicEnabled by BackgroundMusicPlayer.isMusicEnabled.collectAsState()

    var showModManager by remember { mutableStateOf(false) }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            BackgroundMusicPlayer.importCustomAudioTrack(context, it)
        }
    }

    val isAbbreviationEnabled by GameSettings.isAbbreviationEnabled.collectAsState()
    val currencySymbol by GameSettings.currencySymbol.collectAsState()

    // Decorative rotating disk animation state when playing
    var diskRotation by remember { mutableStateOf(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                diskRotation = (diskRotation + 2f) % 360f
                delay(16) // ~60fps rotation
            }
        }
    }

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
                .padding(vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        .testTag("options_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = GlacierBlue
                    )
                }

                Text(
                    text = "OPCIONES Y AUDIO",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            HorizontalDivider(color = DarkSteel)

            // Music Controller / Player Console Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                border = BorderStroke(1.dp, GlacierBlue.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Procedural Album Cover Art with rotating Vinyl design
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        GlacierBlue.copy(alpha = 0.8f),
                                        PitchDarkBg,
                                        GlacierBlue.copy(alpha = 0.2f),
                                        SurfaceCarbon,
                                        GlacierBlue.copy(alpha = 0.8f)
                                    )
                                )
                            )
                            .border(1.dp, DarkSteel, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Vinyl Record
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .rotate(diskRotation)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(1.dp, GlacierBlue.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Sound lines on record
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.DarkGray.copy(alpha = 0.6f), CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(55.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.DarkGray.copy(alpha = 0.6f), CircleShape)
                            )
                            // Record Center Label
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(GlacierBlue)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                        .align(Alignment.Center)
                                )
                            }
                        }

                        // Small overlays for visual flair
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = PitchDarkBg.copy(alpha = 0.7f),
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.Center)
                        )
                    }

                    // Track Details
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentTrack?.title ?: "No se está reproduciendo",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentTrack?.let { "Artista: ${it.artist}" } ?: "Música en espera",
                            color = GlacierBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = currentTrack?.let { "Duración: ${it.durationStr}" } ?: "",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // On/Off toggle row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PitchDarkBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MÚSICA EN SEGUNDO PLANO",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Switch(
                            checked = isMusicEnabled,
                            onCheckedChange = { enabled ->
                                BackgroundMusicPlayer.setMusicEnabled(enabled, context)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PitchDarkBg,
                                checkedTrackColor = GlacierBlue,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = SurfaceCarbon
                            ),
                            modifier = Modifier.scale(0.85f).testTag("music_toggle_switch")
                        )
                    }

                    // Volume Control Slider
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (volume <= 0f) Icons.Default.VolumeMute else if (volume < 0.5f) Icons.Default.VolumeDown else Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = if (isMusicEnabled) GlacierBlue else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "VOLUMEN DE MÚSICA",
                                    color = if (isMusicEnabled) TextPrimary else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "${(volume * 100).toInt()}%",
                                color = if (isMusicEnabled) GlacierBlue else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Slider(
                            value = volume,
                            onValueChange = { vol ->
                                if (isMusicEnabled) {
                                    BackgroundMusicPlayer.setVolume(vol)
                                }
                            },
                            enabled = isMusicEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = GlacierBlue,
                                activeTrackColor = GlacierBlue,
                                inactiveTrackColor = DarkSteel
                            ),
                            modifier = Modifier.testTag("volume_slider")
                        )
                    }

                    // Player Controls (Prev, Play/Pause, Next)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Track Button
                        IconButton(
                            onClick = { if (isMusicEnabled) BackgroundMusicPlayer.playPrevious(context) },
                            enabled = isMusicEnabled,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Anterior",
                                tint = if (isMusicEnabled) TextPrimary else TextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Play / Pause Button
                        IconButton(
                            onClick = {
                                if (isMusicEnabled) {
                                    if (isPlaying) {
                                        BackgroundMusicPlayer.pause()
                                    } else {
                                        BackgroundMusicPlayer.resume(context)
                                    }
                                }
                            },
                            enabled = isMusicEnabled,
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (isMusicEnabled) GlacierBlue else DarkSteel,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                                tint = PitchDarkBg,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Next Track Button
                        IconButton(
                            onClick = { if (isMusicEnabled) BackgroundMusicPlayer.playNext(context) },
                            enabled = isMusicEnabled,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Siguiente",
                                tint = if (isMusicEnabled) TextPrimary else TextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Playlist Selection Area
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BANDA SONORA Y MÚSICA FIFA",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Button(
                        onClick = { audioPickerLauncher.launch("audio/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCarbon),
                        border = BorderStroke(1.dp, GlacierBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FileOpen, contentDescription = null, tint = GlacierBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("IMPORTAR MÚSICA", color = GlacierBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                BackgroundMusicPlayer.tracks.forEach { track ->
                    val isCurrent = currentTrack?.id == track.id
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) GlacierBlue.copy(alpha = 0.08f) else SurfaceCarbon
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isCurrent) GlacierBlue else DarkSteel
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isMusicEnabled) {
                                BackgroundMusicPlayer.playTrack(context, track)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isCurrent) GlacierBlue else DarkSteel),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isCurrent && isPlaying) Icons.Default.VolumeUp else Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = if (isCurrent) PitchDarkBg else TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = track.title,
                                        color = if (isCurrent) GlacierBlue else TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = track.artist,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isCurrent && isPlaying) {
                                    Badge(
                                        containerColor = GlacierBlue.copy(alpha = 0.15f),
                                        contentColor = GlacierBlue
                                    ) {
                                        Text(
                                            text = "SONANDO",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = track.durationStr,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (track.isCustom) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { BackgroundMusicPlayer.deleteCustomTrack(context, track) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar pista", tint = StatusRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // MOD MANAGER CARD BUTTON
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                border = BorderStroke(1.dp, NeonAmber),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showModManager = true }
                    .testTag("open_mod_manager_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(20.dp))
                            Text(
                                text = "🛠️ MOTOR DE MODS Y PERSONALIZACIÓN",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Activa mods de velocidad de partido, frecuencia de goles, interfaces neón, comentarios clásicos y plantillas JSON externas.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = NeonAmber)
                }
            }

            // General Game Audio Configuration Card (Whistles and game sounds)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                border = BorderStroke(1.dp, DarkSteel),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SONIDOS DE PARTIDO Y ALERTAS",
                        color = GlacierBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Los sonidos del motor de partido (silbatos del árbitro al inicio, entretiempo y final, y beeps de goles) se reproducen usando SoundPool de baja latencia.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Financial Configuration Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                border = BorderStroke(1.dp, GlacierBlue.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "CONFIGURACIÓN FINANCIERA",
                        color = GlacierBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Abbreviation Toggle row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PitchDarkBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = "ABREVIAR CIFRAS FINANCIERAS",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Muestra presupuestos y salarios cortos (Ej: $currencySymbol 1.5M en vez de $currencySymbol 1,500,000)",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                lineHeight = 11.sp
                            )
                        }
                        Switch(
                            checked = isAbbreviationEnabled,
                            onCheckedChange = { enabled ->
                                GameSettings.setAbbreviationEnabled(context, enabled)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PitchDarkBg,
                                checkedTrackColor = GlacierBlue,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = SurfaceCarbon
                            ),
                            modifier = Modifier.scale(0.85f).testTag("abbreviation_toggle_switch")
                        )
                    }

                    // Currency symbol selector row
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PitchDarkBg, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "DIVISA DE OPERACIÓN",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Selecciona el símbolo monetario que se utilizará para salarios, presupuestos y taquillas.",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            lineHeight = 11.sp
                        )

                        val currencies = listOf("$", "€", "£", "AR$", "MX$")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            currencies.forEach { curr ->
                                val isSelected = currencySymbol == curr
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) GlacierBlue else SurfaceCarbon)
                                        .border(1.dp, if (isSelected) GlacierBlue else DarkSteel, RoundedCornerShape(6.dp))
                                        .clickable { GameSettings.setCurrencySymbol(context, curr) }
                                        .testTag("currency_option_$curr"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = curr,
                                        color = if (isSelected) PitchDarkBg else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Data Reset Card
            var showResetConfirmation by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "RESTABLECIMIENTO TOTAL DE DATOS",
                        color = StatusRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Atención: Esta acción eliminará permanentemente todo tu progreso de carrera deportiva, clubes fundados, ligas simuladas, diario de mánager, y reestablecerá el universo por completo. No se puede deshacer.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Button(
                        onClick = { showResetConfirmation = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusRed.copy(alpha = 0.15f),
                            contentColor = StatusRed
                        ),
                        border = BorderStroke(1.dp, StatusRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("total_reset_btn")
                    ) {
                        Text(
                            text = "BORRAR TODO Y EMPEZAR DE CERO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            if (showResetConfirmation) {
                AlertDialog(
                    onDismissRequest = { showResetConfirmation = false },
                    containerColor = SurfaceCarbon,
                    titleContentColor = StatusRed,
                    textContentColor = TextPrimary,
                    title = {
                        Text(
                            text = "⚠️ ¿CONFIRMAS EL BORRADO TOTAL?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    text = {
                        Text(
                            text = "Esta acción es irreversible y borrará todos los archivos de guardado. Volverás a la pantalla de fundación o selección de club.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    showResetConfirmation = false
                                    engine.resetAllData()
                                    onBack()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusRed, contentColor = Color.White),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("confirm_reset_btn")
                        ) {
                            Text("SÍ, BORRAR TODO", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showResetConfirmation = false },
                            modifier = Modifier.testTag("dismiss_reset_btn")
                        ) {
                            Text("CANCELAR", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back button
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GlacierBlue,
                    contentColor = PitchDarkBg
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "REGRESAR AL MENÚ PRINCIPAL",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        if (showModManager) {
            ModManagerScreen(onBack = { showModManager = false })
        }
    }
}
