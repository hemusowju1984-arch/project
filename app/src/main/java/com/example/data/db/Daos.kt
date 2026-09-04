package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {
    @Query("SELECT * FROM farms LIMIT 1")
    fun getPrimaryFarm(): Flow<FarmEntity?>

    @Query("SELECT * FROM farms")
    fun getAllFarms(): Flow<List<FarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarm(farm: FarmEntity)

    @Update
    suspend fun updateFarm(farm: FarmEntity)
}

@Dao
interface SensorDao {
    @Query("SELECT * FROM sensor_logs ORDER BY timestamp DESC LIMIT 1")
    fun getLatestSensorLog(): Flow<SensorLogEntity?>

    @Query("SELECT * FROM sensor_logs ORDER BY timestamp DESC LIMIT 30")
    fun getSensorHistory(): Flow<List<SensorLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensorLog(log: SensorLogEntity)
}

@Dao
interface DiseaseScanDao {
    @Query("SELECT * FROM disease_scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<DiseaseScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: DiseaseScanEntity)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY id DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpense(id: Long)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Long, completed: Boolean)
}
