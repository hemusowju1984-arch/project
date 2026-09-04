package com.example.ui.screens

import android.speech.tts.TextToSpeech
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChatMessage
import com.example.ui.CropCareViewModel
import com.example.ui.components.GlassmorphismCard
import com.example.ui.theme.ForestGreen40
import com.example.ui.theme.HarvestGold80
import com.example.ui.theme.OrganicPurple
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatbotScreen(
    viewModel: CropCareViewModel
) {
    val context = LocalContext.current
    val messages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isChatLoading.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var isVoiceRecording by remember { mutableStateOf(false) }

    val languages = listOf("English", "Hindi", "Telugu", "Tamil", "Kannada")
    var langDropdownExpanded by remember { mutableStateOf(false) }

    // TextToSpeech setup
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var currentlySpeakingMsgId by remember { mutableStateOf<String?>(null) }

    DisposableEffect(context) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TTS initialized
            }
        }
        ttsEngine = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    fun speakText(msgId: String, text: String, language: String) {
        val locale = when (language.lowercase()) {
            "hindi" -> Locale("hi", "IN")
            "telugu" -> Locale("te", "IN")
            "tamil" -> Locale("ta", "IN")
            "kannada" -> Locale("kn", "IN")
            else -> Locale("en", "IN")
        }
        ttsEngine?.let { engine ->
            if (currentlySpeakingMsgId == msgId && engine.isSpeaking) {
                engine.stop()
                currentlySpeakingMsgId = null
            } else {
                engine.language = locale
                engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "CropCareTTS")
                currentlySpeakingMsgId = msgId
            }
        }
    }

    // Auto read latest AI response if generated
    LaunchedEffect(messages.size) {
        val latest = messages.lastOrNull()
        if (latest != null && !latest.isUser && messages.size > 1) {
            speakText(latest.id, latest.text, selectedLanguage)
        }
    }

    val samplePrompts = listOf(
        "How to increase tomato crop yield?",
        "What is the best NPK ratio for cotton?",
        "Neem oil spray ratio for whitefly pest",
        "How much water does my crop need today?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Top Header with Expanded Language Picker
        GlassmorphismCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = OrganicPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CropCare AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = langDropdownExpanded,
                    onExpandedChange = { langDropdownExpanded = !langDropdownExpanded }
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(selectedLanguage, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langDropdownExpanded) },
                        modifier = Modifier.menuAnchor(),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = OrganicPurple.copy(alpha = 0.25f),
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = langDropdownExpanded,
                        onDismissRequest = { langDropdownExpanded = false }
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    viewModel.setLanguage(lang)
                                    langDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(
                    msg = msg,
                    isSpeaking = currentlySpeakingMsgId == msg.id,
                    onSpeakClick = {
                        speakText(msg.id, msg.text, selectedLanguage)
                    }
                )
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = OrganicPurple, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "CropCare AI is thinking...", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Quick Suggestion Chips (Horizontal LazyRow shifted up)
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(samplePrompts) { prompt ->
                AssistChip(
                    onClick = { viewModel.sendChatMessage(prompt) },
                    label = { Text(prompt, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Input Field & Voice Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Ask about crops, diseases, NPK...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    isVoiceRecording = !isVoiceRecording
                    if (isVoiceRecording) {
                        textInput = "How much water does my cotton crop need today?"
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isVoiceRecording) HarvestGold80 else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (isVoiceRecording) Color.Black else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendChatMessage(textInput)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ForestGreen40)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    msg: ChatMessage,
    isSpeaking: Boolean,
    onSpeakClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (msg.isUser) ForestGreen40 else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (msg.isUser) 16.dp else 4.dp,
                bottomEnd = if (msg.isUser) 4.dp else 16.dp
            ),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = msg.sender,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (msg.isUser) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
                    )
                    if (!msg.isUser) {
                        IconButton(
                            onClick = onSpeakClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = "Voice Speak",
                                tint = if (isSpeaking) HarvestGold80 else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = msg.text,
                    fontSize = 13.sp,
                    color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
