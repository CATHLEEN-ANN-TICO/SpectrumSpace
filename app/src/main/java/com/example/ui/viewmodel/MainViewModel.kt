package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository: AppRepository
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    // State Flows
    val userSettings: StateFlow<UserSettingsEntity>
    val routineSteps: StateFlow<List<RoutineStepEntity>>
    val commCards: StateFlow<List<CommCardEntity>>
    val sensoryLogs: StateFlow<List<SensoryLogEntity>>
    val latestSensoryLog: StateFlow<SensoryLogEntity?>
    val journalEntries: StateFlow<List<JournalEntryEntity>>
    val habits: StateFlow<List<HabitEntity>>


    // Selected Routine Filter Category ("All", "Morning", "Sensory Break", "Evening")
    private val _selectedRoutineCategory = MutableStateFlow("All")
    val selectedRoutineCategory: StateFlow<String> = _selectedRoutineCategory.asStateFlow()

    // Selected Comm Card Filter Category ("All", "Needs", "Emotions", "Answers")
    private val _selectedCardCategory = MutableStateFlow("All")
    val selectedCardCategory: StateFlow<String> = _selectedCardCategory.asStateFlow()

    // Active Card for Fullscreen display mode
    private val _fullScreenCard = MutableStateFlow<CommCardEntity?>(null)
    val fullScreenCard: StateFlow<CommCardEntity?> = _fullScreenCard.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database)

        tts = TextToSpeech(application, this)

        userSettings = repository.getUserSettings()
            .map { it ?: UserSettingsEntity() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettingsEntity())

        routineSteps = repository.getAllRoutineSteps()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        commCards = repository.getAllCards()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        sensoryLogs = repository.getAllSensoryLogs()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        latestSensoryLog = repository.getLatestSensoryLog()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        journalEntries = repository.getAllJournalEntries()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        habits = repository.getAllHabits()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }


    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsReady = true
                applyTtsSettings()
            }
        }
    }

    private fun applyTtsSettings() {
        val settings = userSettings.value
        tts?.setPitch(settings.speechPitch)
        tts?.setSpeechRate(settings.speechRate)
    }

    fun speakPhrase(phrase: String) {
        if (isTtsReady) {
            applyTtsSettings()
            tts?.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, "COMM_CARD_ID")
        }
    }

    // --- Routine Actions ---
    fun selectRoutineCategory(category: String) {
        _selectedRoutineCategory.value = category
    }

    fun toggleStepCompleted(step: RoutineStepEntity) {
        viewModelScope.launch {
            repository.setStepCompleted(step.id, !step.isCompleted)
        }
    }

    fun addCustomStep(
        context: android.content.Context,
        category: String,
        title: String,
        description: String,
        durationMinutes: Int,
        iconName: String = "CheckCircle",
        reminderHour: Int = 8,
        reminderMinute: Int = 0,
        hasReminder: Boolean = false
    ) {
        viewModelScope.launch {
            val currentSteps = routineSteps.value
            val maxIndex = currentSteps.filter { it.routineCategory == category }.maxOfOrNull { it.orderIndex } ?: 0
            repository.insertStep(
                RoutineStepEntity(
                    routineCategory = category,
                    stepTitle = title,
                    description = description,
                    iconName = iconName,
                    durationMinutes = durationMinutes,
                    orderIndex = maxIndex + 1,
                    reminderHour = reminderHour,
                    reminderMinute = reminderMinute,
                    hasReminder = hasReminder
                )
            )

            if (hasReminder) {
                val calendar = java.util.Calendar.getInstance().apply {
                    set(java.util.Calendar.HOUR_OF_DAY, reminderHour)
                    set(java.util.Calendar.MINUTE, reminderMinute)
                    set(java.util.Calendar.SECOND, 0)
                    if (timeInMillis <= System.currentTimeMillis()) {
                        add(java.util.Calendar.DAY_OF_YEAR, 1)
                    }
                }
                com.example.utils.ReminderScheduler.scheduleReminder(
                    context = context,
                    id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                    title = "Routine Reminder: $title",
                    message = description.ifBlank { "Time for your scheduled $category routine task ($durationMinutes mins)." },
                    triggerAtMillis = calendar.timeInMillis
                )
            }
        }
    }

    fun deleteStep(step: RoutineStepEntity) {
        viewModelScope.launch {
            repository.deleteStep(step)
        }
    }

    fun resetCategorySteps(category: String) {
        viewModelScope.launch {
            if (category == "All") {
                repository.resetAllSteps()
            } else {
                repository.resetCategorySteps(category)
            }
        }
    }

    // --- Comm Card Actions ---
    fun selectCardCategory(category: String) {
        _selectedCardCategory.value = category
    }

    fun openFullScreenCard(card: CommCardEntity) {
        _fullScreenCard.value = card
        viewModelScope.launch {
            repository.incrementCardUsage(card.id)
        }
    }

    fun closeFullScreenCard() {
        _fullScreenCard.value = null
    }

    fun toggleFavoriteCard(card: CommCardEntity) {
        viewModelScope.launch {
            repository.updateCard(card.copy(isFavorite = !card.isFavorite))
        }
    }

    fun addCustomCard(title: String, phrase: String, category: String) {
        viewModelScope.launch {
            repository.insertCard(
                CommCardEntity(
                    category = category,
                    title = title,
                    phrase = phrase,
                    iconName = "Message"
                )
            )
        }
    }

    fun deleteCard(card: CommCardEntity) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    // --- Sensory Battery & Log Actions ---
    fun recordSensoryLog(energyPercent: Int, sensoryState: String, note: String) {
        viewModelScope.launch {
            repository.insertSensoryLog(
                SensoryLogEntity(
                    energyPercent = energyPercent,
                    sensoryState = sensoryState,
                    note = note
                )
            )
        }
    }

    // --- Safe Space Journal Actions ---
    fun addJournalEntry(moodEmoji: String, moodTitle: String, reflectionText: String, isVoiceLogged: Boolean) {
        viewModelScope.launch {
            repository.insertJournalEntry(
                JournalEntryEntity(
                    moodEmoji = moodEmoji,
                    moodTitle = moodTitle,
                    reflectionText = reflectionText,
                    isVoiceLogged = isVoiceLogged
                )
            )
        }
    }

    fun deleteJournalEntry(entry: JournalEntryEntity) {
        viewModelScope.launch {
            repository.deleteJournalEntry(entry)
        }
    }

    // --- Daily Habit Tracker Actions ---
    fun toggleHabit(habit: HabitEntity) {
        viewModelScope.launch {
            val newState = !habit.isCompleted
            val newStreak = if (newState) habit.streakCount + 1 else maxOf(0, habit.streakCount - 1)
            val updated = habit.copy(
                isCompleted = newState,
                streakCount = newStreak,
                lastCompletedDate = if (newState) java.time.LocalDate.now().toString() else habit.lastCompletedDate
            )
            repository.updateHabit(updated)
        }
    }

    fun addHabit(title: String, description: String, iconName: String) {
        viewModelScope.launch {
            repository.insertHabit(
                HabitEntity(
                    title = title,
                    description = description,
                    iconName = iconName
                )
            )
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }


    // --- Settings Actions ---
    fun updateThemePalette(palette: String) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.saveUserSettings(current.copy(themePalette = palette))
        }
    }

    fun updateHighContrast(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.saveUserSettings(current.copy(highContrast = enabled))
        }
    }

    fun updateReduceAnimations(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.saveUserSettings(current.copy(reduceAnimations = enabled))
        }
    }

    fun updateFocusMode(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.saveUserSettings(current.copy(highContrast = enabled, reduceAnimations = enabled))
        }
    }

    fun updateFontScale(scale: Float) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.saveUserSettings(current.copy(fontScale = scale))
        }
    }

    fun updateSpeechSettings(pitch: Float, rate: Float) {
        viewModelScope.launch {
            val current = userSettings.value
            val updated = current.copy(speechPitch = pitch, speechRate = rate)
            repository.saveUserSettings(updated)
            applyTtsSettings()
        }
    }

    fun generateExportCsvData(): String {
        val habitsList = habits.value
        val logsList = sensoryLogs.value
        val journalList = journalEntries.value

        val sb = StringBuilder()
        sb.append("# CALM SPACE WELLNESS & THERAPY EXPORT REPORT\n")
        sb.append("# Generated on: ${java.time.LocalDateTime.now()}\n\n")

        sb.append("=== DAILY HABITS ==-\n")
        sb.append("ID,Title,Description,IsCompleted,StreakCount,LastCompletedDate\n")
        habitsList.forEach { h ->
            sb.append("${h.id},\"${h.title}\",\"${h.description}\",${h.isCompleted},${h.streakCount},${h.lastCompletedDate}\n")
        }

        sb.append("\n=== SENSORY BATTERY HISTORY ==-\n")
        sb.append("ID,Timestamp,EnergyPercent,SensoryState,Note\n")
        logsList.forEach { l ->
            val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(l.timestamp))
            sb.append("${l.id},$dateStr,${l.energyPercent},\"${l.sensoryState}\",\"${l.note.replace("\n", " ")}\"\n")
        }

        sb.append("\n=== SAFE SPACE JOURNAL REFLECTIONS ==-\n")
        sb.append("ID,Timestamp,MoodEmoji,MoodTitle,ReflectionText,IsVoiceLogged\n")
        journalList.forEach { j ->
            val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(j.timestamp))
            sb.append("${j.id},$dateStr,\"${j.moodEmoji}\",\"${j.moodTitle}\",\"${j.reflectionText.replace("\n", " ")}\",${j.isVoiceLogged}\n")
        }

        return sb.toString()
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
