package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CropCareViewModel
import com.example.ui.components.GlassmorphismCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.HarvestGold80
import com.example.ui.theme.OrganicPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WaterBlue

data class LiveMarketPrice(
    val category: String, // "Vegetables", "Grains & Cereals", "Cash Crops"
    val cropName: String,
    val mandiName: String,
    val pricePerQuintal: String,
    val changeTrend: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YieldPredictionScreen(
    viewModel: CropCareViewModel
) {
    val yieldResult by viewModel.yieldResult.collectAsState()
    val isLoading by viewModel.isYieldLoading.collectAsState()
    val esp32State by viewModel.esp32State.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val isTelugu = selectedLanguage.equals("Telugu", ignoreCase = true) || selectedLanguage.contains("తెలుగు")

    var cropType by remember { mutableStateOf(if (isTelugu) "పత్తి (బిటి కాటన్)" else "Cotton (Bt Cotton)") }
    var areaAcres by remember { mutableStateOf("12.5") }
    var seedVariety by remember { mutableStateOf(if (isTelugu) "బోల్‌గార్డ్ II హైబ్రిడ్" else "Bollgard II Hybrid") }
    var prevYield by remember { mutableStateOf(if (isTelugu) "ఎకరానికి 28 క్వింటాళ్లు" else "28 Quintals/Acre") }

    val cropsList = if (isTelugu) listOf(
        "పత్తి (బిటి కాటన్)",
        "గోధుమ (శర్బతి)",
        "వరి / ధాన్యం (BPT 5204)",
        "టమోటా (హైబ్రిడ్)",
        "బంగాళాదుంప (కుఫ్రి)",
        "ఉల్లిపాయ (నాసిక్ రెడ్)",
        "మిరప (తేజ)",
        "జొన్న / మొక్కజొన్న"
    ) else listOf(
        "Cotton (Bt Cotton)",
        "Wheat (Sharbati)",
        "Rice / Paddy (BPT 5204)",
        "Tomato (Hybrid)",
        "Potato (Kufri)",
        "Onion (Nashik Red)",
        "Chilli (Teja)",
        "Maize (Yellow Corn)"
    )
    var expandedCropDropdown by remember { mutableStateOf(false) }

    val marketPrices = listOf(
        // Vegetables
        LiveMarketPrice("Vegetables", "Tomato", "Madanapalle Mandi", "₹2,400 / Qtl", "+8.5% ↑"),
        LiveMarketPrice("Vegetables", "Potato", "Agra APMC", "₹1,850 / Qtl", "+2.4% ↑"),
        LiveMarketPrice("Vegetables", "Onion", "Nashik Yard", "₹2,200 / Qtl", "+4.1% ↑"),
        LiveMarketPrice("Vegetables", "Green Chilli", "Guntur APMC", "₹4,500 / Qtl", "+3.2% ↑"),
        // Grains & Cereals
        LiveMarketPrice("Grains", "Wheat (Sharbati)", "MP Central Mandi", "₹2,950 / Qtl", "+1.8% ↑"),
        LiveMarketPrice("Grains", "Rice / Paddy (BPT)", "Vijayawada APMC", "₹2,350 / Qtl", "+0.5% ↑"),
        LiveMarketPrice("Grains", "Maize (Yellow Corn)", "Kurnool APMC", "₹2,100 / Qtl", "+1.2% ↑"),
        // Cash Crops & Oilseeds
        LiveMarketPrice("Cash Crops", "Cotton (Bt Kapas)", "Kurnool Yard", "₹6,850 / Qtl", "+3.8% ↑"),
        LiveMarketPrice("Cash Crops", "Chilli (Teja)", "Guntur APMC", "₹18,500 / Qtl", "+2.1% ↑"),
        LiveMarketPrice("Oilseeds", "Soybean (Yellow)", "Indore APMC", "₹4,650 / Qtl", "+1.5% ↑"),
        LiveMarketPrice("Oilseeds", "Groundnut", "Rajkot Mandi", "₹6,200 / Qtl", "+2.0% ↑"),
        LiveMarketPrice("Cash Crops", "Sugarcane", "Kolhapur Yard", "₹3,150 / Ton", "+0.8% ↑")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Yield Prediction Header
        item {
            GlassmorphismCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isTelugu) "దిగుబడి అంచనా & వ్యవసాయ విశ్లేషణ" else "Yield Prediction & Farm Intelligence",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // ==================== 1. ALL LIVE SENSOR VALUES ====================
        item {
            Text(
                text = if (isTelugu) "1. లైవ్ IoT సెన్సార్ సమాచారం" else "1. Live IoT Farm Telemetry",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            GlassmorphismCard {
                // Row 1: Soil Moisture & Soil Temp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SensorTile(
                        title = if (isTelugu) "నేల తేమ" else "Soil Moisture",
                        value = "${esp32State.soilMoisturePercent}%",
                        status = if (isTelugu) "అనుకూల స్థాయి" else "Ideal Range",
                        icon = Icons.Default.WaterDrop,
                        tint = WaterBlue,
                        modifier = Modifier.weight(1f)
                    )
                    SensorTile(
                        title = if (isTelugu) "నేల ఉష్ణోగ్రత" else "Soil Temp",
                        value = "${esp32State.soilTemperature}°C",
                        status = if (isTelugu) "సాధారణం" else "Ambient Normal",
                        icon = Icons.Default.DeviceThermostat,
                        tint = HarvestGold80,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: Air Humidity & Water Tank Level
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SensorTile(
                        title = if (isTelugu) "గాలి తేమ" else "Air Humidity",
                        value = "${weather.humidityPercent}%",
                        status = if (isTelugu) "సాపేక్ష తేమ" else "Relative Moisture",
                        icon = Icons.Default.Opacity,
                        tint = WaterBlue,
                        modifier = Modifier.weight(1f)
                    )
                    SensorTile(
                        title = if (isTelugu) "నీటి ట్యాంక్" else "Water Tank",
                        value = "${esp32State.waterTankLevelPercent}%",
                        status = if (isTelugu) "5,000 లీటర్ల రిజర్వాయర్" else "5,000L Reservoir",
                        icon = Icons.Default.Opacity,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 3: Soil pH & Rain Sensor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SensorTile(
                        title = if (isTelugu) "నేల pH స్థాయి" else "Soil pH Level",
                        value = "6.8 pH",
                        status = if (isTelugu) "అనుకూలమైన నేల" else "Optimal Soil",
                        icon = Icons.Default.Science,
                        tint = OrganicPurple,
                        modifier = Modifier.weight(1f)
                    )
                    SensorTile(
                        title = if (isTelugu) "వర్షపాత సెన్సార్" else "Rain Sensor",
                        value = if (isTelugu) "వర్షం లేదు" else "No Rain",
                        status = if (isTelugu) "0.0 mm/hr (స్పష్టం)" else "0.0 mm/hr (Clear)",
                        icon = Icons.Default.WbSunny,
                        tint = HarvestGold80,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // NPK Sensor Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(OrganicPurple.copy(alpha = 0.14f))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Science,
                                contentDescription = null,
                                tint = OrganicPurple,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isTelugu) "నేల NPK పోషకాల స్థాయి" else "Soil NPK Nutrients Chemistry",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OrganicPurple.copy(alpha = 0.22f))
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(if (isTelugu) "నైట్రోజన్ (N)" else "Nitrogen (N)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrganicPurple)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text("${esp32State.nitrogen} mg/kg", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OrganicPurple.copy(alpha = 0.22f))
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(if (isTelugu) "భాస్వరం (P)" else "Phosphorus (P)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrganicPurple)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text("${esp32State.phosphorus} mg/kg", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OrganicPurple.copy(alpha = 0.22f))
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(if (isTelugu) "పొటాషియం (K)" else "Potassium (K)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrganicPurple)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text("${esp32State.potassium} mg/kg", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== 2. CROP DETAILS & YIELD PREDICTION ====================
        item {
            Text(
                text = if (isTelugu) "2. పంట వివరాలు & దిగుబడి అంచనా" else "2. Crop Details & Yield Prediction",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            GlassmorphismCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expandedCropDropdown,
                        onExpandedChange = { expandedCropDropdown = !expandedCropDropdown }
                    ) {
                        OutlinedTextField(
                            value = cropType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isTelugu) "పంట రకం" else "Crop Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCropDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCropDropdown,
                            onDismissRequest = { expandedCropDropdown = false }
                        ) {
                            cropsList.forEach { crop ->
                                DropdownMenuItem(
                                    text = { Text(crop) },
                                    onClick = {
                                        cropType = crop
                                        expandedCropDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = areaAcres,
                        onValueChange = { areaAcres = it },
                        label = { Text(if (isTelugu) "వైశాల్యం (ఎకరాలలో)" else "Area (Acres)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = seedVariety,
                        onValueChange = { seedVariety = it },
                        label = { Text(if (isTelugu) "విత్తన రకం" else "Seed Variety") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = prevYield,
                        onValueChange = { prevYield = it },
                        label = { Text(if (isTelugu) "గత సీజన్ దిగుబడి" else "Previous Season Yield") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val acresVal = areaAcres.toDoubleOrNull() ?: 12.5
                            viewModel.predictYield(cropType, acresVal, seedVariety, "${esp32State.nitrogen}:${esp32State.phosphorus}:${esp32State.potassium}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTelugu) "దిగుబడి అంచనా వేయండి" else "Calculate Yield Prediction", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // ==================== YIELD PREDICTION RESULT ====================
        yieldResult?.let { res ->
            item {
                GlassmorphismCard(
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTelugu) "దిగుబడి అంచనా ఫలితం" else "Yield Prediction Forecast",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        StatusBadge(text = if (isTelugu) "ప్రమాదం: ${res.riskScore}" else "Risk: ${res.riskScore}", badgeColor = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FinancialMetricCard(
                            if (isTelugu) "ఆశించిన దిగుబడి" else "Expected Yield",
                            if (isTelugu) "${res.expectedYieldQuintals} క్వింటాళ్లు" else "${res.expectedYieldQuintals} Quintals",
                            MaterialTheme.colorScheme.primary
                        )
                        FinancialMetricCard(
                            if (res.estimatedProfit >= 0) {
                                if (isTelugu) "అంచనా నికర లాభం" else "Net Estimated Profit"
                            } else {
                                if (isTelugu) "అంచనా నికర నష్టం" else "Net Estimated Loss"
                            },
                            "₹${res.estimatedProfit.toInt()}",
                            if (res.estimatedProfit >= 0) SuccessGreen else AlertRed
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoLabel(if (isTelugu) "కోత సమయం" else "Harvest Window", res.harvestDate, Icons.Default.DateRange)
                        InfoLabel(if (isTelugu) "మొత్తం ఆదాయం" else "Gross Income", "₹${res.estimatedIncome.toInt()}", Icons.Default.CurrencyRupee)
                        InfoLabel(if (isTelugu) "ఉత్పత్తి ఖర్చు" else "Production Cost", "₹${res.estimatedExpenses.toInt()}", Icons.Default.ShowChart)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Profit / Loss Financial Statement
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (res.estimatedProfit >= 0) SuccessGreen.copy(alpha = 0.14f) else AlertRed.copy(alpha = 0.14f))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = if (res.estimatedProfit >= 0) SuccessGreen else AlertRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (res.estimatedProfit >= 0) {
                                            if (isTelugu) "వ్యవసాయ లాభాల వివరాలు" else "FARM PROFIT SUMMARY"
                                        } else {
                                            if (isTelugu) "వ్యవసాయ నష్టాల వివరాలు" else "FARM LOSS SUMMARY"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (res.estimatedProfit >= 0) SuccessGreen else AlertRed
                                    )
                                }
                                Text(
                                    text = "₹${res.estimatedProfit.toInt()}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (res.estimatedProfit >= 0) SuccessGreen else AlertRed
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LedgerItemRow(if (isTelugu) "ఆశించిన రాబడి" else "Expected Revenue", "₹${res.estimatedIncome.toInt()}", isPositive = true)
                            Spacer(modifier = Modifier.height(4.dp))
                            LedgerItemRow(if (isTelugu) "మొత్తం ఉత్పత్తి ఖర్చులు" else "Total Production Expenses", "₹${res.estimatedExpenses.toInt()}", isPositive = false)
                        }
                    }
                }
            }
        }

        // ==================== 3. LIVE MARKET PRICES ====================
        item {
            Text(
                text = if (isTelugu) "3. ప్రత్యక్ష మార్కెట్ ధరలు (మండీలు)" else "3. Live Mandi Market Prices",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            GlassmorphismCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTelugu) "లైవ్ APMC మార్కెట్ ధరలు" else "Real-Time APMC Mandi Rates",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                marketPrices.forEach { item ->
                    MarketPriceRow(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun SensorTile(
    title: String,
    value: String,
    status: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(tint.copy(alpha = 0.12f))
            .padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = tint)
            Text(text = status, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun MarketPriceRow(item: LiveMarketPrice) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.cropName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = item.category, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Text(text = item.mandiName, fontSize = 11.sp, color = Color.Gray)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(text = item.pricePerQuintal, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            Text(
                text = item.changeTrend,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.changeTrend.contains("+")) SuccessGreen else AlertRed
            )
        }
    }
}

@Composable
fun LedgerItemRow(title: String, amount: String, isPositive: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
        Text(
            text = amount,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPositive) SuccessGreen else AlertRed
        )
    }
}

@Composable
fun FinancialMetricCard(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        }
    }
}

@Composable
fun InfoLabel(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = title, fontSize = 10.sp, color = Color.Gray)
        }
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
