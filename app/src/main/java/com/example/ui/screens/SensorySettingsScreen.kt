package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.UserSettingsEntity
import com.example.ui.components.FocusModeComponent
import com.example.ui.theme.*

@Composable
fun SensorySettingsScreen(
    userSettings: UserSettingsEntity,
    onUpdateThemePalette: (String) -> Unit,
    onUpdateHighContrast: (Boolean) -> Unit,
    onUpdateReduceAnimations: (Boolean) -> Unit,
    onUpdateFocusMode: (Boolean) -> Unit,
    onUpdateFontScale: (Float) -> Unit,
    onUpdateSpeechSettings: (pitch: Float, rate: Float) -> Unit,
    onTestVoice: (String) -> Unit,
    onExportCsvData: () -> String
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var pitchState by remember(userSettings.speechPitch) { mutableFloatStateOf(userSettings.speechPitch) }
    var rateState by remember(userSettings.speechRate) { mutableFloatStateOf(userSettings.speechRate) }
    var exportPreviewText by remember { mutableStateOf<String?>(null) }
    var showCopiedToast by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("sensory_settings_list"),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // --- SECTION 1: Color Palette & Visual Mode ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_theme_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Calm Color Palettes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Choose a soft, glare-free visual theme that suits your sensory preferences.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val palettes = listOf(
                        Triple("SAGE", "Soft Sage Green", SagePrimary),
                        Triple("SAND", "Warm Sand Ochre", SandPrimary),
                        Triple("LAVENDER", "Dusk Lavender", LavenderPrimary),
                        Triple("DUSK", "Night Dusk Mode", DuskPrimary)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        palettes.forEach { (key, label, sampleColor) ->
                            val isSelected = userSettings.themePalette.uppercase() == key
                            Surface(
                                onClick = { onUpdateThemePalette(key) },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("theme_option_$key")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(sampleColor)
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- SECTION 2: Accessibility & High Contrast ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_accessibility_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessibilityNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Sensory & High Legibility",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    FocusModeComponent(
                        isFocusModeActive = userSettings.highContrast && userSettings.reduceAnimations,
                        onToggleFocusMode = { onUpdateFocusMode(it) }
                    )

                    // High Contrast Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "High Contrast Mode",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Enforces crisp outlines and solid text contrast.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = userSettings.highContrast,
                            onCheckedChange = { onUpdateHighContrast(it) },
                            modifier = Modifier.testTag("switch_high_contrast")
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Reduce Motion Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Reduce Motion & Animations",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Simplifies screen transitions to prevent motion fatigue.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = userSettings.reduceAnimations,
                            onCheckedChange = { onUpdateReduceAnimations(it) },
                            modifier = Modifier.testTag("switch_reduce_motion")
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Font Size Scale Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Text & Icon Scale",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format("%.1fx", userSettings.fontScale),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Slider(
                            value = userSettings.fontScale,
                            onValueChange = { onUpdateFontScale(it) },
                            valueRange = 0.9f..1.3f,
                            steps = 3,
                            modifier = Modifier.testTag("slider_font_scale")
                        )
                    }
                }
            }
        }

        // --- SECTION 3: Text-To-Speech Speech Voice Settings ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_speech_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "AAC Speech Voice Tuning",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Customize the pitch and speed of spoken communication cards.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Speech Speed Rate Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Speech Speed (Slower = Calmer)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text(String.format("%.2fx", rateState), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = rateState,
                            onValueChange = {
                                rateState = it
                                onUpdateSpeechSettings(pitchState, rateState)
                            },
                            valueRange = 0.5f..1.3f,
                            steps = 7,
                            modifier = Modifier.testTag("slider_speech_rate")
                        )
                    }

                    // Speech Pitch Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Speech Pitch", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text(String.format("%.2fx", pitchState), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = pitchState,
                            onValueChange = {
                                pitchState = it
                                onUpdateSpeechSettings(pitchState, rateState)
                            },
                            valueRange = 0.6f..1.4f,
                            steps = 7,
                            modifier = Modifier.testTag("slider_speech_pitch")
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onTestVoice("Hello! This is your customized calm AAC voice.")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("test_voice_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Test Speech Voice")
                    }
                }
            }
        }

        // --- SECTION 5: Data Export & Therapist Reports ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_export_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Data Export & Therapist Reports",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Export your daily habits, sensory battery history, and safe space journal reflections as a CSV or text file for personal tracking or sharing with therapists.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val csv = onExportCsvData()
                                clipboardManager.setText(AnnotatedString(csv))
                                showCopiedToast = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("copy_csv_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy CSV")
                        }

                        Button(
                            onClick = {
                                val csv = onExportCsvData()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_SUBJECT, "Calm Space Wellness & Battery Report")
                                    putExtra(Intent.EXTRA_TEXT, csv)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Export Report via")
                                try {
                                    context.startActivity(shareIntent)
                                } catch (e: Exception) {
                                    // fallback
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("share_csv_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Report")
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            exportPreviewText = onExportCsvData()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("preview_export_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Preview Export Data")
                    }

                    if (showCopiedToast) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "✓ Export data copied to clipboard successfully!",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (exportPreviewText != null) {
        AlertDialog(
            onDismissRequest = { exportPreviewText = null },
            title = { Text("Export Data Preview") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = exportPreviewText ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { exportPreviewText = null }) {
                    Text("Close")
                }
            }
        )
    }
}
