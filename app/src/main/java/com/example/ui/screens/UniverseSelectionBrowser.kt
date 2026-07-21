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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.engine.GameSettings
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.theme.*

@Composable
fun UniverseSelectionBrowser(
    manager: Manager,
    countries: List<Country>,
    clubs: List<Club>,
    selectedCountry: Country?,
    onSelectCountry: (Country) -> Unit,
    selectedClub: Club?,
    onSelectClub: (Club) -> Unit,
    isSimulating: Boolean,
    onFundClubClick: () -> Unit,
    onChooseClubAndStart: () -> Unit,
    onSelectPlayerForDetail: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencySymbol by GameSettings.currencySymbol.collectAsState()
    val isAbbreviationEnabled by GameSettings.isAbbreviationEnabled.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "SELECCIÓN DE PROYECTO DEPORTIVO",
                        color = GrassEmerald,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        "Mánager: ${manager.name} • Elige una institución para dirigir o funda un club nuevo",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onFundClubClick,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber, contentColor = PitchDarkBg),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("FUNDAR CLUB DESDE CERO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onChooseClubAndStart,
                        enabled = selectedClub != null && !isSimulating,
                        colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald, contentColor = PitchDarkBg),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        if (isSimulating) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PitchDarkBg)
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ELEGIR ESTE CLUB Y EMPEZAR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            HorizontalDivider(color = DarkSteel)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // COLUMN 1: Countries
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                    border = BorderStroke(1.dp, DarkSteel),
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            "1. PAÍSES DISPONIBLES",
                            color = GrassEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(countries, key = { it.name }) { country ->
                                val isSelected = selectedCountry?.name == country.name
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) DarkSteel else Color.Transparent)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) GrassEmerald else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { onSelectCountry(country) }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        country.name,
                                        color = if (isSelected) GrassEmerald else TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Prestigio: ${country.selectionPower}",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // COLUMN 2: Clubs
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                    border = BorderStroke(1.dp, DarkSteel),
                    modifier = Modifier
                        .weight(1.8f)
                        .fillMaxHeight()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            "2. CLUBES EN LA LIGA",
                            color = GrassEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        selectedCountry?.let { country ->
                            val countryClubs = clubs.filter { it.country == country.name }
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(countryClubs, key = { it.id }) { club ->
                                    val isSelected = selectedClub?.id == club.id
                                    val ratings = club.getTeamRatings()
                                    
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) DarkSteel else Color.Transparent)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) NeonAmber else DarkSteel,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onSelectClub(club) }
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                club.name,
                                                color = if (isSelected) NeonAmber else TextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                "Presupuesto: ${GameSettings.formatMoney(club.budget)}",
                                                color = TextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text("ATA: ${ratings.third}", color = PositionATTColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("MED: ${ratings.second}", color = PositionMIDColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("DEF: ${ratings.first}", color = PositionDEFColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text("🏟️ ${club.stadiumCapacity} cap", color = TextSecondary, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        } ?: Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Selecciona un país para ver sus ligas", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                // COLUMN 3: Squad & Player Details
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                    border = BorderStroke(1.dp, DarkSteel),
                    modifier = Modifier
                        .weight(2.0f)
                        .fillMaxHeight()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            "3. PLANTILLA Y ESTADÍSTICAS",
                            color = GrassEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        selectedClub?.let { club ->
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(club.squad, key = { it.fullName }) { player ->
                                    val posColor = when (player.position) {
                                        Position.GK -> PositionGKColor
                                        Position.DEF -> PositionDEFColor
                                        Position.MID -> PositionMIDColor
                                        Position.ATT -> PositionATTColor
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(DarkSteelCard)
                                            .clickable { onSelectPlayerForDetail(player) }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 36.dp, height = 20.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(posColor.copy(alpha = 0.15f))
                                                .border(1.dp, posColor, RoundedCornerShape(4.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                player.position.name,
                                                color = posColor,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Text(
                                            player.fullName,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Text(
                                            "EDAD: ${player.age}",
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )

                                        Text(
                                            "VAL: ${player.getOverallRating()}",
                                            color = GrassEmerald,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        } ?: Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Selecciona un club para auditar su plantel", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
