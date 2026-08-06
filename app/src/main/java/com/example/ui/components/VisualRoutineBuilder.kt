package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

data class RoutineIconOption(
    val name: String,
    val label: String,
    val icon: ImageVector
)

data class RoutineColorOption(
    val name: String,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisualRoutineBuilderCard(
    defaultCategory: String = "Morning",
    onSaveRoutineStep: (category: String, title: String, description: String, iconName: String, colorTag: String, durationMinutes: Int) -> Unit,
    onCancel: () -> Unit = {}
) {
    var stepTitle by remember { mutableStateOf("") }
    var stepDescription by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(defaultCategory) }
    var durationMinutes by remember { mutableStateOf(10) }
    
    val iconOptions = listOf(
        RoutineIconOption("Waking", "Wake Up", Icons.Default.WbSunny),
        RoutineIconOption("Water", "Hydrate", Icons.Default.WaterDrop),
        RoutineIconOption("Breakfast", "Meal", Icons.Default.Restaurant),
        RoutineIconOption("Brush", "Hygiene", Icons.Default.Check),
        RoutineIconOption("Breathe", "Breathe", Icons.Default.Air),
        RoutineIconOption("Quiet", "Quiet Time", Icons.Default.SelfImprovement),
        RoutineIconOption("Walk", "Walk/Move", Icons.Default.DirectionsWalk),
        RoutineIconOption("Tidy", "Tidy Up", Icons.Default.CleaningServices),
        RoutineIconOption("Read", "Read/Study", Icons.Default.MenuBook),
        RoutineIconOption("Sleep", "Rest", Icons.Default.Bedtime)
    )

    var selectedIcon by remember { mutableStateOf(iconOptions.first()) }

    val categories = listOf("Morning", "Sensory Break", "Evening", "Work/Study")
    
    val colorOptions = listOf(
        RoutineColorOption("Sage", Color(0xFF81C784)),
        RoutineColorOption("Lavender", Color(0xFFBA68C8)),
        RoutineColorOption("Sand", Color(0xFFFFB74D)),
        RoutineColorOption("Sky", Color(0xFF64B5F6)),
        RoutineColorOption("Rose", Color(0xFFF06292))
    )

    var selectedColor by remember { mutableStateOf(colorOptions.first()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("visual_routine_builder_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "Visual Routine Builder",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Structure your day with custom icons, color-coded tags, and calming visual cues.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Step Title
            OutlinedTextField(
                value = stepTitle,
                onValueChange = { stepTitle = it },
                label = { Text("Task / Step Title") },
                placeholder = { Text("E.g., 10-Minute Quiet Reading") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("builder_input_title"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Step Description
            OutlinedTextField(
                value = stepDescription,
                onValueChange = { stepDescription = it },
                label = { Text("Gentle Instruction / Tip (Optional)") },
                placeholder = { Text("E.g., Find a cozy armchair and dim lights.") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            // Routine Category Chips
            Text(
                text = "Routine Category:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("builder_category_$cat")
                    )
                }
            }

            // Icon Selector
            Text(
                text = "Select Visual Routine Icon:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(iconOptions) { item ->
                    val isSelected = selectedIcon.name == item.name
                    Surface(
                        onClick = { selectedIcon = item },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("builder_icon_${item.name}")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                maxLines = 1,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Color-coded Label Tag Selector
            Text(
                text = "Color-Coded Label Tag:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                colorOptions.forEach { colOpt ->
                    val isSelected = selectedColor.name == colOpt.name
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colOpt.color)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = colOpt }
                            .testTag("builder_color_${colOpt.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Duration Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estimated Duration",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "$durationMinutes minutes",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Slider(
                    value = durationMinutes.toFloat(),
                    onValueChange = { durationMinutes = it.toInt() },
                    valueRange = 1f..60f,
                    steps = 58,
                    modifier = Modifier.testTag("builder_duration_slider")
                )
            }

            // Save & Cancel Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onCancel != {}) {
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = {
                        if (stepTitle.isNotBlank()) {
                            onSaveRoutineStep(
                                selectedCategory,
                                stepTitle.trim(),
                                stepDescription.trim(),
                                selectedIcon.name,
                                selectedColor.name,
                                durationMinutes
                            )
                            stepTitle = ""
                            stepDescription = ""
                        }
                    },
                    enabled = stepTitle.isNotBlank(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("builder_save_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add to Visual Routine", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
