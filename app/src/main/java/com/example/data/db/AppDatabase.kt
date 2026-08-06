package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RoutineStepEntity::class,
        CommCardEntity::class,
        SensoryLogEntity::class,
        UserSettingsEntity::class,
        JournalEntryEntity::class,
        HabitEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun commCardDao(): CommCardDao
    abstract fun sensoryLogDao(): SensoryLogDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun journalDao(): JournalDao
    abstract fun habitDao(): HabitDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calm_space_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            // Initial Settings
            database.userSettingsDao().saveSettings(
                UserSettingsEntity(
                    id = 1,
                    themePalette = "SAGE",
                    fontScale = 1.0f,
                    highContrast = false,
                    reduceAnimations = false,
                    speechPitch = 1.0f,
                    speechRate = 0.85f
                )
            )

            // Initial Routine Steps
            val defaultRoutines = listOf(
                // Morning Routine
                RoutineStepEntity(
                    routineCategory = "Morning",
                    stepTitle = "Wake Up & Soft Stretch",
                    description = "Take 3 slow deep breaths before moving.",
                    iconName = "Waking",
                    durationMinutes = 5,
                    orderIndex = 0
                ),
                RoutineStepEntity(
                    routineCategory = "Morning",
                    stepTitle = "Drink Water",
                    description = "A full glass of cool water to wake up your sensory system.",
                    iconName = "Water",
                    durationMinutes = 2,
                    orderIndex = 1
                ),
                RoutineStepEntity(
                    routineCategory = "Morning",
                    stepTitle = "Wash Face & Brush Teeth",
                    description = "Warm or cool water to feel fresh.",
                    iconName = "Brush",
                    durationMinutes = 5,
                    orderIndex = 2
                ),
                RoutineStepEntity(
                    routineCategory = "Morning",
                    stepTitle = "Eat Comfortable Breakfast",
                    description = "Choose a familiar texture and taste you love today.",
                    iconName = "Breakfast",
                    durationMinutes = 15,
                    orderIndex = 3
                ),
                RoutineStepEntity(
                    routineCategory = "Morning",
                    stepTitle = "Review Daily Schedule",
                    description = "Look over your plan so there are no surprises.",
                    iconName = "Schedule",
                    durationMinutes = 5,
                    orderIndex = 4
                ),

                // Sensory Reset Routine
                RoutineStepEntity(
                    routineCategory = "Sensory Break",
                    stepTitle = "Step into Quiet Space",
                    description = "Dim lighting or put on noise-canceling headphones.",
                    iconName = "Quiet",
                    durationMinutes = 3,
                    orderIndex = 0
                ),
                RoutineStepEntity(
                    routineCategory = "Sensory Break",
                    stepTitle = "Box Breathing (4-4-4-4)",
                    description = "Follow the visual breathing rhythm for 3 cycles.",
                    iconName = "Breathe",
                    durationMinutes = 4,
                    orderIndex = 1
                ),
                RoutineStepEntity(
                    routineCategory = "Sensory Break",
                    stepTitle = "Tactile Fidget or Weighted Touch",
                    description = "Squeeze a stress ball or drape a heavy blanket.",
                    iconName = "Touch",
                    durationMinutes = 5,
                    orderIndex = 2
                ),
                RoutineStepEntity(
                    routineCategory = "Sensory Break",
                    stepTitle = "Sensory Battery Check",
                    description = "Log how much energy you restored.",
                    iconName = "Battery",
                    durationMinutes = 2,
                    orderIndex = 3
                ),

                // Evening Routine
                RoutineStepEntity(
                    routineCategory = "Evening",
                    stepTitle = "Tidy Workspace & Prepare Tomorrow",
                    description = "Put away items so morning is gentle and predictable.",
                    iconName = "Tidy",
                    durationMinutes = 10,
                    orderIndex = 0
                ),
                RoutineStepEntity(
                    routineCategory = "Evening",
                    stepTitle = "Pajamas & Soft Clothing",
                    description = "Change into seamless, soft tag-free clothing.",
                    iconName = "Shirt",
                    durationMinutes = 5,
                    orderIndex = 1
                ),
                RoutineStepEntity(
                    routineCategory = "Evening",
                    stepTitle = "Low Light & Screen Warmth",
                    description = "Turn off harsh overhead lights.",
                    iconName = "NightLight",
                    durationMinutes = 5,
                    orderIndex = 2
                ),
                RoutineStepEntity(
                    routineCategory = "Evening",
                    stepTitle = "Special Interest or Reading",
                    description = "Enjoy your favorite comfort topic without distraction.",
                    iconName = "Book",
                    durationMinutes = 20,
                    orderIndex = 3
                )
            )
            database.routineDao().insertSteps(defaultRoutines)

            // Initial AAC Cards
            val defaultCards = listOf(
                // Needs
                CommCardEntity(
                    category = "Needs",
                    title = "Need a Quiet Break",
                    phrase = "I am feeling overstimulated and need a quiet break right now.",
                    iconName = "VolumeOff",
                    isFavorite = true
                ),
                CommCardEntity(
                    category = "Needs",
                    title = "Too Loud Here",
                    phrase = "The environment is too loud for me. Please lower the volume.",
                    iconName = "VolumeMute",
                    isFavorite = true
                ),
                CommCardEntity(
                    category = "Needs",
                    title = "Need Water",
                    phrase = "Could I please have a glass of water?",
                    iconName = "LocalDrinkingWater",
                    isFavorite = false
                ),
                CommCardEntity(
                    category = "Needs",
                    title = "Need Personal Space",
                    phrase = "I need some personal space for a few minutes.",
                    iconName = "AccessibilityNew",
                    isFavorite = true
                ),
                CommCardEntity(
                    category = "Needs",
                    title = "Please Slow Down",
                    phrase = "Could you please slow down and give me a moment to process?",
                    iconName = "HourglassBottom",
                    isFavorite = false
                ),

                // Emotions
                CommCardEntity(
                    category = "Emotions",
                    title = "I'm Overwhelmed",
                    phrase = "I am overwhelmed right now. I need a calm moment.",
                    iconName = "Warning",
                    isFavorite = true
                ),
                CommCardEntity(
                    category = "Emotions",
                    title = "Feeling Calm",
                    phrase = "I am feeling peaceful and comfortable.",
                    iconName = "Spa",
                    isFavorite = false
                ),
                CommCardEntity(
                    category = "Emotions",
                    title = "I Need Processing Time",
                    phrase = "I hear you, but my brain needs extra time to process this.",
                    iconName = "Psychology",
                    isFavorite = true
                ),
                CommCardEntity(
                    category = "Emotions",
                    title = "I'm Non-Verbal Right Now",
                    phrase = "I cannot speak easily right now. Please read my cards or text me.",
                    iconName = "RecordVoiceOver",
                    isFavorite = true
                ),

                // Quick Answers
                CommCardEntity(
                    category = "Answers",
                    title = "Yes",
                    phrase = "Yes.",
                    iconName = "ThumbUp",
                    isFavorite = true
                ),
                CommCardEntity(
                    category = "Answers",
                    title = "No, Thank You",
                    phrase = "No, thank you.",
                    iconName = "ThumbDown",
                    isFavorite = true
                ),
                CommCardEntity(
                    category = "Answers",
                    title = "I Don't Know Yet",
                    phrase = "I don't know yet.",
                    iconName = "HelpOutline",
                    isFavorite = false
                ),
                CommCardEntity(
                    category = "Answers",
                    title = "Thank You",
                    phrase = "Thank you so much for understanding.",
                    iconName = "Favorite",
                    isFavorite = false
                )
            )
            database.commCardDao().insertCards(defaultCards)

            // Initial Sensory Log
            database.sensoryLogDao().insertLog(
                SensoryLogEntity(
                    energyPercent = 75,
                    sensoryState = "Calm",
                    note = "Welcome to Calm Space! Your peaceful companion."
                )
            )

            // Initial Habits
            val defaultHabits = listOf(
                HabitEntity(title = "Drank Water", description = "Hydration is key for gentle energy.", iconName = "WaterDrop"),
                HabitEntity(title = "Took Medication / Supplements", description = "Daily wellness routine.", iconName = "Medication"),
                HabitEntity(title = "Morning Gentle Stretch", description = "Awaken the body with soft movement.", iconName = "Accessibility"),
                HabitEntity(title = "Fresh Air / Sunlight", description = "Step outside or near a bright window.", iconName = "WbSunny"),
                HabitEntity(title = "Mindful Breathing (5 mins)", description = "Calm nervous system reset.", iconName = "SelfImprovement")
            )
            for (habit in defaultHabits) {
                database.habitDao().insertHabit(habit)
            }
        }
    }
}
