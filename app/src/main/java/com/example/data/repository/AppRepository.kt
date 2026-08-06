package com.example.data.repository

import com.example.data.db.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val database: AppDatabase) {

    // Routine Steps
    fun getAllRoutineSteps(): Flow<List<RoutineStepEntity>> = database.routineDao().getAllRoutineSteps()
    fun getStepsByCategory(category: String): Flow<List<RoutineStepEntity>> = database.routineDao().getStepsByCategory(category)
    suspend fun insertStep(step: RoutineStepEntity) = database.routineDao().insertStep(step)
    suspend fun updateStep(step: RoutineStepEntity) = database.routineDao().updateStep(step)
    suspend fun deleteStep(step: RoutineStepEntity) = database.routineDao().deleteStep(step)
    suspend fun setStepCompleted(id: Long, completed: Boolean) = database.routineDao().setStepCompleted(id, completed)
    suspend fun resetCategorySteps(category: String) = database.routineDao().resetCategorySteps(category)
    suspend fun resetAllSteps() = database.routineDao().resetAllSteps()

    // Comm Cards
    fun getAllCards(): Flow<List<CommCardEntity>> = database.commCardDao().getAllCards()
    fun getCardsByCategory(category: String): Flow<List<CommCardEntity>> = database.commCardDao().getCardsByCategory(category)
    suspend fun insertCard(card: CommCardEntity) = database.commCardDao().insertCard(card)
    suspend fun updateCard(card: CommCardEntity) = database.commCardDao().updateCard(card)
    suspend fun deleteCard(card: CommCardEntity) = database.commCardDao().deleteCard(card)
    suspend fun incrementCardUsage(id: Long) = database.commCardDao().incrementCardUsage(id)

    // Sensory Logs
    fun getAllSensoryLogs(): Flow<List<SensoryLogEntity>> = database.sensoryLogDao().getAllLogs()
    fun getLatestSensoryLog(): Flow<SensoryLogEntity?> = database.sensoryLogDao().getLatestLog()
    suspend fun insertSensoryLog(log: SensoryLogEntity) = database.sensoryLogDao().insertLog(log)
    suspend fun clearSensoryLogs() = database.sensoryLogDao().clearLogs()

    // Settings
    fun getUserSettings(): Flow<UserSettingsEntity?> = database.userSettingsDao().getSettings()
    suspend fun saveUserSettings(settings: UserSettingsEntity) = database.userSettingsDao().saveSettings(settings)
}
