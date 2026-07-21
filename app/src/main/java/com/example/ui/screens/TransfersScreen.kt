package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.GameEngine
import com.example.engine.TransferNegotiationResult
import com.example.model.Club
import com.example.model.Player
import com.example.model.Position
import com.example.ui.components.ProceduralPlayerFace
import com.example.ui.theme.*

@Composable
fun TransfersScreen(
    engine: GameEngine,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clubs by engine.clubs.collectAsState()
    val manager by engine.manager.collectAsState()
    val freeAgentsAndTalents by engine.freeAgentsAndTalents.collectAsState()

    val userClub = remember(clubs, manager) {
        clubs.firstOrNull { it.id == manager.currentClubId }
    }

    var selectedTab by remember { mutableStateOf(0) } // 0 = Agentes Libres y Cantera, 1 = Todos los Clubes
    var searchQuery by remember { mutableStateOf("") }
    var selectedPositionFilter by remember { mutableStateOf<Position?>(null) }
    var playerToNegotiate by remember { mutableStateOf<Pair<Player, Club?>?>(null) } // Player & owning Club (null if free agent)

    // Gather all players from other clubs
    val allClubPlayersWithClub = remember(clubs, manager) {
        clubs.filter { it.id != manager.currentClubId }.flatMap { club ->
            club.squad.map { player -> Pair(player, club) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDarkBg)
            .padding(12.dp)
    ) {
        // Top Header Title & User Club Budget Summary
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(1.dp, DarkSteel),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Mercado",
                        tint = GrassEmerald,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "MERCADO DE FICHAJES Y CANTERA",
                            color = GrassEmerald,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Negocia contratos, agentes libres y promesas juveniles continentales",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                if (userClub != null) {
                    Surface(
                        color = PitchDarkBg,
                        border = BorderStroke(1.dp, GrassEmerald.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Presupuesto: $${userClub.budget}",
                                color = GrassEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Masa Salarial: $${userClub.wageBudget}/sem",
                                color = NeonAmber,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Sub-navigation Category Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabTitles = listOf("🏛️ AGENTES LIBRES Y CANTERA", "⚽ JUGADORES DE OTROS CLUBES")
            tabTitles.forEachIndexed { index, title ->
                val isSel = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) GrassEmerald else SurfaceCarbon)
                        .border(1.dp, if (isSel) GrassEmerald else DarkSteel, RoundedCornerShape(8.dp))
                        .clickable { selectedTab = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSel) PitchDarkBg else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Search & Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por nombre...", fontSize = 12.sp, color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GrassEmerald,
                    unfocusedBorderColor = DarkSteel,
                    focusedContainerColor = PitchDarkBg,
                    unfocusedContainerColor = PitchDarkBg,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Position Filters
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                item {
                    FilterChip(
                        selected = selectedPositionFilter == null,
                        onClick = { selectedPositionFilter = null },
                        label = { Text("TODOS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonAmber,
                            selectedLabelColor = PitchDarkBg,
                            containerColor = SurfaceCarbon,
                            labelColor = TextPrimary
                        )
                    )
                }
                items(Position.values()) { pos ->
                    FilterChip(
                        selected = selectedPositionFilter == pos,
                        onClick = { selectedPositionFilter = if (selectedPositionFilter == pos) null else pos },
                        label = { Text(pos.name, fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GrassEmerald,
                            selectedLabelColor = PitchDarkBg,
                            containerColor = SurfaceCarbon,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
        }

        // Main Player List
        Box(modifier = Modifier.weight(1f)) {
            val filteredPlayers: List<Pair<Player, Club?>> = remember(selectedTab, searchQuery, selectedPositionFilter, freeAgentsAndTalents, allClubPlayersWithClub) {
                val rawList = if (selectedTab == 0) {
                    freeAgentsAndTalents.map { Pair(it, null) }
                } else {
                    allClubPlayersWithClub
                }

                rawList.filter { (player, _) ->
                    val matchesName = searchQuery.isEmpty() || player.fullName.contains(searchQuery, ignoreCase = true)
                    val matchesPos = selectedPositionFilter == null || player.position == selectedPositionFilter
                    matchesName && matchesPos
                }
            }

            if (filteredPlayers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron jugadores disponibles en el mercado con los filtros seleccionados.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredPlayers, key = { it.first.id }) { (player, owningClub) ->
                        TransferPlayerCard(
                            player = player,
                            owningClub = owningClub,
                            onNegotiateClick = { playerToNegotiate = Pair(player, owningClub) }
                        )
                    }
                }
            }
        }
    }

    // Transfer Negotiation Modal Dialog
    if (playerToNegotiate != null) {
        val (p, owningClub) = playerToNegotiate!!
        TransferNegotiationDialog(
            player = p,
            owningClub = owningClub,
            userClub = userClub,
            onDismiss = { playerToNegotiate = null },
            onConfirmSigning = { fee, salary, years ->
                val success = engine.executeTransferSigning(
                    player = p,
                    owningClub = owningClub,
                    transferFee = fee,
                    agreedSalary = salary,
                    agreedYears = years
                )
                if (success) {
                    Toast.makeText(context, "¡FICHAJE COMPLETADO! ${p.fullName} se unió al equipo.", Toast.LENGTH_LONG).show()
                    playerToNegotiate = null
                } else {
                    Toast.makeText(context, "No tienes presupuesto suficiente para este fichaje.", Toast.LENGTH_SHORT).show()
                }
            },
            engine = engine
        )
    }
}

@Composable
fun TransferPlayerCard(
    player: Player,
    owningClub: Club?,
    onNegotiateClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
        border = BorderStroke(1.dp, if (player.isYouthTalent) NeonAmber.copy(alpha = 0.8f) else DarkSteel),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProceduralPlayerFace(player = player, size = 44.dp)

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val posBg = when (player.position) {
                            Position.GK -> PositionOrangeGK
                            Position.DEF -> StatusBlue
                            Position.MID -> StatusTeal
                            Position.ATT -> StatusRed
                        }
                        Text(
                            text = player.position.name,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(posBg)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )

                        Text(
                            text = player.fullName,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (player.isYouthTalent) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonAmber.copy(alpha = 0.2f))
                                    .border(1.dp, NeonAmber, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("💎 JOYA CANTERA", color = NeonAmber, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Edad: ${player.age} años | Nacionalidad: ${player.country} | ${owningClub?.name ?: "Agente Libre"}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Valor: $${player.marketValue}",
                            color = GrassEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Salario: $${player.salary}/sem",
                            color = GlacierBlue,
                            fontSize = 11.sp
                        )
                        if (player.isYouthTalent) {
                            Text(
                                text = "✨ Potencial: ${player.potentialRating}",
                                color = NeonAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    if (player.notableAchievements.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            player.notableAchievements.take(2).forEach { ach ->
                                Text(
                                    text = ach,
                                    color = TextSecondary,
                                    fontSize = 9.5.sp,
                                    modifier = Modifier
                                        .background(PitchDarkBg, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${player.getOverallRating()} OVR",
                    color = if (player.getOverallRating() >= 80) NeonAmber else GrassEmerald,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onNegotiateClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald, contentColor = PitchDarkBg),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("✍️ NEGOCIAR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TransferNegotiationDialog(
    player: Player,
    owningClub: Club?,
    userClub: Club?,
    onDismiss: () -> Unit,
    onConfirmSigning: (Long, Long, Int) -> Unit,
    engine: GameEngine
) {
    var feeOffer by remember { mutableStateOf(player.marketValue) }
    var salaryOffer by remember { mutableStateOf(player.salary) }
    var contractYearsOffer by remember { mutableStateOf(3) }
    var clubAccepted by remember { mutableStateOf(owningClub == null) } // True if free agent
    var clubFeedbackMessage by remember { mutableStateOf<String?>(null) }
    var contractFeedbackMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .padding(8.dp)
                .testTag("transfer_negotiation_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
            border = BorderStroke(2.dp, GrassEmerald)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Modal Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    ProceduralPlayerFace(player = player, size = 48.dp)
                    Column {
                        Text(
                            text = "NEGOCIACIÓN DE FICHAJE: ${player.fullName.uppercase()}",
                            color = GrassEmerald,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${player.position.name} | ${player.age} años | OVR: ${player.getOverallRating()} | ${owningClub?.name ?: "Agente Libre"}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                HorizontalDivider(color = DarkSteel, modifier = Modifier.padding(bottom = 12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Step 1: Club Transfer Fee Negotiation (If owned by a club)
                    if (owningClub != null) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PitchDarkBg),
                                border = BorderStroke(1.dp, if (clubAccepted) GrassEmerald else DarkSteel),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "1. TRASPASO ENTRE CLUBES (${owningClub.name})",
                                        color = NeonAmber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Valor de Mercado Estimado: $${player.marketValue}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Oferta de Traspaso:", color = TextPrimary, fontSize = 12.sp)
                                        Text("$${feeOffer}", color = GrassEmerald, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = { feeOffer = (feeOffer - 50_000L).coerceAtLeast(10_000L) },
                                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCarbon),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("-$50k", fontSize = 10.sp)
                                        }
                                        Button(
                                            onClick = { feeOffer += 50_000L },
                                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCarbon),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("+$50k", fontSize = 10.sp)
                                        }
                                        Button(
                                            onClick = { feeOffer += 250_000L },
                                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCarbon),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("+$250k", fontSize = 10.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            val res = engine.negotiateClubTransfer(player, owningClub, feeOffer)
                                            when (res) {
                                                is TransferNegotiationResult.ClubAccepted -> {
                                                    clubAccepted = true
                                                    clubFeedbackMessage = "✅ ¡EL CLUB ACEPTÓ TU OFERTA DE $${feeOffer}! Pasa a negociar con el jugador."
                                                }
                                                is TransferNegotiationResult.ClubRejected -> {
                                                    clubAccepted = false
                                                    clubFeedbackMessage = "❌ ${res.reason}"
                                                    feeOffer = res.minimumFeeExpected
                                                }
                                                else -> {}
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (clubAccepted) DarkSteel else NeonAmber),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(if (clubAccepted) "✅ Oferta Aceptada por Club" else "Enviar Oferta de Traspaso al Club", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    if (clubFeedbackMessage != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = clubFeedbackMessage!!,
                                            color = if (clubAccepted) GrassEmerald else StatusRed,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Step 2: Personal Terms & Salary Negotiation
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PitchDarkBg),
                            border = BorderStroke(1.dp, DarkSteel),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "2. CONTRATO PERSONAL CON EL JUGADOR",
                                    color = GrassEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Pretensión Salarial Base: $${player.salary}/semana",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Salario Semanal Ofrecido:", color = TextPrimary, fontSize = 12.sp)
                                    Text("$${salaryOffer}/sem", color = GlacierBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { salaryOffer = (salaryOffer - 500L).coerceAtLeast(500L) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCarbon),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("-$500", fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = { salaryOffer += 500L },
                                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCarbon),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("+$500", fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = { salaryOffer += 2000L },
                                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCarbon),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("+$2k", fontSize = 10.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Duración de Contrato:", color = TextPrimary, fontSize = 12.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        (1..5).forEach { yrs ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (contractYearsOffer == yrs) GrassEmerald else SurfaceCarbon)
                                                    .clickable { contractYearsOffer = yrs }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text("$yrs yr", color = if (contractYearsOffer == yrs) PitchDarkBg else TextPrimary, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCarbon, contentColor = TextPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val contractRes = engine.negotiatePlayerContract(player, salaryOffer, contractYearsOffer, 0L)
                            when (contractRes) {
                                is TransferNegotiationResult.ContractAccepted -> {
                                    onConfirmSigning(feeOffer, salaryOffer, contractYearsOffer)
                                }
                                is TransferNegotiationResult.ContractRejected -> {
                                    contractFeedbackMessage = contractRes.reason
                                    salaryOffer = contractRes.requiredSalary
                                }
                                else -> {}
                            }
                        },
                        enabled = clubAccepted,
                        colors = ButtonDefaults.buttonColors(containerColor = GrassEmerald, contentColor = PitchDarkBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp)
                    ) {
                        Text("🖊️ FIRMAR FICHAJE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                if (contractFeedbackMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = contractFeedbackMessage!!,
                        color = StatusRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
