package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BatteryHigh
import com.example.ui.theme.BatteryLow
import com.example.ui.theme.BatteryMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensoryTopBar(
    title: String,
    latestEnergyPercent: Int?,
    latestSensoryState: String?,
    onEnergyClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            if (latestEnergyPercent != null) {
                val batteryColor = when {
                    latestEnergyPercent >= 70 -> BatteryHigh
                    latestEnergyPercent >= 40 -> BatteryMedium
                    else -> BatteryLow
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = batteryColor.copy(alpha = 0.15f),
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onEnergyClick() }
                        .testTag("topbar_energy_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (latestEnergyPercent > 50) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                            contentDescription = "Sensory Battery $latestEnergyPercent%",
                            tint = batteryColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$latestEnergyPercent% ${latestSensoryState ?: ""}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
