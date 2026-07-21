package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.engine.GameSettings
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun CustomClubCreationDialog(
    initialClubName: String,
    countryName: String?,
    onDismissRequest: () -> Unit,
    onFundClub: (name: String, stadiumCapacity: Int, budget: Long) -> Unit
) {
    val currencySymbol by GameSettings.currencySymbol.collectAsState()
    val isAbbreviationEnabled by GameSettings.isAbbreviationEnabled.collectAsState()

    var name by remember { mutableStateOf(initialClubName) }
    var capacity by remember { mutableFloatStateOf(25000f) }
    var budgetValue by remember { mutableFloatStateOf(10_000_000f) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(2.dp, GrassEmerald),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(420.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "FUNDAR NUEVO CLUB DEPORTIVO",
                    color = GrassEmerald,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la Institución", color = GrassEmerald) },
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
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    "País afiliado: ${countryName ?: "Ninguno seleccionado"}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    Text(
                        "Capacidad del Estadio: ${capacity.toInt()} espectadores",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Slider(
                        value = capacity,
                        onValueChange = { capacity = it },
                        valueRange = 10000f..80000f,
                        colors = SliderDefaults.colors(
                            thumbColor = GrassEmerald,
                            activeTrackColor = GrassEmerald,
                            inactiveTrackColor = DarkSteel
                        )
                    )
                }

                Column {
                    Text(
                        "Presupuesto de Arranque: ${GameSettings.formatMoney(budgetValue.toLong())} USD",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Slider(
                        value = budgetValue,
                        onValueChange = { budgetValue = it },
                        valueRange = 5000000f..40000000f,
                        colors = SliderDefaults.colors(
                            thumbColor = GrassEmerald,
                            activeTrackColor = GrassEmerald,
                            inactiveTrackColor = DarkSteel
                        )
                    )
                }

                HorizontalDivider(color = DarkSteel)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            if (name.trim().isNotEmpty()) {
                                onFundClub(name.trim(), capacity.toInt(), budgetValue.toLong())
                            }
                        },
                        enabled = name.trim().isNotEmpty() && countryName != null,
                        colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald, contentColor = PitchDarkBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("FUNDAR CLUB", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
