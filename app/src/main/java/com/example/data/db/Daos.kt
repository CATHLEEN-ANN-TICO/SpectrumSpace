package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_steps ORDER BY routineCategory, orderIndex ASC")
    fun getAllRoutineSteps(): Flow<List<RoutineStepEntity>>

    @Query("SELECT * FROM routine_steps WHERE routineCategory = :category ORDER BY orderIndex ASC")
    fun getStepsByCategory(category: String): Flow<List<RoutineStepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: RoutineStepEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<RoutineStepEntity>)

    @Update
    suspend fun updateStep(step: RoutineStepEntity)

    @Delete
    suspend fun deleteStep(step: RoutineStepEntity)

    @Query("UPDATE routine_steps SET isCompleted = :completed WHERE id = :id")
    suspend fun setStepCompleted(id: Long, completed: Boolean)

    @Query("UPDATE routine_steps SET isCompleted = 0 WHERE routineCategory = :category")
    suspend fun resetCategorySteps(category: String)

    @Query("UPDATE routine_steps SET isCompleted = 0")
    suspend fun resetAllSteps()
}

@Dao
interface CommCardDao {
    @Query("SELECT * FROM comm_cards ORDER BY isFavorite DESC, category, usageCount DESC")
    fun getAllCards(): Flow<List<CommCardEntity>>

    @Query("SELECT * FROM comm_cards WHERE category = :category ORDER BY isFavorite DESC, usageCount DESC")
    fun getCardsByCategory(category: String): Flow<List<CommCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CommCardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CommCardEntity>)

    @Update
    suspend fun updateCard(card: CommCardEntity)

    @Delete
    suspend fun deleteCard(card: CommCardEntity)

    @Query("UPDATE comm_cards SET usageCount = usageCount + 1 WHERE id = :id")
    suspend fun incrementCardUsage(id: Long)
}

@Dao
interface SensoryLogDao {
    @Query("SELECT * FROM sensory_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<SensoryLogEntity>>

    @Query("SELECT * FROM sensory_logs ORDER BY timestamp DESC LIMIT 1")
    fun getLatestLog(): Flow<SensoryLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SensoryLogEntity): Long

    @Query("DELETE FROM sensory_logs")
    suspend fun clearLogs()
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getSettings(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettingsEntity)
}
