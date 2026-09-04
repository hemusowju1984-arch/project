package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farms")
data class FarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmerName: String = "Ramesh Kumar",
    val farmName: String = "Green Valley Organic Farm",
    val location: String = "Kurnool, Andhra Pradesh",
    val cropType: String = "Cotton & Tomato",
    val areaAcres: Double = 12.5,
    val latitude: Double = 15.8281,
    val longitude: Double = 78.0373,
    val qrCodeId: String = "FARM-CC-98231"
)

@Entity(tableName = "sensor_logs")
data class SensorLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val soilMoisture: Int = 42, // %
    val soilTemp: Double = 26.4, // °C
    val soilPh: Double = 6.8,
    val nitrogen: Int = 140, // mg/kg
    val phosphorus: Int = 45, // mg/kg
    val potassium: Int = 190, // mg/kg
    val waterTankLevel: Int = 78, // %
    val pumpStatus: Boolean = false, // ON/OFF
    val airTemp: Double = 29.5, // °C
    val humidity: Int = 62 // %
)

@Entity(tableName = "disease_scans")
data class DiseaseScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isQualityGood: Boolean = true,
    val qualityErrorMessage: String = "",
    val confidencePercent: Int = 95,
    val isIdentified: Boolean = true,
    val confidenceErrorMessage: String = "",
    val isHealthy: Boolean = false,
    val cropName: String = "Crop Leaf",
    val growthStage: String = "Flowering & Fruiting Stage",
    val plantAge: String = "45 Days",
    val diseaseName: String = "Fungal Leaf Spot",
    val affectedAreaPercent: Int = 15,
    val severity: String = "Moderate",
    val affectedPart: String = "Leaf",
    val causes: String = "High ambient humidity (>75%), poor air circulation between dense leaves, and leaf wetness.",
    val symptoms: String = "Circular dark brown spots with concentric target rings and yellow halo rings.",
    val treatment: String = "Spray Mancozeb 75% WP @ 2.5g/L or Azoxystrobin 23% SC @ 1 ml/L.",
    val organicTreatment: String = "1. Spray Neem Oil (10,000 PPM) @ 3 ml/Liter water.\n2. Apply Trichoderma viride bio-fungicide @ 5g/L as soil drenching.",
    val chemicalTreatment: String = "1. Spray Mancozeb 75% WP @ 2.5g/L or Azoxystrobin 23% SC @ 1 ml/L.\n2. Repeat spray after 8-10 days.",
    val fungicidePesticide: String = "Mancozeb 75% WP / Azoxystrobin 23% SC",
    val recommendedFertilizers: String = "1. NPK 19-19-19 (Foliar Spray @ 5g/L)\n2. Calcium Nitrate @ 3g/L\n3. Micronutrient Spray (Boron + Zinc @ 1.5g/L)",
    val preventiveMeasures: String = "Implement proper crop rotation, maintain soil drainage, and use resistant seed varieties.",
    val expectedRecovery: String = "7 - 10 Days",
    val farmerAdvice: String = "Apply foliar sprays in late evening hours. Avoid overhead watering to prevent fungal spore dispersal.",
    val imageResName: String = "img_crop_leaf_disease"
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String = "Jul 26, 2026",
    val category: String = "General", // Seeds, Fertilizer, Pesticides, Labor, Machinery, Water, Electricity, Transport
    val title: String = "Farm Expense",
    val amount: Double = 0.0,
    val isIncome: Boolean = false, // false = expense, true = income
    val note: String = ""
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "Farm Task",
    val timeCategory: String = "Morning", // Morning, Afternoon, Evening
    val isCompleted: Boolean = false,
    val priority: String = "HIGH" // HIGH, MEDIUM, LOW
)
