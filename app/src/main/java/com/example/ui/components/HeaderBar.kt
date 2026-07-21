package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.engine.GameSettings
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Club
import com.example.model.Manager
import com.example.ui.screens.formatLocalDateToSpanish
import com.example.ui.theme.*
import java.time.LocalDate

@Composable
fun HeaderBar(
    manager: Manager,
    club: Club?,
    isSimulating: Boolean,
    currentDate: LocalDate,
    onSimulateClick: () -> Unit
) {
    val currencySymbol by GameSettings.currencySymbol.collectAsState()
    val isAbbreviationEnabled by GameSettings.isAbbreviationEnabled.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
        border = BorderStroke(1.dp, DarkSteel),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Manager details (Left)
            Column(
                modifier = Modifier.weight(1.1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "MÁNAGER: ${manager.name.uppercase()}",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Licencia: ${manager.license} | Reputación: ${manager.reputation}/100",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 2. Club details (Middle)
            if (club != null) {
                Column(
                    modifier = Modifier.weight(1.0f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = club.name.uppercase(),
                        color = GlacierBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Balance: ${GameSettings.formatMoney(club.budget)}",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1.0f))
            }

            // 3. Date & Button section (Right)
            Row(
                modifier = Modifier.weight(1.4f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "FECHA ACTUAL",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                    Text(
                        text = formatLocalDateToSpanish(currentDate).uppercase(),
                        color = GlacierBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = onSimulateClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GlacierBlue, contentColor = PitchDarkBg),
                    enabled = !isSimulating,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .testTag("simulate_button")
                        .height(38.dp)
                ) {
                    if (isSimulating) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = PitchDarkBg, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Calculando...", fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Siguiente Jornada", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SIMULAR JORNADA", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}
