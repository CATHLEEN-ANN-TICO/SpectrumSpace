package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.SensoryLogEntity
import com.example.ui.components.BoxBreathingSection
import com.example.ui.components.Grounding54321Section
import com.example.ui.components.SensoryBatteryComposable
import com.example.ui.components.SensoryHistoryChartCard
import com.example.ui.components.TactileFidgetSection
import com.example.ui.theme.BatteryHigh
import com.example.ui.theme.BatteryLow
import com.example.ui.theme.BatteryMedium
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SensoryCheckInScreen(
    sensoryLogs: List<SensoryLogEntity>,
    onRecordLog: (energyPercent: Int, sensoryState: String, note: String) -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(70f) }
    var selectedState by remember { mutableStateOf("Calm") }
    var noteInput by remember { mutableStateOf("") }
    var activeToolTab by remember { mutableIntStateOf(0) } // 0: 54321, 1: Breathing, 2: Fidget

    val sensoryStates = listOf("Calm", "Balanced", "Overstimulated", "Drained", "Hyperfocused")

    val batteryColor = when {
        sliderValue >= 70f -> BatteryHigh
        sliderValue >= 40f -> BatteryMedium
        else -> BatteryLow
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("sensory_screen_list"),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // --- SECTION 0: Sensory Battery Color-Coded Scale Component ---
        item {
            SensoryBatteryComposable(
                currentEnergy = sliderValue.toInt(),
                onEnergySelected = { percent, state ->
                    sliderValue = percent.toFloat()
                    selectedState = state
                }
            )
        }

        // --- SECTION 1: Interactive Sensory Battery Slider Tracker ---
        item {
            SensoryHistoryChartCard(sensoryLogs = sensoryLogs)
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sensory_battery_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
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
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = null,
                                tint = batteryColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Sensory Energy Tank",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = batteryColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${sliderValue.toInt()}% Capacity",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = batteryColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Battery Slider
                    Column {
                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            valueRange = 10f..100f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = batteryColor,
                                activeTrackColor = batteryColor,
                                inactiveTrackColor = batteryColor.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("sensory_battery_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("10% Overwhelmed", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("50% Balanced", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("100% Full Energy", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // State Chips
                    Text(
                        text = "Current Primary Sensory State:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sensoryStates.forEach { state ->
                            FilterChip(
                                selected = selectedState == state,
                                onClick = { selectedState = state },
                                label = { Text(state) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("state_chip_$state")
                            )
                        }
                    }

                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Note or Trigger (Optional)") },
                        placeholder = { Text("E.g., Bright lights at mall, quiet after headphones.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_sensory_note"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            onRecordLog(sliderValue.toInt(), selectedState, noteInput.trim())
                            noteInput = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_sensory_log_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Current Energy Tank", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- SECTION 2: Calming Grounding Tools ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Calming & Grounding Tools",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )

                TabRow(
                    selectedTabIndex = activeToolTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                ) {
                    Tab(
                        selected = activeToolTab == 0,
                        onClick = { activeToolTab = 0 },
                        text = { Text("5-4-3-2-1 Sense", fontSize = 13.sp) },
                        modifier = Modifier.testTag("tab_tool_54321")
                    )
                    Tab(
                        selected = activeToolTab == 1,
                        onClick = { activeToolTab = 1 },
                        text = { Text("Box Breathing", fontSize = 13.sp) },
                        modifier = Modifier.testTag("tab_tool_breathing")
                    )
                    Tab(
                        selected = activeToolTab == 2,
                        onClick = { activeToolTab = 2 },
                        text = { Text("Touch Fidget", fontSize = 13.sp) },
                        modifier = Modifier.testTag("tab_tool_fidget")
                    )
                }

                when (activeToolTab) {
                    0 -> Grounding54321Section()
                    1 -> BoxBreathingSection()
                    2 -> TactileFidgetSection()
                }
            }
        }

        // --- SECTION 3: Recent Check-in Logs History ---
        item {
            Text(
                text = "Recent Energy Log History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
            )
        }

        if (sensoryLogs.isEmpty()) {
            item {
                Text(
                    text = "No check-in logs recorded yet. Log your current state above!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        } else {
            items(sensoryLogs.take(10), key = { it.id }) { log ->
                SensoryLogHistoryItem(log = log)
            }
        }
    }
}

@Composable
fun SensoryLogHistoryItem(log: SensoryLogEntity) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault()) }
    val formattedTime = remember(log.timestamp) { dateFormat.format(Date(log.timestamp)) }

    val batteryColor = when {
        log.energyPercent >= 70 -> BatteryHigh
        log.energyPercent >= 40 -> BatteryMedium
        else -> BatteryLow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sensory_log_item_${log.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = batteryColor.copy(alpha = 0.15f)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${log.energyPercent}%",
                        fontWeight = FontWeight.Bold,
                        color = batteryColor,
                        fontSize = 15.sp
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.sensoryState,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (log.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = log.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
