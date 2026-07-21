package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DashboardTab(val title: String, val icon: ImageVector) {
    object ClubInfo : DashboardTab("Club y Tabla", Icons.Default.SportsFootball)
    object Squad : DashboardTab("Plantilla", Icons.Default.People)
    object Calendar : DashboardTab("Calendario", Icons.Default.DateRange)
    object LiveMatch : DashboardTab("Simulador Vivo", Icons.Default.Tv)
    object Social : DashboardTab("Red Social", Icons.Default.AlternateEmail)
    object ManagerCareer : DashboardTab("Mi Carrera", Icons.Default.AccountBalanceWallet)
    object FafiFederation : DashboardTab("Gabinete FEDEBOL", Icons.Default.Policy)
}
