package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.HabitEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DailyHabitTrackerScreen(
    habits: List<HabitEntity>,
    onToggleHabit: (HabitEntity) -> Unit,
    onAddHabit: (title: String, description: String, iconName: String) -> Unit,
    onDeleteHabit: (HabitEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var celebratingHabitId by remember { mutableStateOf<Long?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val completedCount = habits.count { it.isCompleted }
    val totalCount = habits.size
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val allCompleted = totalCount > 0 && completedCount == totalCount

    // Celebratory pulse animation for header if all completed
    val infiniteTransition = rememberInfiniteTransition(label = "celebration_pulse")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (allCompleted) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_habit_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Habit")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card with progress & celebration
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (allCompleted) 
                            MaterialTheme.colorScheme.tertiaryContainer 
                        else 
                            MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scalePulse)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (allCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (allCompleted) Icons.Default.EmojiEvents else Icons.Default.Checklist,
                                        contentDescription = null,
                                        tint = if (allCompleted) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (allCompleted) "Wonderful! All Habits Complete! 🎉" else "Daily Habit Tracker",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (allCompleted) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "$completedCount of $totalCount wellness goals completed today",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = (if (allCompleted) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer).copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Progress Indicator
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = if (allCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            // Habits Section Header
            item {
                Text(
                    text = "Today's Wellness Checklist",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (habits.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "No wellness habits added yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap the + button below to add a gentle daily habit.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(habits, key = { it.id }) { habit ->
                    val isCelebrating = celebratingHabitId == habit.id
                    val itemScale by animateFloatAsState(
                        targetValue = if (isCelebrating) 1.06f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "habit_scale"
                    )

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (habit.isCompleted) 
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else 
                                MaterialTheme.colorScheme.surface
                        ),
                        border = if (habit.isCompleted) 
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                        else 
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (habit.isCompleted) 0.dp else 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(itemScale)
                            .testTag("habit_card_${habit.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onToggleHabit(habit)
                                    if (!habit.isCompleted) {
                                        celebratingHabitId = habit.id
                                        coroutineScope.launch {
                                            delay(600)
                                            if (celebratingHabitId == habit.id) celebratingHabitId = null
                                        }
                                    }
                                }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Large Easy-to-Tap Icon Button
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (habit.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = getHabitIcon(habit.iconName),
                                        contentDescription = habit.title,
                                        tint = if (habit.isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            // Title & Description & Streak
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = habit.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (habit.description.isNotBlank()) {
                                    Text(
                                        text = habit.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (habit.streakCount > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalFireDepartment,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${habit.streakCount} day streak",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }

                            // Checkbox / Completion Indicator
                            Checkbox(
                                checked = habit.isCompleted,
                                onCheckedChange = {
                                    onToggleHabit(habit)
                                    if (!habit.isCompleted) {
                                        celebratingHabitId = habit.id
                                        coroutineScope.launch {
                                            delay(600)
                                            if (celebratingHabitId == habit.id) celebratingHabitId = null
                                        }
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("habit_checkbox_${habit.id}")
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, desc, iconName ->
                onAddHabit(title, desc, iconName)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, iconName: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val icons = listOf("WaterDrop", "Medication", "Accessibility", "WbSunny", "SelfImprovement", "Favorite", "Restaurant")
    var selectedIcon by remember { mutableStateOf("WaterDrop") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Wellness Habit") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Title (e.g. Drank Water)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_habit_title_input")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_habit_desc_input")
                )

                Text(
                    text = "Choose Icon",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icons.take(4).forEach { iconName ->
                        val isSelected = selectedIcon == iconName
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .clickable { selectedIcon = iconName }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = getHabitIcon(iconName),
                                    contentDescription = iconName,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title.trim(), description.trim(), selectedIcon)
                    }
                },
                modifier = Modifier.testTag("save_new_habit_button")
            ) {
                Text("Add Habit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun getHabitIcon(iconName: String): ImageVector {
    return when (iconName) {
        "WaterDrop" -> Icons.Default.WaterDrop
        "Medication" -> Icons.Default.Medication
        "Accessibility" -> Icons.Default.Accessibility
        "WbSunny" -> Icons.Default.WbSunny
        "SelfImprovement" -> Icons.Default.SelfImprovement
        "Favorite" -> Icons.Default.Favorite
        "Restaurant" -> Icons.Default.Restaurant
        else -> Icons.Default.CheckCircle
    }
}
