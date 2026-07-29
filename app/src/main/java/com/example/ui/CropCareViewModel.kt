package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AgricultureWeatherInfo
import com.example.data.api.Esp32DeviceState
import com.example.data.api.GeminiVisionService
import com.example.data.api.IrrigationMode
import com.example.data.db.AppDatabase
import com.example.data.db.DiseaseScanEntity
import com.example.data.db.ExpenseEntity
import com.example.data.db.TaskEntity
import com.example.data.repository.CropCareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "Just now",
    val isVoicePlaying: Boolean = false
)

data class YieldPredictionResult(
    val expectedYieldQuintals: Double = 32.4,
    val harvestDate: String = "Oct 18, 2026",
    val estimatedIncome: Double = 208980.0,
    val estimatedExpenses: Double = 48500.0,
    val estimatedProfit: Double = 160480.0,
    val riskScore: String = "LOW (14/100)",
    val suggestions: List<String> = listOf(
        "Maintain current drip irrigation schedule of 25 mins daily",
        "Apply split dose of Potassium Nitrate in 10 days",
        "Monitor for bollworm activity as humidity stays high"
    )
)

data class MarketPriceItem(
    val cropName: String,
    val mandiName: String,
    val pricePerQuintal: Int,
    val changeTrend: String,
    val isBestSellingTime: Boolean = false
)

data class FarmNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: String = "Just now",
    val type: String = "ALERT", // IRRIGATION, DISEASE, WEATHER, SYSTEM
    val isRead: Boolean = false
)

class CropCareViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = CropCareRepository(db)
    private val prefs = application.getSharedPreferences("CropCarePrefs", Context.MODE_PRIVATE)

    // Auth & User Profile State
    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", true))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _farmerName = MutableStateFlow(prefs.getString("farmer_name", "Ramesh Kumar") ?: "Ramesh Kumar")
    val farmerName: StateFlow<String> = _farmerName.asStateFlow()

    private val _farmerPhone = MutableStateFlow(prefs.getString("farmer_phone", "+91 98765 43210") ?: "+91 98765 43210")
    val farmerPhone: StateFlow<String> = _farmerPhone.asStateFlow()

    private val _farmerLocation = MutableStateFlow(prefs.getString("farmer_location", "Kurnool, Andhra Pradesh") ?: "Kurnool, Andhra Pradesh")
    val farmerLocation: StateFlow<String> = _farmerLocation.asStateFlow()

    private val _farmName = MutableStateFlow(prefs.getString("farm_name", "Green Valley Organic Farm") ?: "Green Valley Organic Farm")
    val farmName: StateFlow<String> = _farmName.asStateFlow()

    private val _farmSizeAcres = MutableStateFlow(prefs.getString("farm_size", "12.5 Acres") ?: "12.5 Acres")
    val farmSizeAcres: StateFlow<String> = _farmSizeAcres.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(prefs.getString("selected_language", "English") ?: "English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // ESP32 & Sensors State
    private val _esp32State = MutableStateFlow(repository.getEsp32State())
    val esp32State: StateFlow<Esp32DeviceState> = _esp32State.asStateFlow()

    // Notifications State
    private val _notifications = MutableStateFlow<List<FarmNotification>>(
        listOf(
            FarmNotification(
                title = "Irrigation System Ready",
                message = "ESP32 IoT sensor grid online. Soil moisture: 42%.",
                timestamp = "10 mins ago",
                type = "IRRIGATION"
            ),
            FarmNotification(
                title = "Weather Rain Alert",
                message = "Moderate rain predicted on Wednesday (+22mm). Hold nitrogen fertilizer application.",
                timestamp = "1 hr ago",
                type = "WEATHER"
            )
        )
    )
    val notifications: StateFlow<List<FarmNotification>> = _notifications.asStateFlow()

    // Database Flows
    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val expenses: StateFlow<List<ExpenseEntity>> = _expenses.asStateFlow()

    private val _diseaseScans = MutableStateFlow<List<DiseaseScanEntity>>(emptyList())
    val diseaseScans: StateFlow<List<DiseaseScanEntity>> = _diseaseScans.asStateFlow()

    // Weather
    private val _weather = MutableStateFlow(repository.getWeather())
    val weather: StateFlow<AgricultureWeatherInfo> = _weather.asStateFlow()

    // AI Chatbot State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "CropCare AI",
                text = "Namaste Ramesh Ji! I am your AI Agriculture Assistant. Ask me about your crop diseases, soil NPK values, irrigation schedule, or government schemes.",
                isUser = false,
                timestamp = "09:00 AM"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Yield Prediction State
    private val _yieldResult = MutableStateFlow<YieldPredictionResult?>(YieldPredictionResult())
    val yieldResult: StateFlow<YieldPredictionResult?> = _yieldResult.asStateFlow()

    private val _isYieldLoading = MutableStateFlow(false)
    val isYieldLoading: StateFlow<Boolean> = _isYieldLoading.asStateFlow()

    // Market Prices
    val marketPrices = MutableStateFlow(
        listOf(
            MarketPriceItem("Cotton (Kapas)", "Kurnool APMC", 6850, "+3.8% ↑", isBestSellingTime = true),
            MarketPriceItem("Tomato", "Madanapalle Mandi", 2400, "+8.5% ↑", isBestSellingTime = true),
            MarketPriceItem("Red Gram (Toor)", "Guntur Mandi", 8200, "-0.5% ↓", isBestSellingTime = false),
            MarketPriceItem("Chilli (Teja)", "Guntur Yard", 18500, "+2.1% ↑", isBestSellingTime = true),
            MarketPriceItem("Paddy (BPT)", "Vijayawada APMC", 2280, "0.0% = ", isBestSellingTime = false)
        )
    )

    // Disease Scan active result
    private val _latestScanResult = MutableStateFlow<DiseaseScanEntity?>(null)
    val latestScanResult: StateFlow<DiseaseScanEntity?> = _latestScanResult.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }

        viewModelScope.launch {
            repository.allTasks.collectLatest { taskList ->
                _tasks.value = taskList
            }
        }

        viewModelScope.launch {
            repository.allExpenses.collectLatest { expList ->
                _expenses.value = expList
            }
        }

        viewModelScope.launch {
            repository.allScans.collectLatest { scanList ->
                _diseaseScans.value = scanList
                if (_latestScanResult.value == null) {
                    _latestScanResult.value = scanList.firstOrNull() ?: generateScanResult("Tomato Leaf")
                }
            }
        }

        // Collect live sensor stream from ESP32 simulation & handle Automated Irrigation
        viewModelScope.launch {
            repository.getLiveSensorStream().collectLatest { log ->
                val current = _esp32State.value
                val newMoisture = log.soilMoisture

                // Automated Irrigation logic:
                var pumpActive = current.pumpRelayActive
                if (current.mode == IrrigationMode.AUTOMATIC || current.mode == IrrigationMode.AI_SMART) {
                    if (newMoisture < 35 && !pumpActive) {
                        pumpActive = true
                        addNotification(
                            title = "Motor Auto-Started",
                            message = "Low soil moisture detected ($newMoisture%). Drip motor pump activated automatically.",
                            type = "IRRIGATION"
                        )
                    } else if (newMoisture >= 65 && pumpActive) {
                        pumpActive = false
                        addNotification(
                            title = "Motor Auto-Stopped",
                            message = "Optimal soil moisture reached ($newMoisture%). Irrigation pump shut down.",
                            type = "IRRIGATION"
                        )
                    }
                }

                _esp32State.value = current.copy(
                    soilMoisturePercent = newMoisture,
                    soilTemperature = log.soilTemp,
                    soilPh = log.soilPh,
                    nitrogen = log.nitrogen,
                    phosphorus = log.phosphorus,
                    potassium = log.potassium,
                    waterTankLevelPercent = log.waterTankLevel,
                    pumpRelayActive = pumpActive
                )
            }
        }
    }

    // Auth & Profile actions
    fun login(phone: String, name: String) {
        _farmerName.value = name
        _farmerPhone.value = phone
        _isLoggedIn.value = true
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("farmer_name", name)
            .putString("farmer_phone", phone)
            .apply()
    }

    fun updateFarmerProfile(name: String, phone: String, location: String, farm: String, size: String) {
        _farmerName.value = name
        _farmerPhone.value = phone
        _farmerLocation.value = location
        _farmName.value = farm
        _farmSizeAcres.value = size

        prefs.edit()
            .putString("farmer_name", name)
            .putString("farmer_phone", phone)
            .putString("farmer_location", location)
            .putString("farm_name", farm)
            .putString("farm_size", size)
            .apply()
    }

    fun logout() {
        _isLoggedIn.value = false
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }

    fun toggleDarkMode() {
        val next = !_isDarkMode.value
        _isDarkMode.value = next
        prefs.edit().putBoolean("is_dark_mode", next).apply()
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
        prefs.edit().putString("selected_language", lang).apply()
    }

    // Notifications Action
    fun addNotification(title: String, message: String, type: String = "ALERT") {
        val newNotif = FarmNotification(title = title, message = message, type = type)
        _notifications.value = listOf(newNotif) + _notifications.value
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    // Pump Control actions
    fun togglePump(active: Boolean) {
        _esp32State.value = repository.togglePump(active)
        val title = if (active) "Motor Pump Started" else "Motor Pump Stopped"
        val msg = if (active) "Irrigation pump turned ON manually by farmer." else "Irrigation pump turned OFF manually."
        addNotification(title, msg, "IRRIGATION")
    }

    fun setIrrigationMode(mode: IrrigationMode) {
        _esp32State.value = repository.setIrrigationMode(mode)
    }

    // Tasks actions
    fun toggleTask(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.updateTaskStatus(taskId, completed)
        }
    }

    fun addNewTask(title: String, timeCat: String, priority: String) {
        viewModelScope.launch {
            repository.addTask(TaskEntity(title = title, timeCategory = timeCat, priority = priority))
        }
    }

    // Expense actions
    fun addExpenseItem(title: String, category: String, amount: Double, isIncome: Boolean) {
        viewModelScope.launch {
            val dateStr = "Jul 26, 2026"
            repository.addExpense(ExpenseEntity(date = dateStr, category = category, title = title, amount = amount, isIncome = isIncome))
        }
    }

    fun deleteExpenseItem(id: Long) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    // AI Disease Scan action with Gemini Vision API
    fun analyzeCropImageBitmap(bitmap: Bitmap, cropHint: String = "Tomato Leaf") {
        viewModelScope.launch {
            _isScanning.value = true
            val visionService = GeminiVisionService(getApplication())
            val scan = visionService.analyzeCropImage(bitmap, cropHint, _selectedLanguage.value)
            repository.recordDiseaseScan(scan)
            _latestScanResult.value = scan
            _isScanning.value = false

            if (!scan.isQualityGood) {
                addNotification("Image Quality Error", scan.qualityErrorMessage, "DISEASE")
            } else if (!scan.isHealthy) {
                addNotification("Disease Detected", "${scan.cropName}: ${scan.diseaseName} (${scan.severity} Severity)", "DISEASE")
            }
        }
    }

    fun generateScanResult(cropName: String): DiseaseScanEntity {
        val isTomato = cropName.contains("Tomato", ignoreCase = true)
        val isCotton = cropName.contains("Cotton", ignoreCase = true)
        val isChilli = cropName.contains("Chilli", ignoreCase = true)
        val isPaddy = cropName.contains("Paddy", ignoreCase = true) || cropName.contains("Rice", ignoreCase = true)

        val cropTitle = when {
            isTomato -> "Tomato Crop"
            isCotton -> "Cotton Crop"
            isChilli -> "Chilli Crop"
            isPaddy -> "Paddy/Rice Crop"
            else -> "$cropName Leaf"
        }

        val diseaseTitle = when {
            isTomato -> "Tomato Early Blight (Alternaria solani)"
            isCotton -> "Cotton Bacterial Blight & Leaf Curl"
            isChilli -> "Chilli Leaf Curl & Powdery Mildew"
            isPaddy -> "Rice Blast & Brown Leaf Spot"
            else -> "Fungal Leaf Spot & Blight"
        }

        return DiseaseScanEntity(
            cropName = cropTitle,
            diseaseName = diseaseTitle,
            confidencePercent = 95,
            affectedAreaPercent = 18,
            severity = "Moderate",
            causes = "High ambient humidity (>75%), poor air circulation between dense leaves, and prolonged leaf wetness.",
            symptoms = "Circular dark brown spots with concentric target rings, yellow halo rings, and brown leaf tip wilting.",
            treatment = "Spray Mancozeb 75% WP @ 2.5g/L or Azoxystrobin 23% SC @ 1 ml/L.",
            organicTreatment = "1. Spray Neem Oil (10,000 PPM) @ 3 ml/Liter water.\n2. Apply Trichoderma viride bio-fungicide @ 5g/L as soil drenching.",
            chemicalTreatment = "Spray Mancozeb 75% WP @ 2.5g/L or Azoxystrobin 23% SC @ 1 ml/L. Repeat spray after 8-10 days.",
            fungicidePesticide = "Mancozeb 75% WP / Azoxystrobin 23% SC",
            recommendedFertilizers = "1. NPK 19-19-19 (Foliar Spray @ 5g/L)\n2. Calcium Nitrate @ 3g/L\n3. Micronutrient Spray (Boron + Zinc @ 1.5g/L)",
            expectedRecovery = "7 - 10 Days"
        )
    }

    fun analyzeCropImage(cropName: String) {
        viewModelScope.launch {
            _isScanning.value = true
            kotlinx.coroutines.delay(400)
            val scan = generateScanResult(cropName)
            repository.recordDiseaseScan(scan)
            _latestScanResult.value = scan
            _isScanning.value = false
        }
    }

    // Yield Prediction calculation
    fun predictYield(crop: String, acres: Double, seedVariety: String, npkRatio: String) {
        viewModelScope.launch {
            _isYieldLoading.value = true
            kotlinx.coroutines.delay(1200)
            val baseYieldPerAcre = if (crop.contains("Cotton", ignoreCase = true)) 2.6 else 4.2
            val totalYield = (acres * baseYieldPerAcre * 10).toInt() / 10.0
            val estInc = totalYield * 6800.0
            val estExp = acres * 3800.0
            val estProf = estInc - estExp

            _yieldResult.value = YieldPredictionResult(
                expectedYieldQuintals = totalYield,
                harvestDate = "Oct 24, 2026",
                estimatedIncome = estInc,
                estimatedExpenses = estExp,
                estimatedProfit = estProf,
                riskScore = "LOW (12/100)",
                suggestions = listOf(
                    "Seed variety '$seedVariety' shows excellent vigor with current soil NPK ($npkRatio)",
                    "Recommended soil moisture during boll/fruit filling phase: 50-60%",
                    "Apply micronutrient Zinc Sulphate @ 10kg/acre before flowering stage"
                )
            )
            _isYieldLoading.value = false
        }
    }

    // Chatbot sending
    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(sender = "Farmer", text = userText, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val context = "Soil Moisture: ${_esp32State.value.soilMoisturePercent}%, Temp: ${_esp32State.value.soilTemperature}°C, pH: ${_esp32State.value.soilPh}, NPK: 140-45-190"
            val aiResponseText = repository.askGemini(userText, _selectedLanguage.value, context)
            val aiMsg = ChatMessage(sender = "CropCare AI", text = aiResponseText, isUser = false)
            _chatMessages.value = _chatMessages.value + aiMsg
            _isChatLoading.value = false
        }
    }
}
