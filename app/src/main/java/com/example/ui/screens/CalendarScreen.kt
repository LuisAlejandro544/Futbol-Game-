package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Club
import com.example.model.League
import com.example.model.Manager
import com.example.ui.theme.*
import java.time.LocalDate

@Composable
fun CalendarScreen(
    currentDate: LocalDate,
    manager: Manager,
    ligas: List<League>,
    clubs: List<Club>
) {
    var selectedViewType by remember { mutableStateOf("mine") } // "mine", "league", "intl"
    
    // Find manager's active league
    val userClubId = manager.currentClubId
    val userLeague = ligas.firstOrNull { it.clubs.any { c -> c.id == userClubId } }
    
    // Selected club state for inspection
    var selectedClubForCalendar by remember(userClubId) { mutableStateOf(clubs.firstOrNull { it.id == userClubId }) }
    var selectedLeagueForCalendar by remember(userLeague) { mutableStateOf(userLeague) }
    
    // Filter/populate selected club lists depending on type
    val displayedMatches = remember(selectedClubForCalendar, selectedLeagueForCalendar) {
        if (selectedClubForCalendar == null || selectedLeagueForCalendar == null) emptyList()
        else {
            selectedLeagueForCalendar!!.fixtures.flatMap { it.matches }.filter {
                it.homeClubId == selectedClubForCalendar!!.id || it.awayClubId == selectedClubForCalendar!!.id
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Card with Dynamic Date Status (Modularized)
        item {
            CalendarTopDateCard(currentDate = currentDate)
        }

        // Tab Row for Selection Category (Modularized)
        item {
            CalendarViewTypeTabs(
                selectedViewType = selectedViewType,
                onViewTypeSelected = { type ->
                    selectedViewType = type
                    when (type) {
                        "mine" -> {
                            selectedClubForCalendar = clubs.firstOrNull { it.id == userClubId }
                            selectedLeagueForCalendar = userLeague
                        }
                        "league" -> {
                            val otherClub = userLeague?.clubs?.firstOrNull { it.id != userClubId }
                            selectedClubForCalendar = otherClub
                            selectedLeagueForCalendar = userLeague
                        }
                        "intl" -> {
                            val intlLeague = ligas.firstOrNull { it.id != userLeague?.id }
                            val intlClub = intlLeague?.clubs?.firstOrNull()
                            selectedClubForCalendar = intlClub
                            selectedLeagueForCalendar = intlLeague
                        }
                    }
                }
            )
        }

        // Sub-selector menus for fine-grained club selection (Modularized)
        if (selectedViewType == "league" && userLeague != null) {
            item {
                LeagueRivalSelector(
                    userLeague = userLeague,
                    selectedClubForCalendar = selectedClubForCalendar,
                    onClubSelected = { club ->
                        selectedClubForCalendar = club
                    }
                )
            }
        } else if (selectedViewType == "intl") {
            item {
                IntlLeagueAndClubSelector(
                    ligas = ligas,
                    selectedLeagueForCalendar = selectedLeagueForCalendar,
                    selectedClubForCalendar = selectedClubForCalendar,
                    onLeagueSelected = { league ->
                        selectedLeagueForCalendar = league
                        selectedClubForCalendar = league.clubs.firstOrNull()
                    },
                    onClubSelected = { club ->
                        selectedClubForCalendar = club
                    }
                )
            }
        }

        // Timeline Schedule Output Title
        item {
            Text(
                text = "FIXTURE DE COMPETICIÓN: ${selectedClubForCalendar?.name?.uppercase() ?: "SIN SELECCIÓN"}",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // List of Matches
        if (displayedMatches.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay partidos programados para este club.", color = TextSecondary)
                }
            }
        } else {
            val userLgCurrentRound = selectedLeagueForCalendar?.currentRound ?: 0
            
            items(count = displayedMatches.size, key = { index -> displayedMatches[index].id }) { index ->
                val match = displayedMatches[index]
                val roundNumber = index + 1
                val matchDate = getDateForRound(roundNumber)
                val isNextMatch = roundNumber == userLgCurrentRound + 1
                
                CalendarMatchRow(
                    match = match,
                    roundNumber = roundNumber,
                    matchDate = matchDate,
                    isNextMatch = isNextMatch,
                    selectedClubForCalendar = selectedClubForCalendar
                )
            }
        }
    }
}
