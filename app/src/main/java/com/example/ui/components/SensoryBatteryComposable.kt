package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BatteryHigh
import com.example.ui.theme.BatteryLow
import com.example.ui.theme.BatteryMedium

data class SensoryBatteryLevel(
    val title: String,
    val energyPercent: Int,
    val color: Color,
    val icon: ImageVector,
    val description: String
)

@Composable
fun SensoryBatteryComposable(
    currentEnergy: Int = 75,
    onEnergySelected: (Int, String) -> Unit
) {
    val levels = listOf(
        SensoryBatteryLevel(
            title = "Overstimulated",
            energyPercent = 20,
            color = BatteryLow,
            icon = Icons.Default.Warning,
            description = "Sensory overload, need quiet & low stimulation immediately."
        ),
        SensoryBatteryLevel(
            title = "Drained / Tired",
            energyPercent = 45,
            color = BatteryMedium,
            icon = Icons.Default.BatteryAlert,
            description = "Low energy, need gentle pacing and rest."
        ),
        SensoryBatteryLevel(
            title = "Balanced & Okay",
            energyPercent = 70,
            color = BatteryMedium,
            icon = Icons.Default.BatteryChargingFull,
            description = "Stable and coping well with current surroundings."
        ),
        SensoryBatteryLevel(
            title = "High Energy & Calm",
            energyPercent = 95,
            color = BatteryHigh,
            icon = Icons.Default.Spa,
            description = "Feeling peaceful, energized, and ready for the day."
        )
    )

    var selectedLevel by remember {
        mutableStateOf(
            levels.firstOrNull { kotlin.math.abs(it.energyPercent - currentEnergy) < 30 } ?: levels[2]
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sensory_battery_composable"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = selectedLevel.icon,
                        contentDescription = null,
                        tint = selectedLevel.color,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Sensory Battery Level",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = selectedLevel.color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${selectedLevel.energyPercent}% - ${selectedLevel.title}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = selectedLevel.color,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Text(
                text = "Select how your sensory tank feels right now (Red = Overstimulated, Yellow = Okay, Green = High Energy):",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 4 Color-Coded Level Buttons
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                levels.forEach { level ->
                    val isSelected = selectedLevel.title == level.title
                    Surface(
                        onClick = {
                            selectedLevel = level
                            onEnergySelected(level.energyPercent, level.title)
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) level.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, level.color) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("battery_level_${level.title.lowercase().replace(" ", "_")}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(level.color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = level.icon,
                                    contentDescription = level.title,
                                    tint = level.color,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = level.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = level.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = level.color,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
