package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.ForestGreen40
import com.example.ui.theme.ForestGreen80
import com.example.ui.theme.HarvestGold80
import com.example.ui.theme.WaterBlue

@Composable
fun MarketExpensesScreen(
    viewModel: CropCareViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Market Prices, 1: Expense Manager
    val marketPrices by viewModel.marketPrices.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var itemTitle by remember { mutableStateOf("") }
    var itemCategory by remember { mutableStateOf("Fertilizer") }
    var itemAmount by remember { mutableStateOf("") }
    var isIncomeType by remember { mutableStateOf(false) }

    val totalIncome = expenses.filter { it.isIncome }.sumOf { it.amount }
    val totalExpense = expenses.filter { !it.isIncome }.sumOf { it.amount }
    val netProfit = totalIncome - totalExpense

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
                    text = { Text("Daily Mandi Prices", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Farm Expense Tracker", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Mandi Prices Section
            item {
                GlassmorphismCard(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = ForestGreen80, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Live APMC Agriculture Mandi Prices",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Updated daily from e-NAM & APMC State Markets",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            items(marketPrices) { crop ->
                GlassmorphismCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = crop.cropName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (crop.isBestSellingTime) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    StatusBadge(text = "PEAK PRICE WINDOW", badgeColor = HarvestGold80)
                                }
                            }
                            Text(
                                text = "Mandi: ${crop.mandiName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${crop.pricePerQuintal} / Qtl",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen80
                            )
                            Text(
                                text = crop.changeTrend,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (crop.changeTrend.contains("+")) ForestGreen80 else AlertRed
                            )
                        }
                    }
                }
            }
        } else {
            // Expense Manager Section
            item {
                GlassmorphismCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Profit & Loss Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Record")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ExpenseStatBox("Total Sales", "₹${totalIncome.toInt()}", ForestGreen80)
                        ExpenseStatBox("Total Inputs", "₹${totalExpense.toInt()}", AlertRed)
                        ExpenseStatBox("Net Profit", "₹${netProfit.toInt()}", HarvestGold80)
                    }
                }
            }

            if (showAddDialog) {
                item {
                    GlassmorphismCard(
                        borderColor = ForestGreen80
                    ) {
                        Text(text = "Add Expense or Crop Sale", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = itemTitle,
                            onValueChange = { itemTitle = it },
                            label = { Text("Title (e.g. Urea Bags / Labor)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = itemAmount,
                            onValueChange = { itemAmount = it },
                            label = { Text("Amount (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    val amt = itemAmount.toDoubleOrNull() ?: 0.0
                                    viewModel.addExpenseItem(itemTitle, itemCategory, amt, isIncome = false)
                                    showAddDialog = false
                                    itemTitle = ""
                                    itemAmount = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Add Expense")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    val amt = itemAmount.toDoubleOrNull() ?: 0.0
                                    viewModel.addExpenseItem(itemTitle, itemCategory, amt, isIncome = true)
                                    showAddDialog = false
                                    itemTitle = ""
                                    itemAmount = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen40),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Add Income")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Expense & Sale Entries",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(expenses) { entry ->
                GlassmorphismCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = entry.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "${entry.category} • ${entry.date}", fontSize = 11.sp, color = Color.Gray)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${if (entry.isIncome) "+" else "-"} ₹${entry.amount.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (entry.isIncome) ForestGreen80 else AlertRed
                            )
                            IconButton(onClick = { viewModel.deleteExpenseItem(entry.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                            }
                        }
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
fun ExpenseStatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}
