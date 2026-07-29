package com.example.data.api

import com.example.data.db.SensorLogEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

enum class IrrigationMode {
    MANUAL,
    AUTOMATIC,
    AI_SMART
}

data class Esp32DeviceState(
    val deviceId: String = "ESP32-AGRI-PUMP-01",
    val isConnected: Boolean = true,
    val wifiSignalDbm: Int = -62,
    val firmwareVersion: String = "v2.4.1-IoT",
    val pumpRelayActive: Boolean = false,
    val mode: IrrigationMode = IrrigationMode.AI_SMART,
    val waterTankLevelPercent: Int = 78, // %
    val soilMoisturePercent: Int = 42, // %
    val soilTemperature: Double = 26.4, // °C
    val soilPh: Double = 6.8,
    val nitrogen: Int = 140, // mg/kg
    val phosphorus: Int = 45, // mg/kg
    val potassium: Int = 190, // mg/kg
    val lastIrrigationTime: String = "Today, 06:30 AM (20 mins)",
    val nextScheduledIrrigation: String = "Today, 06:00 PM (Smart AI)",
    val totalWaterUsedTodayLiters: Int = 340
)

object Esp32SimulationService {
    private var currentState = Esp32DeviceState()

    fun getDeviceState(): Esp32DeviceState = currentState

    fun setPumpStatus(active: Boolean): Esp32DeviceState {
        currentState = currentState.copy(
            pumpRelayActive = active,
            lastIrrigationTime = if (active) "Running now..." else "Just finished"
        )
        return currentState
    }

    fun setIrrigationMode(mode: IrrigationMode): Esp32DeviceState {
        currentState = currentState.copy(mode = mode)
        return currentState
    }

    fun liveTelemetryStream(): Flow<SensorLogEntity> = flow {
        while (true) {
            val moistureFluctuation = Random.nextInt(-1, 2)
            val tempFluctuation = Random.nextDouble(-0.2, 0.2)
            
            val newMoisture = (currentState.soilMoisturePercent + moistureFluctuation).coerceIn(20, 95)
            val newTemp = ((currentState.soilTemperature + tempFluctuation) * 10).toInt() / 10.0

            currentState = currentState.copy(
                soilMoisturePercent = newMoisture,
                soilTemperature = newTemp
            )

            val log = SensorLogEntity(
                soilMoisture = newMoisture,
                soilTemp = newTemp,
                soilPh = currentState.soilPh,
                nitrogen = currentState.nitrogen,
                phosphorus = currentState.phosphorus,
                potassium = currentState.potassium,
                waterTankLevel = currentState.waterTankLevelPercent,
                pumpStatus = currentState.pumpRelayActive
            )
            emit(log)
            delay(5000) // update telemetry every 5s
        }
    }
}
