package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.CropCareViewModel
import com.example.ui.components.GlassmorphismCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.OrganicPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WaterBlue
import com.example.util.AppLocalization
import java.util.Locale

@Composable
fun DiseaseScanScreen(
    viewModel: CropCareViewModel
) {
    val context = LocalContext.current
    val scanResult by viewModel.latestScanResult.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var chatInputText by remember { mutableStateOf("") }

    // TextToSpeech Voice Assistant Engine
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Engine ready
            }
        }
        tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }
            override fun onDone(utteranceId: String?) {
                isSpeaking = false
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                isSpeaking = false
            }
        })
        ttsEngine = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    fun speakDiseaseAndFertilizer(diseaseName: String, treatmentText: String) {
        val lang = selectedLanguage.lowercase()
        val locale = when {
            lang.contains("telugu") || lang.contains("te") -> Locale("te", "IN")
            lang.contains("hindi") || lang.contains("hi") -> Locale("hi", "IN")
            lang.contains("tamil") || lang.contains("ta") -> Locale("ta", "IN")
            lang.contains("kannada") || lang.contains("kn") -> Locale("kn", "IN")
            lang.contains("malayalam") || lang.contains("ml") -> Locale("ml", "IN")
            else -> Locale("en", "IN")
        }

        val speechText = when {
            lang.contains("telugu") || lang.contains("te") ->
                "గుర్తించిన వ్యాధి: $diseaseName. నివారణ మరియు ఎరువుల వివరాలు: $treatmentText."
            lang.contains("hindi") || lang.contains("hi") ->
                "पहचाना गया रोग: $diseaseName. अनुशंसित उपचार और उर्वरक: $treatmentText."
            lang.contains("tamil") || lang.contains("ta") ->
                "கண்டறியப்பட்ட நோய்: $diseaseName. பரிந்துரைக்கப்பட்ட சிகிச்சை: $treatmentText."
            lang.contains("kannada") || lang.contains("kn") ->
                "ಗುರ್ತಿಸಲಾದ ರೋಗ: $diseaseName. ಶಿಫಾರಸು ಮಾಡಿದ ಚಿಕಿತ್ಸೆ: $treatmentText."
            lang.contains("malayalam") || lang.contains("ml") ->
                "കണ്ടെത്തിയ രോഗം: $diseaseName. ചിക്ത്സയും വളവും: $treatmentText."
            else ->
                "Detected Disease: $diseaseName. Recommended Treatment and Fertilizer: $treatmentText."
        }

        ttsEngine?.let { engine ->
            if (engine.isSpeaking && isSpeaking) {
                engine.stop()
                isSpeaking = false
            } else {
                engine.language = locale
                isSpeaking = true
                engine.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "CropDiseaseTTS")
            }
        }
    }

    // Auto-read voice assistant when scan result arrives
    LaunchedEffect(scanResult?.id) {
        val scan = scanResult
        if (scan != null && scan.isQualityGood && scan.isIdentified) {
            val treatmentInfo = if (scan.isHealthy) scan.symptoms else "${scan.treatment} ${scan.recommendedFertilizers}"
            speakDiseaseAndFertilizer(scan.diseaseName, treatmentInfo)
        }
    }

    // Camera launcher to take a live photo
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            selectedUri = null
            viewModel.analyzeCropImageBitmap(bitmap, "Live Camera Crop Leaf")
        }
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(context, "Camera permission required.", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery launcher to select an image from phone
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            capturedBitmap = null
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
                viewModel.analyzeCropImageBitmap(bitmap, "Gallery Upload Leaf")
            } catch (e: Exception) {
                e.printStackTrace()
                viewModel.analyzeCropImage("Gallery Leaf")
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            GlassmorphismCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            ) {
                Text(
                    text = AppLocalization.tr("scan_title", selectedLanguage),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sample Leaf Preset Chips
                Text(
                    text = AppLocalization.tr("upload_image", selectedLanguage),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sampleLeaves = listOf("Tomato Leaf", "Cotton Leaf", "Chilli Leaf", "Paddy Leaf")
                    items(sampleLeaves) { leaf ->
                        AssistChip(
                            onClick = {
                                viewModel.analyzeCropImage(leaf)
                            },
                            label = { Text(leaf, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                labelColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                ) {
                    if (capturedBitmap != null) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Captured Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (selectedUri != null) {
                        AsyncImage(
                            model = selectedUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_crop_leaf_disease_1785053793786),
                            contentDescription = "Leaf Scan Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (isScanning) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = AppLocalization.tr("analyzing", selectedLanguage),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val hasPermission = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_GRANTED

                                        if (hasPermission) {
                                            cameraLauncher.launch()
                                        } else {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(AppLocalization.tr("take_photo", selectedLanguage), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = { galleryLauncher.launch("image/*") },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(AppLocalization.tr("choose_gallery", selectedLanguage), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== IMAGE QUALITY CHECK FAILURE BANNER ====================
        scanResult?.let { scan ->
            if (!scan.isQualityGood) {
                item {
                    GlassmorphismCard(
                        backgroundColor = AlertRed.copy(alpha = 0.15f),
                        borderColor = AlertRed
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = AlertRed,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppLocalization.tr("quality_error_title", selectedLanguage),
                                    fontWeight = FontWeight.Bold,
                                    color = AlertRed,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (scan.qualityErrorMessage.isNotBlank()) scan.qualityErrorMessage
                                    else AppLocalization.tr("quality_error_msg", selectedLanguage),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            } else if (!scan.isIdentified || scan.confidencePercent < 60) {
                // Low Confidence Warning
                item {
                    GlassmorphismCard(
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                        borderColor = MaterialTheme.colorScheme.tertiary
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppLocalization.tr("confidence", selectedLanguage) + ": ${scan.confidencePercent}%",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (scan.confidenceErrorMessage.isNotBlank()) scan.confidenceErrorMessage
                                    else AppLocalization.tr("low_confidence_msg", selectedLanguage),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            } else {
                // Clean AI Report Output Card
                item {
                    GlassmorphismCard(
                        borderColor = if (scan.isHealthy) SuccessGreen else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ) {
                        // Title Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (scan.isHealthy) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Eco,
                                        contentDescription = null,
                                        tint = if (scan.isHealthy) SuccessGreen else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${scan.cropName} • ${scan.growthStage} (${scan.plantAge})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (scan.isHealthy) SuccessGreen else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

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
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (scan.isHealthy) SuccessGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (scan.isHealthy) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (scan.isHealthy) SuccessGreen else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = scan.diseaseName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${AppLocalization.tr("confidence", selectedLanguage)}: ${scan.confidencePercent}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            StatusBadge(
                                text = scan.severity,
                                badgeColor = if (scan.isHealthy) SuccessGreen else AlertRed
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Voice Assistant Interactive Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSpeaking) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                )
                                .border(
                                    1.dp,
                                    if (isSpeaking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSpeaking) AlertRed.copy(alpha = 0.2f)
                                                else MaterialTheme.colorScheme.primaryContainer
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isSpeaking) Icons.Default.GraphicEq else Icons.Default.RecordVoiceOver,
                                            contentDescription = "Voice Assistant",
                                            tint = if (isSpeaking) AlertRed else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = AppLocalization.tr("listen_voice", selectedLanguage),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${scan.diseaseName}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        val treatmentInfo = if (scan.isHealthy) scan.symptoms else "${scan.treatment} ${scan.recommendedFertilizers}"
                                        speakDiseaseAndFertilizer(scan.diseaseName, treatmentInfo)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSpeaking) AlertRed else MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isSpeaking) AppLocalization.tr("stop_voice", selectedLanguage)
                                        else AppLocalization.tr("listen_voice", selectedLanguage),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            DiagnosticMetric(AppLocalization.tr("affected_part", selectedLanguage), scan.affectedPart, AlertRed)
                            DiagnosticMetric(AppLocalization.tr("confidence", selectedLanguage), "${scan.confidencePercent}%", MaterialTheme.colorScheme.primary)
                            DiagnosticMetric(AppLocalization.tr("recovery", selectedLanguage), scan.expectedRecovery, WaterBlue)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = AppLocalization.tr("symptoms", selectedLanguage) + " & " + AppLocalization.tr("causes", selectedLanguage),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• ${AppLocalization.tr("symptoms", selectedLanguage)}: ${scan.symptoms}\n• ${AppLocalization.tr("causes", selectedLanguage)}: ${scan.causes}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Organic Treatment
                        TreatmentSection(
                            title = AppLocalization.tr("organic", selectedLanguage),
                            icon = Icons.Default.Eco,
                            iconTint = SuccessGreen,
                            content = scan.organicTreatment
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Chemical Treatment
                        TreatmentSection(
                            title = AppLocalization.tr("fungicide", selectedLanguage),
                            icon = Icons.Default.Science,
                            iconTint = WaterBlue,
                            content = if (scan.fungicidePesticide.isNotBlank()) "${scan.chemicalTreatment}\n\n• ${AppLocalization.tr("fungicide", selectedLanguage)}: ${scan.fungicidePesticide}" else scan.chemicalTreatment
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Recommended Fertilizers & Advice
                        TreatmentSection(
                            title = AppLocalization.tr("farmer_advice", selectedLanguage),
                            icon = Icons.Default.Healing,
                            iconTint = OrganicPurple,
                            content = "${scan.farmerAdvice}\n\n• ${AppLocalization.tr("prevention", selectedLanguage)}: ${scan.preventiveMeasures}"
                        )
                    }
                }
            }
        }

        // ==================== INBUILT CHATGPT AI CROP DOCTOR ====================
        item {
            GlassmorphismCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Gemini AI Crop Doctor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Recent Chat History
                    if (chatMessages.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            chatMessages.takeLast(4).forEach { msg ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (msg.isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .border(
                                            1.dp,
                                            if (msg.isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = if (msg.isUser) Icons.Default.CameraAlt else Icons.Default.SmartToy,
                                            contentDescription = null,
                                            tint = if (msg.isUser) MaterialTheme.colorScheme.primary else OrganicPurple,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (msg.isUser) "You" else "Gemini Crop AI",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (msg.isUser) MaterialTheme.colorScheme.primary else OrganicPurple
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = msg.text,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (isChatLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini is answering...", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Input Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInputText,
                            onValueChange = { chatInputText = it },
                            placeholder = { Text("Ask Gemini AI about crop diseases...", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (chatInputText.isNotBlank()) {
                                    val text = chatInputText
                                    chatInputText = ""
                                    viewModel.sendChatMessage(text)
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun DiagnosticMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color)
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
fun TreatmentSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(iconTint.copy(alpha = 0.12f))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = iconTint)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
