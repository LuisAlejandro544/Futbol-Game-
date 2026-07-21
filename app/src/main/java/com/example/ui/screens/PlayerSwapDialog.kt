package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Club
import com.example.model.Player
import com.example.ui.theme.*

@Composable
fun PlayerSwapDialog(
    club: Club,
    selectedPlayer: Player,
    onDismiss: () -> Unit,
    onSwapPlayers: (String, String) -> Unit
) {
    val targets = if (selectedPlayer.isStarter) {
        club.squad.filter { !it.isStarter }
    } else {
        club.squad.filter { it.isStarter }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (selectedPlayer.isStarter) "Mandar a la Banca" else "Subir a Titular",
                color = NeonAmber,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Selecciona con qué jugador intercambiar a ${selectedPlayer.fullName} (${selectedPlayer.position}):",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.heightIn(max = 240.dp)
                ) {
                    items(targets, key = { it.id }) { targetPlayer ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                                .background(PitchDarkBg)
                                .border(1.dp, if (targetPlayer.position == selectedPlayer.position) GrassEmerald.copy(alpha = 0.5f) else DarkSteel)
                                .clickable {
                                    onSwapPlayers(
                                        if (selectedPlayer.isStarter) selectedPlayer.id else targetPlayer.id,
                                        if (selectedPlayer.isStarter) targetPlayer.id else selectedPlayer.id
                                    )
                                    onDismiss()
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(targetPlayer.fullName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Pos: ${targetPlayer.position} | OVR: ${targetPlayer.getOverallRating()} | En: ${targetPlayer.energy}%", color = TextSecondary, fontSize = 10.sp)
                            }
                            if (targetPlayer.position == selectedPlayer.position) {
                                Text("Misma Pos.", color = GrassEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                         }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        },
        containerColor = SurfaceCarbon,
        textContentColor = TextPrimary
    )
}
