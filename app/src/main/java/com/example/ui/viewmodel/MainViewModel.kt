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

    fun addCustomStep(category: String, title: String, description: String, durationMinutes: Int, iconName: String = "CheckCircle") {
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
                    orderIndex = maxIndex + 1
                )
            )
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

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
