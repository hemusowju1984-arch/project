package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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

@Composable
fun ReportsAdminScreen(
    viewModel: CropCareViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: PDF Reports, 1: Govt Schemes, 2: Admin Panel

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
                    text = { Text("PDF Reports", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Govt Schemes", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Admin Panel", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // PDF Reports Section
            item {
                GlassmorphismCard {
                    Text(
                        text = "Generate Farm Export Reports",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Download official PDF documents for bank loans, crop insurance claims, or agricultural extensions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PdfReportItem("Crop Health & Diagnostics Report", "Includes TFLite leaf disease scan history & remedies.")
                    Spacer(modifier = Modifier.height(10.dp))
                    PdfReportItem("Soil Health Card Summary (NPK & pH)", "Includes laboratory & ESP32 sensor values.")
                    Spacer(modifier = Modifier.height(10.dp))
                    PdfReportItem("Irrigation & Water Consumption Log", "Shows total liters used & ESP32 pump runtime.")
                    Spacer(modifier = Modifier.height(10.dp))
                    PdfReportItem("Farm Profit & Loss Expense Ledger", "Detailed audit of seeds, labor, fertilizer & sales income.")
                }
            }
        } else if (selectedTab == 1) {
            // Govt Schemes Directory
            item {
                GlassmorphismCard {
                    Text(
                        text = "PM-KISAN Samman Nidhi Scheme",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen80
                    )
                    Text(text = "Direct income support of ₹6,000 per year in 3 equal installments to landholding farmer families.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusBadge(text = "Eligible • Apply online via CSC", badgeColor = ForestGreen80)
                }
            }

            item {
                GlassmorphismCard {
                    Text(
                        text = "Pradhan Mantri Fasal Bima Yojana (PMFBY)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HarvestGold80
                    )
                    Text(text = "Comprehensive crop insurance against natural calamities, pests & diseases.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusBadge(text = "2% Premium for Kharif Crops", badgeColor = HarvestGold80)
                }
            }

            item {
                GlassmorphismCard {
                    Text(
                        text = "Kisan Credit Card (KCC) Scheme",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OrganicPurple
                    )
                    Text(text = "Concessional interest rate loan up to ₹3 Lakhs for agricultural credit requirements.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusBadge(text = "Subsidized 4% Interest Rate", badgeColor = OrganicPurple)
                }
            }
        } else {
            // Admin Panel
            item {
                GlassmorphismCard(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = ForestGreen80, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "CropCare AI Central Platform Admin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "System Metrics & System Health", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AdminStatBox("Active Farmers", "12,480", ForestGreen80)
                        AdminStatBox("IoT Sensors", "34,920", HarvestGold80)
                        AdminStatBox("AI Scans Today", "8,140", OrganicPurple)
                    }
                }
            }

            item {
                GlassmorphismCard {
                    Text(text = "System Status", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Gemini 3.5 Flash Model: ONLINE")
                    Text(text = "• TFLite Leaf Vision Model: v2.4 LOADED")
                    Text(text = "• ESP32 MQTT Telemetry Gateway: 99.9% UPTIME")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PdfReportItem(title: String, desc: String) {
    var downloaded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = desc, fontSize = 11.sp, color = Color.Gray)
            }
        }

        Button(
            onClick = { downloaded = true },
            colors = ButtonDefaults.buttonColors(containerColor = if (downloaded) ForestGreen80 else ForestGreen40),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (downloaded) "Downloaded" else "PDF", fontSize = 11.sp)
        }
    }
}

@Composable
fun AdminStatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}
