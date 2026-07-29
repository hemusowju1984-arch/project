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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.PestControl
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.ui.CropCareViewModel
import com.example.ui.components.GlassmorphismCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ForestGreen40
import com.example.ui.theme.ForestGreen80
import com.example.ui.theme.HarvestGold80
import com.example.ui.theme.OrganicPurple
import com.example.ui.theme.WarningAmber

@Composable
fun FertilizerPestScreen(
    viewModel: CropCareViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Fertilizer, 1: Pest Management
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
                    text = { Text("Fertilizer AI Guide", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Pest Management", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Fertilizer Section
            item {
                GlassmorphismCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Soil NPK Readings",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "N: ${esp32State.nitrogen} • P: ${esp32State.phosphorus} • K: ${esp32State.potassium} mg/kg",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        StatusBadge(text = "Soil pH 6.8", badgeColor = ForestGreen80)
                    }
                }
            }

            item {
                Text(
                    text = "Recommended Organic Fertilizers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                GlassmorphismCard {
                    FertilizerItemRow(
                        name = "Neem Coated Cake & Vermicompost",
                        type = "Soil Conditioner",
                        qty = "100 kg / acre",
                        appDate = "At Sowing / Basal Dose",
                        icon = Icons.Default.Eco,
                        iconTint = ForestGreen80
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FertilizerItemRow(
                        name = "Jeevamrutha Bio-Nutrient Liquid",
                        type = "Microbial Foliar Spray",
                        qty = "200 Liters / acre",
                        appDate = "Every 15 Days in Drip",
                        icon = Icons.Default.Eco,
                        iconTint = ForestGreen80
                    )
                }
            }

            item {
                Text(
                    text = "Recommended Chemical / Inorganic Doses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                GlassmorphismCard {
                    FertilizerItemRow(
                        name = "Neem Coated Urea (46% N)",
                        type = "Nitrogen Booster",
                        qty = "25 kg / acre (Split Dose)",
                        appDate = "Day 30 & Day 60",
                        icon = Icons.Default.Science,
                        iconTint = WarningAmber
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FertilizerItemRow(
                        name = "Di-Ammonium Phosphate (DAP 18-46-0)",
                        type = "Phosphorus Root Booster",
                        qty = "50 kg / acre",
                        appDate = "Basal Application",
                        icon = Icons.Default.Science,
                        iconTint = WarningAmber
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FertilizerItemRow(
                        name = "Muriate of Potash (MOP 60% K)",
                        type = "Potassium Fruit Fill",
                        qty = "20 kg / acre",
                        appDate = "At Flowering Stage",
                        icon = Icons.Default.Science,
                        iconTint = OrganicPurple
                    )
                }
            }

            item {
                GlassmorphismCard {
                    Text(
                        text = "Essential Micronutrient Sprays",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Zinc Sulphate (21% Zn): 10 kg/acre to prevent leaf chlorosis.")
                    Text(text = "• Borax (10.5% B): 2.5 kg/acre for fruit setting and flower strength.")
                    Text(text = "• Calcium Nitrate: 5 kg/acre via drip to eliminate blossom end rot.")
                }
            }
        } else {
            // Pest Management Section
            item {
                GlassmorphismCard(
                    borderColor = WarningAmber.copy(alpha = 0.5f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BugReport, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI Pest Identification & Outbreak Warning",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Current Risk: Whitefly & Pink Bollworm (Moderate Risk due to humidity)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            item {
                GlassmorphismCard {
                    Text(
                        text = "Organic Pest Control Methods",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen80
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Sticky Yellow Traps: Install 10-12 traps/acre to reduce sucking pests.")
                    Text(text = "• Pheromone Traps: 4 traps/acre for adult pink bollworm monitoring.")
                    Text(text = "• Neem Oil Spray: Cold-pressed Neem oil (10,000 PPM) @ 3 ml/L water.")
                }
            }

            item {
                GlassmorphismCard {
                    Text(
                        text = "Chemical Pesticide Guidelines",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = WarningAmber
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Imidacloprid 17.8% SL @ 0.5 ml/L for whitefly control.")
                    Text(text = "• Spinetoram 11.7% SC @ 1 ml/L for thrips and caterpillar control.")
                    Text(text = "• Always wear safety gear during chemical spraying.")
                }
            }

            item {
                GlassmorphismCard {
                    Text(
                        text = "Preventive Crop Protection Protocol",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Maintain clean farm field borders to prevent weed host habitats.")
                    Text(text = "• Rotate crops every season to break pest life cycles.")
                    Text(text = "• Spray early in the morning (06:00 - 09:00 AM) or late afternoon.")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun FertilizerItemRow(
    name: String,
    type: String,
    qty: String,
    appDate: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "$type • $appDate", fontSize = 11.sp, color = Color.Gray)
            }
        }
        Text(text = qty, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = iconTint)
    }
}
