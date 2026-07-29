package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.WeatherDayForecast
import com.example.ui.CropCareViewModel
import com.example.ui.components.GlassmorphismCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ForestGreen40
import com.example.ui.theme.ForestGreen80
import com.example.ui.theme.HarvestGold80
import com.example.ui.theme.OrganicPurple
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WaterBlue

@Composable
fun WeatherSoilScreen(
    viewModel: CropCareViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Weather, 1: Soil Health
    val weather by viewModel.weather.collectAsState()
    val esp32State by viewModel.esp32State.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Weather Forecast", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Soil Health Card", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Weather Forecast Screen
            item {
                GlassmorphismCard(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${weather.currentTemp}°C",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${weather.locationName} • ${weather.condition}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(
                            Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = HarvestGold80,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeatherMetric("Humidity", "${weather.humidityPercent}%", Icons.Default.Opacity)
                        WeatherMetric("Wind Speed", "${weather.windSpeedKmH} km/h", Icons.Default.Air)
                        WeatherMetric("UV Index", "${weather.uvIndex} High", Icons.Default.WbSunny)
                        WeatherMetric("Evapo Index", "${weather.evapotranspirationMm} mm", Icons.Default.DeviceThermostat)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = HarvestGold80, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Sunrise: ${weather.sunriseTime}", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbTwilight, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Sunset: ${weather.sunsetTime}", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Severe Alerts Card
            item {
                GlassmorphismCard(
                    borderColor = WarningAmber.copy(alpha = 0.5f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Agricultural Weather Alerts",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    weather.activeAlerts.forEach { alert ->
                        Text(
                            text = "• $alert",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "7-Day Farm Weather Forecast",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(weather.sevenDayForecast) { day ->
                GlassmorphismCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (day.iconType) {
                                    "STORM" -> Icons.Default.Thunderstorm
                                    "RAIN" -> Icons.Default.Opacity
                                    "CLOUDY" -> Icons.Default.Cloud
                                    else -> Icons.Default.WbSunny
                                },
                                contentDescription = null,
                                tint = if (day.iconType == "STORM" || day.iconType == "RAIN") WaterBlue else HarvestGold80,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = day.dayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = day.condition, fontSize = 12.sp, color = Color.Gray)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Rain: ${day.rainProbPercent}%", fontSize = 12.sp, color = WaterBlue)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "${day.maxTemp}° / ${day.minTemp}°", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        } else {
            // Soil Health Card
            item {
                GlassmorphismCard(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Soil Health Score: 94 / 100",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Certified Organic Soil Profile • Fertile Alluvial Silt",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        StatusBadge(text = "EXCELLENT", badgeColor = ForestGreen80)
                    }
                }
            }

            item {
                Text(
                    text = "Soil Physical & Chemical Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                GlassmorphismCard {
                    SoilPropertyRow("Soil Moisture Content", "${esp32State.soilMoisturePercent}%", "Ideal for Cotton/Tomato", ForestGreen80)
                    Spacer(modifier = Modifier.height(10.dp))
                    SoilPropertyRow("Soil Temperature", "${esp32State.soilTemperature}°C", "Normal Root Zone Temp", HarvestGold80)
                    Spacer(modifier = Modifier.height(10.dp))
                    SoilPropertyRow("Soil pH Balance", "${esp32State.soilPh} pH", "Optimal Nutrient Availability", OrganicPurple)
                    Spacer(modifier = Modifier.height(10.dp))
                    SoilPropertyRow("Organic Carbon (OC)", "0.78 %", "Medium to High Carbon Level", WaterBlue)
                }
            }

            item {
                Text(
                    text = "Macro Nutrients (NPK) Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                GlassmorphismCard {
                    SoilPropertyRow("Nitrogen (N)", "${esp32State.nitrogen} mg/kg", "Medium Level", ForestGreen80)
                    Spacer(modifier = Modifier.height(10.dp))
                    SoilPropertyRow("Phosphorus (P)", "${esp32State.phosphorus} mg/kg", "High Available P", WaterBlue)
                    Spacer(modifier = Modifier.height(10.dp))
                    SoilPropertyRow("Potassium (K)", "${esp32State.potassium} mg/kg", "Rich Potassium Status", HarvestGold80)
                }
            }

            item {
                GlassmorphismCard {
                    Text(
                        text = "Soil Improvement Recommendations",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Add 2 tons/acre Vermicompost to maintain Organic Carbon > 0.8%.")
                    Text(text = "• Apply Trichoderma viride bio-fungicide to prevent root rot pathogens.")
                    Text(text = "• Maintain mulching to minimize soil evaporation during hot afternoons.")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun WeatherMetric(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = ForestGreen80, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(text = title, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun SoilPropertyRow(label: String, value: String, status: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = status, fontSize = 11.sp, color = Color.Gray)
        }
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color)
    }
}
