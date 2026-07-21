package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DashboardTab
import com.example.ui.theme.GrassEmerald
import com.example.ui.theme.PitchDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NavigationSidebar(
    activeTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        DashboardTab.ClubInfo,
        DashboardTab.Squad,
        DashboardTab.Calendar,
        DashboardTab.LiveMatch,
        DashboardTab.Social,
        DashboardTab.ManagerCareer,
        DashboardTab.FafiFederation
    )

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "FAFI",
            color = GrassEmerald,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        tabs.forEach { tab ->
            val isSelected = tab == activeTab
            val iconColor = if (isSelected) GrassEmerald else TextSecondary
            val bgColor = if (isSelected) PitchDarkBg else Color.Transparent

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 8.dp)
                    .testTag("nav_tab_${tab.title.replace(" ", "_").lowercase()}"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tab.title,
                    color = if (isSelected) TextPrimary else TextSecondary,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
