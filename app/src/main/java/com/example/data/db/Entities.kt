package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_steps")
data class RoutineStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineCategory: String, // "Morning", "Evening", "Sensory Break", "Work/Study"
    val stepTitle: String,
    val description: String = "",
    val iconName: String = "CheckCircle",
    val durationMinutes: Int = 5,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "comm_cards")
data class CommCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // "Needs", "Emotions", "Answers", "Custom"
    val title: String,
    val phrase: String,
    val iconName: String = "Message",
    val isFavorite: Boolean = false,
    val usageCount: Int = 0
)

@Entity(tableName = "sensory_logs")
data class SensoryLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val energyPercent: Int = 50, // 10 to 100
    val sensoryState: String = "Balanced", // e.g. "Calm", "Overstimulated", "Drained", "Hyperfocused"
    val note: String = ""
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val themePalette: String = "SAGE", // "SAGE", "SAND", "LAVENDER", "DUSK"
    val fontScale: Float = 1.0f, // 0.9f to 1.3f
    val highContrast: Boolean = false,
    val reduceAnimations: Boolean = false,
    val speechPitch: Float = 1.0f,
    val speechRate: Float = 0.9f // slightly calmer, slower speech default
)
