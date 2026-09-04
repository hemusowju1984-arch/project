package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.IrrigationMode
import com.example.ui.CropCareViewModel
import com.example.ui.components.GlassmorphismCard
import com.example.ui.components.MetricGaugeCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ForestGreen80
import com.example.ui.theme.HarvestGold80
import com.example.ui.theme.OrganicPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WaterBlue

@Composable
fun IrrigationScreen(
    viewModel: CropCareViewModel
) {
    val esp32State by viewModel.esp32State.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val isTelugu = selectedLanguage.equals("Telugu", ignoreCase = true) || selectedLanguage.contains("తెలుగు")

    var showEsp32CodeDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // ==================== BIG NEAT ESP32 MOTOR PUMP CONTROL BOX ====================
        item {
            GlassmorphismCard(
                backgroundColor = if (esp32State.pumpRelayActive) WaterBlue.copy(alpha = 0.20f) else MaterialTheme.colorScheme.surface,
                borderColor = if (esp32State.pumpRelayActive) WaterBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(if (esp32State.pumpRelayActive) WaterBlue else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PowerSettingsNew,
                                    contentDescription = null,
                                    tint = if (esp32State.pumpRelayActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = if (esp32State.pumpRelayActive) {
                                        if (isTelugu) "మోటార్ పంప్ నడుస్తోంది" else "MOTOR PUMP RUNNING"
                                    } else {
                                        if (isTelugu) "మోటార్ పంప్ ఆపివేయబడింది" else "MOTOR PUMP STOPPED"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (esp32State.pumpRelayActive) WaterBlue else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (esp32State.pumpRelayActive) {
                                        if (isTelugu) "2HP సబ్‌మెర్సిబుల్ పంప్ ఆన్‌లో ఉంది • డ్రిప్ నీటిపారుదల సక్రమంగా జరుగుతోంది" else "2HP Submersible Pump Active • Drip Line Pressurized"
                                    } else {
                                        if (isTelugu) "పంప్ ప్రస్తుతం ఆఫ్‌లో ఉంది • రక్షణ స్థితి" else "Pump is Currently Off • Safe & Idle State"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                )
                            }
                        }

                        // ON / OFF Control Box Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (esp32State.pumpRelayActive) WaterBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (esp32State.pumpRelayActive) (if (isTelugu) "ఆన్" else "ON") else (if (isTelugu) "ఆఫ్" else "OFF"),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = if (esp32State.pumpRelayActive) WaterBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Switch(
                                    checked = esp32State.pumpRelayActive,
                                    onCheckedChange = { viewModel.togglePump(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = WaterBlue,
                                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ESP32 Connection & Hardware Info Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "ESP32-AGRI-01", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text(
                            text = if (esp32State.isConnected) (if (isTelugu) "ఆన్‌లైన్" else "ONLINE") else (if (isTelugu) "ఆఫ్‌లైన్" else "OFFLINE"),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = if (esp32State.isConnected) SuccessGreen else AlertRed
                        )
                    }

                    if (esp32State.pumpRelayActive) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.togglePump(false) },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.PowerSettingsNew, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(if (isTelugu) "అత్యవసర మోటార్ నిలిపివేత" else "EMERGENCY STOP MOTOR", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // ==================== IOT SENSOR TELEMETRY (INCREASED BOX SIZES) ====================
        item {
            Text(
                text = if (isTelugu) "IoT లైవ్ సెన్సార్ సమాచారం" else "IoT Live Sensor Telemetry",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            GlassmorphismCard {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Soil Moisture & Soil Temperature
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(WaterBlue.copy(alpha = 0.14f))
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = WaterBlue, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = if (isTelugu) "నేల తేమ" else "Soil Moisture", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "${esp32State.soilMoisturePercent}%", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = WaterBlue)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(HarvestGold80.copy(alpha = 0.14f))
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DeviceThermostat, contentDescription = null, tint = HarvestGold80, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = if (isTelugu) "నేల ఉష్ణోగ్రత" else "Soil Temp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "${esp32State.soilTemperature}°C", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = HarvestGold80)
                            }
                        }
                    }

                    // Air Humidity & Water Tank Level
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(WaterBlue.copy(alpha = 0.14f))
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Opacity, contentDescription = null, tint = WaterBlue, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = if (isTelugu) "గాలి తేమ" else "Air Humidity", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "${weather.humidityPercent}%", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = WaterBlue)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = if (isTelugu) "నీటి ట్యాంక్ స్థాయి" else "Water Tank Level", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "${esp32State.waterTankLevelPercent}%", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    // Soil pH & Rain Sensor
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(OrganicPurple.copy(alpha = 0.14f))
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Science, contentDescription = null, tint = OrganicPurple, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = if (isTelugu) "నేల pH స్థాయి" else "Soil pH Level", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "6.8 pH", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = OrganicPurple)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(HarvestGold80.copy(alpha = 0.14f))
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = HarvestGold80, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = if (isTelugu) "వర్షపాత సెన్సార్" else "Rain Sensor", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = if (isTelugu) "వర్షం లేదు" else "NO RAIN", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = HarvestGold80)
                            }
                        }
                    }

                    // Soil NPK Nutrients
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(OrganicPurple.copy(alpha = 0.14f))
                            .padding(22.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Science,
                                    contentDescription = null,
                                    tint = OrganicPurple,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isTelugu) "నేల NPK పోషకాల స్థాయి" else "Soil NPK Nutrients Level",
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
                                        .background(OrganicPurple.copy(alpha = 0.2f))
                                        .padding(vertical = 10.dp, horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(if (isTelugu) "నైట్రోజన్ (N)" else "Nitrogen (N)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrganicPurple)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("${esp32State.nitrogen} mg/kg", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(OrganicPurple.copy(alpha = 0.2f))
                                        .padding(vertical = 10.dp, horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(if (isTelugu) "భాస్వరం (P)" else "Phosphorus (P)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrganicPurple)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("${esp32State.phosphorus} mg/kg", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(OrganicPurple.copy(alpha = 0.2f))
                                        .padding(vertical = 10.dp, horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(if (isTelugu) "పొటాషియం (K)" else "Potassium (K)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrganicPurple)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("${esp32State.potassium} mg/kg", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for ESP32 Arduino C++ Code
    if (showEsp32CodeDialog) {
        AlertDialog(
            onDismissRequest = { showEsp32CodeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ESP32 Arduino Firmware Code", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                val esp32Code = """
                    // ESP32 Smart Agriculture Motor & Multi-Sensor Automation Firmware
                    #include <WiFi.h>
                    #include <HTTPClient.h>
                    #include <DHT.h>
                    #include <HardwareSerial.h>

                    // Pin Definitions
                    #define RELAY_PIN 26        // Motor Pump Relay Control
                    #define SOIL_MOISTURE_PIN 34// Capacitive Soil Moisture Analog Pin
                    #define RAIN_SENSOR_PIN 35  // Rain Probe Digital/Analog Pin
                    #define TANK_TRIG_PIN 18    // Ultrasonic Distance Sensor Trig
                    #define TANK_ECHO_PIN 19    // Ultrasonic Distance Sensor Echo
                    #define DHTPIN 4            // DHT22 Air Temp & Humidity
                    #define PH_SENSOR_PIN 32    // Soil pH Analog Probe
                    #define DHTTYPE DHT22

                    // RS485 Modbus RTU for Soil NPK Sensor (RX2=16, TX2=17)
                    HardwareSerial rs485(2);
                    const byte npkQuery[] = {0x01, 0x03, 0x00, 0x1E, 0x00, 0x03, 0x65, 0xCD};
                    byte npkResponse[11];

                    DHT dht(DHTPIN, DHTTYPE);

                    void setup() {
                      Serial.begin(115200);
                      rs485.begin(9600, SERIAL_8N1, 16, 17);
                      
                      pinMode(RELAY_PIN, OUTPUT);
                      pinMode(SOIL_MOISTURE_PIN, INPUT);
                      pinMode(RAIN_SENSOR_PIN, INPUT);
                      pinMode(TANK_TRIG_PIN, OUTPUT);
                      pinMode(TANK_ECHO_PIN, INPUT);
                      pinMode(PH_SENSOR_PIN, INPUT);
                      
                      digitalWrite(RELAY_PIN, LOW); // Default Motor OFF
                      dht.begin();
                      Serial.println("ESP32 Smart AI Agriculture System Ready!");
                    }

                    float getWaterTankLevel() {
                      digitalWrite(TANK_TRIG_PIN, LOW);
                      delayMicroseconds(2);
                      digitalWrite(TANK_TRIG_PIN, HIGH);
                      delayMicroseconds(10);
                      digitalWrite(TANK_TRIG_PIN, LOW);
                      long duration = pulseIn(TANK_ECHO_PIN, HIGH);
                      float distanceCm = duration * 0.034 / 2;
                      float tankPercent = map(distanceCm, 200, 10, 0, 100);
                      return constrain(tankPercent, 0, 100);
                    }

                    void loop() {
                      int rawMoisture = analogRead(SOIL_MOISTURE_PIN);
                      int soilMoisturePercent = map(rawMoisture, 4095, 1500, 0, 100);
                      soilMoisturePercent = constrain(soilMoisturePercent, 0, 100);

                      int isRaining = digitalRead(RAIN_SENSOR_PIN); // LOW = Rain detected
                      float airTemp = dht.readTemperature();
                      float airHumidity = dht.readHumidity();
                      float waterTankPct = getWaterTankLevel();

                      // Read pH Level
                      int rawPH = analogRead(PH_SENSOR_PIN);
                      float soilPH = (rawPH / 4095.0) * 14.0;

                      // Read Soil NPK via RS485
                      int nitrogen = 42, phosphorus = 28, potassium = 85; // Default fallback
                      rs485.write(npkQuery, sizeof(npkQuery));
                      delay(100);
                      if (rs485.available() >= 11) {
                        rs485.readBytes(npkResponse, 11);
                        nitrogen = (npkResponse[3] << 8) | npkResponse[4];
                        phosphorus = (npkResponse[5] << 8) | npkResponse[6];
                        potassium = (npkResponse[7] << 8) | npkResponse[8];
                      }

                      // ESP32 Automated Motor Relay Trigger Logic
                      bool shouldRunMotor = (soilMoisturePercent < 45) && 
                                           (isRaining == HIGH) && 
                                           (waterTankPct > 15.0);

                      if (shouldRunMotor) {
                        digitalWrite(RELAY_PIN, HIGH); // Motor Pump ON
                        Serial.println("✓ Motor Status: ON (Irrigating)");
                      } else {
                        digitalWrite(RELAY_PIN, LOW);  // Motor Pump OFF
                        Serial.println("ⓘ Motor Status: OFF (Idle / Safety Stop)");
                      }

                      Serial.printf("Telemetry -> Moisture: %d%% | Rain: %s | Temp: %.1fC | Humid: %.1f%% | Tank: %.1f%% | pH: %.1f | NPK: %d-%d-%d\n",
                        soilMoisturePercent, (isRaining == LOW ? "YES" : "NO"), airTemp, airHumidity, waterTankPct, soilPH, nitrogen, phosphorus, potassium);

                      delay(3000); // Poll every 3 seconds
                    }
                """.trimIndent()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E1E1E))
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    SelectionContainer {
                        Text(
                            text = esp32Code,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF00FF66)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showEsp32CodeDialog = false }) {
                    Text("Close Code")
                }
            }
        )
    }
}

@Composable
fun AutomationConditionRow(
    conditionName: String,
    currentVal: String,
    isMet: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isMet) SuccessGreen else AlertRed,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = conditionName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        Text(
            text = "$currentVal (${if (isMet) "PASS" else "FAIL"})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isMet) SuccessGreen else AlertRed
        )
    }
}
