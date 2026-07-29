package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.CropCareViewModel
import com.example.ui.components.GlassmorphismCard
import com.example.ui.components.MetricGaugeCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ForestGreen40
import com.example.ui.theme.ForestGreen80
import com.example.ui.theme.HarvestGold80
import com.example.ui.theme.OrganicPurple
import com.example.ui.theme.WaterBlue
import com.example.util.AppLocalization

@Composable
fun HomeScreen(
    viewModel: CropCareViewModel,
    onNavigateToIrrigation: () -> Unit,
    onNavigateToDiseaseScan: () -> Unit,
    onNavigateToYield: () -> Unit,
    onNavigateToChatbot: () -> Unit
) {
    val farmerName by viewModel.farmerName.collectAsState()
    val farmerLocation by viewModel.farmerLocation.collectAsState()
    val esp32State by viewModel.esp32State.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // Header Profile & Location
            GlassmorphismCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${AppLocalization.tr("welcome", selectedLanguage)}, $farmerName",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusBadge(
                                text = "Pro Farmer",
                                badgeColor = HarvestGold80
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = farmerLocation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Health Score Badge
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(ForestGreen40, ForestGreen80)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "92%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = AppLocalization.tr("health_status", selectedLanguage),
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Hero Smart Farm Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_farm_1785053782543),
                    contentDescription = "Farm Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ESP32 IoT Sensor Grid: ONLINE",
                                color = HarvestGold80,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = AppLocalization.tr("nav_irrigation", selectedLanguage),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = onNavigateToIrrigation,
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen40),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(AppLocalization.tr("pump_on", selectedLanguage))
                        }
                    }
                }
            }
        }

        // Live Weather Widget
        item {
            GlassmorphismCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.WbSunny,
                            contentDescription = "Weather",
                            tint = HarvestGold80,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "${weather.currentTemp}°C • ${weather.condition}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "High: ${weather.maxTemp}°C  Low: ${weather.minTemp}°C",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Opacity,
                                contentDescription = null,
                                tint = WaterBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${weather.humidityPercent}% ${AppLocalization.tr("humidity", selectedLanguage)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Air,
                                contentDescription = null,
                                tint = ForestGreen80,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${weather.windSpeedKmH} km/h",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // ESP32 Live Telemetry Gauge Grid
        item {
            Text(
                text = "Live ESP32 IoT Sensors",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricGaugeCard(
                    title = AppLocalization.tr("soil_moisture", selectedLanguage),
                    value = "${esp32State.soilMoisturePercent}",
                    unit = "%",
                    subtitle = "Optimal Range (40-60%)",
                    percentage = esp32State.soilMoisturePercent / 100f,
                    gaugeColor = WaterBlue,
                    icon = Icons.Default.WaterDrop,
                    modifier = Modifier.weight(1f)
                )

                MetricGaugeCard(
                    title = AppLocalization.tr("water_tank", selectedLanguage),
                    value = "${esp32State.waterTankLevelPercent}",
                    unit = "%",
                    subtitle = "Tank Level Good",
                    percentage = esp32State.waterTankLevelPercent / 100f,
                    gaugeColor = ForestGreen80,
                    icon = Icons.Default.Opacity,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricGaugeCard(
                    title = AppLocalization.tr("soil_temp", selectedLanguage),
                    value = "${esp32State.soilTemperature}",
                    unit = "°C",
                    subtitle = "Normal Root Temp",
                    percentage = (esp32State.soilTemperature / 45.0).toFloat(),
                    gaugeColor = HarvestGold80,
                    icon = Icons.Default.DeviceThermostat,
                    modifier = Modifier.weight(1f)
                )

                MetricGaugeCard(
                    title = AppLocalization.tr("soil_ph", selectedLanguage),
                    value = "${esp32State.soilPh}",
                    unit = "pH",
                    subtitle = "Slightly Acidic (Ideal)",
                    percentage = (esp32State.soilPh / 14.0).toFloat(),
                    gaugeColor = OrganicPurple,
                    icon = Icons.Default.Science,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Soil NPK Values Card
        item {
            GlassmorphismCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = AppLocalization.tr("npk_ratio", selectedLanguage),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Macro Nutrients (mg/kg)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    StatusBadge(text = "Optimal Balance", badgeColor = ForestGreen80)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NPKChip(label = "Nitrogen (N)", value = "${esp32State.nitrogen}", color = ForestGreen80)
                    NPKChip(label = "Phosphorus (P)", value = "${esp32State.phosphorus}", color = WaterBlue)
                    NPKChip(label = "Potassium (K)", value = "${esp32State.potassium}", color = HarvestGold80)
                }
            }
        }

        // Pump Relay Quick Switch
        item {
            GlassmorphismCard(
                backgroundColor = if (esp32State.pumpRelayActive) WaterBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = if (esp32State.pumpRelayActive) WaterBlue else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (esp32State.pumpRelayActive) AppLocalization.tr("pump_on", selectedLanguage) else AppLocalization.tr("pump_off", selectedLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Last Irrigation: ${esp32State.lastIrrigationTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Switch(
                        checked = esp32State.pumpRelayActive,
                        onCheckedChange = { viewModel.togglePump(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = WaterBlue)
                    )
                }
            }
        }

        // AI Smart Suggestions
        item {
            GlassmorphismCard(
                backgroundColor = OrganicPurple.copy(alpha = 0.15f),
                borderColor = OrganicPurple.copy(alpha = 0.4f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = OrganicPurple,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "CropCare Gemini Smart Advice",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = OrganicPurple
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Soil moisture at ${esp32State.soilMoisturePercent}%. Drip schedule auto-optimized.\n• Rain forecast for Wednesday (+25mm) — pause nitrogen fertilizer application.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Today's Tasks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppLocalization.tr("tasks", selectedLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = { showAddTaskDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Task")
                }
            }
        }

        items(tasks) { task ->
            GlassmorphismCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { viewModel.toggleTask(task.id, it) },
                        colors = CheckboxDefaults.colors(checkedColor = ForestGreen40)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (task.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${task.timeCategory} • Priority: ${task.priority}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun NPKChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}
