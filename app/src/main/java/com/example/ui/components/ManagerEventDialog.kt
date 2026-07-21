package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Warning
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
import com.example.model.ManagerEvent
import com.example.ui.theme.*

@Composable
fun ManagerEventDialog(
    event: ManagerEvent,
    onOptionSelected: (Int) -> Unit
) {
    Dialog(
        onDismissRequest = { /* Force decision, cannot dismiss without choosing */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("manager_event_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(2.dp, NeonAmber)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Warning / Event Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerta",
                        tint = NeonAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "EVENTO DE GESTIÓN DEPORTIVA",
                        color = NeonAmber,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                HorizontalDivider(color = DarkSteel, modifier = Modifier.padding(bottom = 16.dp))

                // Event Title & Affected Player Badge
                Text(
                    text = event.title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSteel)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AssignmentInd,
                        contentDescription = null,
                        tint = GrassEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Involucrado: ${event.affectedPlayerName}",
                        color = GrassEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Narrative description
                Text(
                    text = event.description,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PitchDarkBg)
                        .border(1.dp, DarkSteel, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "¿CÓMO VAS A REACCIONAR, MÁNAGER?",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Decision Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    event.options.forEachIndexed { index, option ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceCarbon.copy(alpha = 0.5f))
                                .border(1.dp, GrassEmerald, RoundedCornerShape(10.dp))
                                .clickable { onOptionSelected(index) }
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = option.text,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = option.feedback,
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = option.effectDescription,
                                        color = GlacierBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
