package com.example.data.repository

import com.example.data.api.AgricultureWeatherInfo
import com.example.data.api.Esp32DeviceState
import com.example.data.api.Esp32SimulationService
import com.example.data.api.GeminiChatService
import com.example.data.api.IrrigationMode
import com.example.data.api.WeatherService
import com.example.data.db.AppDatabase
import com.example.data.db.DiseaseScanEntity
import com.example.data.db.ExpenseEntity
import com.example.data.db.FarmEntity
import com.example.data.db.SensorLogEntity
import com.example.data.db.TaskEntity
import kotlinx.coroutines.flow.Flow

class CropCareRepository(private val db: AppDatabase) {

    // Database Flows
    val primaryFarm: Flow<FarmEntity?> = db.farmDao().getPrimaryFarm()
    val allScans: Flow<List<DiseaseScanEntity>> = db.diseaseScanDao().getAllScans()
    val allExpenses: Flow<List<ExpenseEntity>> = db.expenseDao().getAllExpenses()
    val allTasks: Flow<List<TaskEntity>> = db.taskDao().getAllTasks()
    val sensorHistory: Flow<List<SensorLogEntity>> = db.sensorDao().getSensorHistory()

    suspend fun saveFarm(farm: FarmEntity) = db.farmDao().insertFarm(farm)
    suspend fun updateFarm(farm: FarmEntity) = db.farmDao().updateFarm(farm)

    suspend fun recordDiseaseScan(scan: DiseaseScanEntity) = db.diseaseScanDao().insertScan(scan)
    suspend fun addExpense(expense: ExpenseEntity) = db.expenseDao().insertExpense(expense)
    suspend fun deleteExpense(id: Long) = db.expenseDao().deleteExpense(id)
    suspend fun addTask(task: TaskEntity) = db.taskDao().insertTask(task)
    suspend fun updateTaskStatus(taskId: Long, completed: Boolean) = db.taskDao().updateTaskStatus(taskId, completed)

    // ESP32 Telemetry
    fun getEsp32State(): Esp32DeviceState = Esp32SimulationService.getDeviceState()
    fun togglePump(active: Boolean): Esp32DeviceState = Esp32SimulationService.setPumpStatus(active)
    fun setIrrigationMode(mode: IrrigationMode): Esp32DeviceState = Esp32SimulationService.setIrrigationMode(mode)
    fun getLiveSensorStream(): Flow<SensorLogEntity> = Esp32SimulationService.liveTelemetryStream()

    // Weather
    fun getWeather(): AgricultureWeatherInfo = WeatherService.getCurrentAgriWeather()

    // Gemini Chatbot
    suspend fun askGemini(prompt: String, language: String, context: String): String {
        return GeminiChatService.generateAgricultureResponse(prompt, language, context)
    }

    // Seed initial demo data if database is empty
    suspend fun seedInitialDataIfEmpty() {
        // Seed default farm
        db.farmDao().insertFarm(
            FarmEntity(
                farmerName = "Ramesh Kumar",
                farmName = "Green Valley Organic Farm",
                location = "Kurnool, Andhra Pradesh",
                cropType = "Cotton & Tomato",
                areaAcres = 12.5
            )
        )

        // Seed initial tasks
        db.taskDao().insertTask(TaskEntity(title = "Inspect drip irrigation filters in Sector B", timeCategory = "Morning", priority = "HIGH"))
        db.taskDao().insertTask(TaskEntity(title = "Apply Neem Oil spray on Tomato bed 4", timeCategory = "Afternoon", priority = "MEDIUM"))
        db.taskDao().insertTask(TaskEntity(title = "Check water tank refill valve", timeCategory = "Evening", priority = "LOW"))

        // Seed initial expenses
        db.expenseDao().insertExpense(ExpenseEntity(date = "Jul 24, 2026", category = "Fertilizer", title = "Neem Coated Urea (5 bags)", amount = 1420.0, isIncome = false))
        db.expenseDao().insertExpense(ExpenseEntity(date = "Jul 22, 2026", category = "Labor", title = "Weeding labor 4 workers", amount = 2400.0, isIncome = false))
        db.expenseDao().insertExpense(ExpenseEntity(date = "Jul 20, 2026", category = "Market Sale", title = "Cotton harvest 12 quintals sale", amount = 77400.0, isIncome = true))
    }
}
