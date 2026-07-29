package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CropCareViewModel
import com.example.ui.components.GlassmorphismCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.SuccessGreen

data class AppLanguage(val code: String, val name: String, val nativeName: String)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: CropCareViewModel,
    onLogout: () -> Unit
) {
    val farmerName by viewModel.farmerName.collectAsState()
    val farmerPhone by viewModel.farmerPhone.collectAsState()
    val farmerLocation by viewModel.farmerLocation.collectAsState()
    val farmSizeAcres by viewModel.farmSizeAcres.collectAsState()

    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    // Expanded Farmer Profile Fields
    var isProfileExpanded by remember { mutableStateOf(true) }
    var editName by remember(farmerName) { mutableStateOf(farmerName) }
    var editPhone by remember(farmerPhone) { mutableStateOf(farmerPhone) }
    var editLocation by remember(farmerLocation) { mutableStateOf(farmerLocation) }
    var editFarmSize by remember(farmSizeAcres) { mutableStateOf(farmSizeAcres) }
    var showSavedBanner by remember { mutableStateOf(false) }

    val isTelugu = selectedLanguage.equals("Telugu", ignoreCase = true) || selectedLanguage.contains("తెలుగు")

    val languages = listOf(
        AppLanguage("en", "English", "English"),
        AppLanguage("te", "Telugu", "తెలుగు"),
        AppLanguage("hi", "Hindi", "हिंदी"),
        AppLanguage("ta", "Tamil", "தமிழ்"),
        AppLanguage("ml", "Malayalam", "മലയാളം"),
        AppLanguage("kn", "Kannada", "కನ್ನಡ"),
        AppLanguage("mr", "Marathi", "మరాఠీ"),
        AppLanguage("gu", "Gujarati", "ગુજરાતી"),
        AppLanguage("bn", "Bengali", "বাংলা"),
        AppLanguage("pa", "Punjabi", "ਪੰਜਾਬੀ")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Expanded Farmer Profile Box
        item {
            Text(
                text = if (isTelugu) "రైతు ప్రొఫైల్" else "Farmer Profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            GlassmorphismCard(
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isProfileExpanded = !isProfileExpanded },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (farmerName.isNotBlank()) farmerName else (if (isTelugu) "రైతు ప్రొఫైల్" else "Farmer Profile"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTelugu) "రైతు వివరాలను చూడండి & ఎడిట్ చేయండి" else "Tap to Expand / Edit Profile Details",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    StatusBadge(
                        text = if (isProfileExpanded) (if (isTelugu) "కుదించు" else "Collapse") else (if (isTelugu) "విస్తరించు" else "Expand"),
                        badgeColor = MaterialTheme.colorScheme.primary
                    )
                }

                if (isProfileExpanded) {
                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it; showSavedBanner = false },
                            label = { Text(if (isTelugu) "రైతు పూర్తి పేరు" else "Farmer Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it; showSavedBanner = false },
                            label = { Text(if (isTelugu) "ఫోన్ నంబర్" else "Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = editLocation,
                            onValueChange = { editLocation = it; showSavedBanner = false },
                            label = { Text(if (isTelugu) "గ్రామం / నివాసం" else "Village / Location") },
                            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = editFarmSize,
                            onValueChange = { editFarmSize = it; showSavedBanner = false },
                            label = { Text(if (isTelugu) "భూమి పరిమాణం (ఎకరాలలో)" else "Land Size (Acres)") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (showSavedBanner) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SuccessGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTelugu) "ప్రొఫైల్ విజయవంతంగా సేవ్ చేయబడింది!" else "Profile Saved Successfully!",
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.updateFarmerProfile(
                                    name = editName,
                                    phone = editPhone,
                                    location = editLocation,
                                    farm = "",
                                    size = editFarmSize
                                )
                                showSavedBanner = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTelugu) "ప్రొఫైల్ సేవ్ చేయండి" else "Save Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // Language Preferences
        item {
            Text(
                text = if (isTelugu) "ఎంచుకున్న భాష" else "Selected Language",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            GlassmorphismCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTelugu) "అప్లికేషన్ భాషను ఎంచుకోండి" else "Choose Application Language",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isTelugu)
                        "యాక్టివ్: అన్ని యాప్ పేర్లు, శీర్షికలు, వాయిస్ అసిస్టెంట్ మరియు సమాధానాలు తెలుగులో ఉంటాయి."
                    else
                        "Active: All app labels, titles, voice assistant, and AI responses are set to $selectedLanguage.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    languages.forEach { lang ->
                        val isSelected = selectedLanguage.equals(lang.name, ignoreCase = true) ||
                                selectedLanguage.equals(lang.nativeName, ignoreCase = true)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                )
                                .clickable { viewModel.setLanguage(lang.name) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = "${lang.name} (${lang.nativeName})",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // App System Preferences
        item {
            Text(
                text = if (isTelugu) "సిస్టమ్ సెట్టింగ్‌లు" else "System Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            GlassmorphismCard {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isTelugu) "డార్క్ మోడ్ ఇంటర్‌ఫేస్" else "Dark Mode Interface",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isTelugu) "బయట చదవడానికి అనుకూలమైన డార్క్ థీమ్" else "High-contrast dark theme for outdoor readability",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isTelugu) "ఆఫ్‌లైన్ నిల్వ స్థలం (Room DB)" else "Offline SQLite Storage",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isTelugu) "డేటా సురక్షితంగా సేవ్ చేయబడింది" else "Room DB local backup active",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    StatusBadge(text = if (isTelugu) "సింక్ అయింది" else "SYNCED", badgeColor = SuccessGreen)
                }
            }
        }

        // Logout Button
        item {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isTelugu) "ఖాతా నుండి లాగ్ అవుట్ చేయండి" else "Logout Account", fontWeight = FontWeight.Bold)
            }
        }
    }
}
