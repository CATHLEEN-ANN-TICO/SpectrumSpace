package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ScreenTab
import com.example.ui.components.SensoryBottomNav
import com.example.ui.components.SensoryTopBar
import com.example.ui.screens.*
import com.example.ui.theme.CalmSpaceTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
            val routineSteps by viewModel.routineSteps.collectAsStateWithLifecycle()
            val commCards by viewModel.commCards.collectAsStateWithLifecycle()
            val sensoryLogs by viewModel.sensoryLogs.collectAsStateWithLifecycle()
            val latestSensoryLog by viewModel.latestSensoryLog.collectAsStateWithLifecycle()
            val journalEntries by viewModel.journalEntries.collectAsStateWithLifecycle()
            val habits by viewModel.habits.collectAsStateWithLifecycle()

            val selectedRoutineCategory by viewModel.selectedRoutineCategory.collectAsStateWithLifecycle()
            val selectedCardCategory by viewModel.selectedCardCategory.collectAsStateWithLifecycle()
            val fullScreenCard by viewModel.fullScreenCard.collectAsStateWithLifecycle()

            var currentTab by remember { mutableStateOf<ScreenTab>(ScreenTab.Routines) }

            var showAddCardDialog by remember { mutableStateOf(false) }
            var showAddStepDialog by remember { mutableStateOf(false) }

            CalmSpaceTheme(
                paletteKey = userSettings.themePalette,
                highContrast = userSettings.highContrast
            ) {
                Scaffold(
                    topBar = {
                        SensoryTopBar(
                            title = when (currentTab) {
                                ScreenTab.Routines -> "Visual Routines"
                                ScreenTab.Cards -> "AAC Cards"
                                ScreenTab.Habits -> "Daily Habit Tracker"
                                ScreenTab.Journal -> "Safe Space Journal"
                                ScreenTab.Sensory -> "Grounding & Battery"
                                ScreenTab.Settings -> "Sensory Settings"
                            },
                            latestEnergyPercent = latestSensoryLog?.energyPercent,
                            latestSensoryState = latestSensoryLog?.sensoryState,
                            onEnergyClick = { currentTab = ScreenTab.Sensory }
                        )
                    },
                    bottomBar = {
                        SensoryBottomNav(
                            currentRoute = currentTab.route,
                            onTabSelected = { currentTab = it }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            ScreenTab.Routines -> {
                                VisualRoutinesScreen(
                                    steps = routineSteps,
                                    selectedCategory = selectedRoutineCategory,
                                    onSelectCategory = { viewModel.selectRoutineCategory(it) },
                                    onToggleStep = { viewModel.toggleStepCompleted(it) },
                                    onDeleteStep = { viewModel.deleteStep(it) },
                                    onResetCategory = { viewModel.resetCategorySteps(it) },
                                    onOpenAddDialog = { showAddStepDialog = true }
                                )
                            }
                            ScreenTab.Cards -> {
                                CommCardsScreen(
                                    cards = commCards,
                                    selectedCategory = selectedCardCategory,
                                    onSelectCategory = { viewModel.selectCardCategory(it) },
                                    onCardClick = { viewModel.openFullScreenCard(it) },
                                    onSpeakCard = { viewModel.speakPhrase(it) },
                                    onToggleFavorite = { viewModel.toggleFavoriteCard(it) },
                                    onDeleteCard = { viewModel.deleteCard(it) },
                                    onOpenAddDialog = { showAddCardDialog = true }
                                )
                            }
                            ScreenTab.Habits -> {
                                DailyHabitTrackerScreen(
                                    habits = habits,
                                    onToggleHabit = { viewModel.toggleHabit(it) },
                                    onAddHabit = { title, desc, icon -> viewModel.addHabit(title, desc, icon) },
                                    onDeleteHabit = { viewModel.deleteHabit(it) }
                                )
                            }
                            ScreenTab.Journal -> {

                                SafeSpaceJournalScreen(
                                    journalEntries = journalEntries,
                                    onAddEntry = { emoji, title, text, isVoice ->
                                        viewModel.addJournalEntry(emoji, title, text, isVoice)
                                    },
                                    onDeleteEntry = { viewModel.deleteJournalEntry(it) }
                                )
                            }
                            ScreenTab.Sensory -> {
                                SensoryCheckInScreen(
                                    sensoryLogs = sensoryLogs,
                                    onRecordLog = { energy, state, note ->
                                        viewModel.recordSensoryLog(energy, state, note)
                                    }
                                )
                            }
                            ScreenTab.Settings -> {
                                SensorySettingsScreen(
                                    userSettings = userSettings,
                                    onUpdateThemePalette = { viewModel.updateThemePalette(it) },
                                    onUpdateHighContrast = { viewModel.updateHighContrast(it) },
                                    onUpdateReduceAnimations = { viewModel.updateReduceAnimations(it) },
                                    onUpdateFocusMode = { viewModel.updateFocusMode(it) },
                                    onUpdateFontScale = { viewModel.updateFontScale(it) },
                                    onUpdateSpeechSettings = { pitch, rate ->
                                        viewModel.updateSpeechSettings(pitch, rate)
                                    },
                                    onTestVoice = { viewModel.speakPhrase(it) },
                                    onExportCsvData = { viewModel.generateExportCsvData() }
                                )
                            }
                        }
                    }

                    // --- Dialog Overlays ---
                    fullScreenCard?.let { card ->
                        FullScreenCardDialog(
                            card = card,
                            onSpeak = { viewModel.speakPhrase(it) },
                            onToggleFavorite = { viewModel.toggleFavoriteCard(it) },
                            onDismiss = { viewModel.closeFullScreenCard() }
                        )
                    }

                    if (showAddCardDialog) {
                        AddCustomCardDialog(
                            onAddCard = { title, phrase, cat ->
                                viewModel.addCustomCard(title, phrase, cat)
                            },
                            onDismiss = { showAddCardDialog = false }
                        )
                    }

                    if (showAddStepDialog) {
                        val context = LocalContext.current
                        AddRoutineStepDialog(
                            defaultCategory = selectedRoutineCategory,
                            onAddStep = { cat, title, desc, mins, icon, rHour, rMin, hasRem ->
                                viewModel.addCustomStep(
                                    context = context,
                                    category = cat,
                                    title = title,
                                    description = desc,
                                    durationMinutes = mins,
                                    iconName = icon,
                                    reminderHour = rHour,
                                    reminderMinute = rMin,
                                    hasReminder = hasRem
                                )
                            },
                            onDismiss = { showAddStepDialog = false }
                        )
                    }
                }
            }
        }
    }
}
