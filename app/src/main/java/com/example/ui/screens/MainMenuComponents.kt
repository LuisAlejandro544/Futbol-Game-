package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameEngine
import com.example.engine.hasSavedGame
import com.example.model.Manager
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MainMenuScreen(
    engine: GameEngine,
    onNavigateToPhase: (SetupPhase) -> Unit,
    onContinueGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hasSavedGame = remember { engine.hasSavedGame() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDarkBg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Logo & Title
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.radialGradient(listOf(GlacierBlue.copy(alpha = 0.2f), Color.Transparent)))
                    .border(2.dp, GlacierBlue, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsFootball,
                    contentDescription = null,
                    tint = GlacierBlue,
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "FEDEBOL",
                    color = GlacierBlue,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "MANAGER",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Simulador de Gestión de Fútbol Procedural",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Menu Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Continuar Partida (Only enabled if saved game exists)
                MenuButton(
                    text = "CONTINUAR PARTIDA",
                    icon = Icons.Default.PlayArrow,
                    enabled = hasSavedGame,
                    onClick = onContinueGame,
                    accentColor = GlacierBlue,
                    testTag = "btn_continue_game"
                )

                // Nueva Partida
                MenuButton(
                    text = "NUEVA PARTIDA",
                    icon = Icons.Default.Add,
                    enabled = true,
                    onClick = { onNavigateToPhase(SetupPhase.ENTER_NAME) },
                    accentColor = GlacierBlue,
                    testTag = "btn_new_game"
                )

                // Partidas Guardadas
                MenuButton(
                    text = "PARTIDAS GUARDADAS",
                    icon = Icons.Default.Save,
                    enabled = true,
                    onClick = { onNavigateToPhase(SetupPhase.SAVED_GAMES) },
                    accentColor = NeonAmber,
                    testTag = "btn_saved_games"
                )

                // Opciones
                MenuButton(
                    text = "OPCIONES",
                    icon = Icons.Default.Settings,
                    enabled = true,
                    onClick = { onNavigateToPhase(SetupPhase.OPTIONS) },
                    accentColor = TextSecondary,
                    testTag = "btn_options"
                )

                // Ayuda
                MenuButton(
                    text = "AYUDA Y REGLAS",
                    icon = Icons.Default.HelpOutline,
                    enabled = true,
                    onClick = { onNavigateToPhase(SetupPhase.HELP_MENU) },
                    accentColor = CardGold,
                    testTag = "btn_help"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Salir
                OutlinedMenuButton(
                    text = "SALIR DEL JUEGO",
                    icon = Icons.Default.ExitToApp,
                    onClick = {
                        (context as? Activity)?.finish() ?: System.exit(0)
                    },
                    testTag = "btn_exit"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Version info or status
            Text(
                text = "v2.0.0-PRO • FEDEBOL Global Corporation",
                color = TextSecondary.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val alpha = if (enabled) 1f else 0.35f
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) SurfaceCarbon else SurfaceCarbon.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, if (enabled) accentColor.copy(alpha = 0.6f) else DarkSteel),
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor.copy(alpha = alpha),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = text,
                    color = TextPrimary.copy(alpha = alpha),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            if (enabled) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = accentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun OutlinedMenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, DarkSteel),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
