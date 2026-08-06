package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// --- 1. 5-4-3-2-1 Grounding Card ---
data class GroundingStep(
    val count: Int,
    val sense: String,
    val prompt: String,
    val detail: String,
    val icon: ImageVector
)

@Composable
fun Grounding54321Section() {
    val steps = remember {
        listOf(
            GroundingStep(5, "Sight", "Look around and name 5 things you can see.", "E.g., A clock, a soft pillow, a green leaf, light through window.", Icons.Default.Visibility),
            GroundingStep(4, "Touch", "Feel 4 physical textures near you.", "E.g., Your soft shirt, smooth desk edge, cool metal ring, cozy blanket.", Icons.Default.TouchApp),
            GroundingStep(3, "Sound", "Listen closely for 3 distinct sounds.", "E.g., Quiet fan hum, distant birds, your calm heartbeat.", Icons.Default.Hearing),
            GroundingStep(2, "Smell", "Notice 2 scents or familiar smells.", "E.g., Fresh air, warm tea, lavender lotion.", Icons.Default.FilterVintage),
            GroundingStep(1, "Taste", "Notice 1 taste or take a sip of water.", "E.g., Clean fresh water, cool mint.", Icons.Default.LocalCafe)
        )
    }

    var currentStepIndex by remember { mutableStateOf(0) }
    val activeStep = steps[currentStepIndex]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("grounding_54321_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "5-4-3-2-1 Sensory Grounding",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Step ${currentStepIndex + 1} of 5",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Step Content Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(18.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = activeStep.icon,
                                contentDescription = activeStep.sense,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Text(
                        text = "Find ${activeStep.count} Things: ${activeStep.sense}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = activeStep.prompt,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = activeStep.detail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentStepIndex > 0) currentStepIndex--
                    },
                    enabled = currentStepIndex > 0,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Previous")
                }

                Button(
                    onClick = {
                        if (currentStepIndex < steps.size - 1) currentStepIndex++ else currentStepIndex = 0
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (currentStepIndex < steps.size - 1) "Next Sense" else "Restart Reset")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = if (currentStepIndex < steps.size - 1) Icons.Default.ArrowForward else Icons.Default.Refresh, contentDescription = null)
                }
            }
        }
    }
}

// --- 2. Visual Box Breathing ---
@Composable
fun BoxBreathingSection() {
    var isBreathingActive by remember { mutableStateOf(false) }
    var breathPhase by remember { mutableStateOf("Breathe In...") }
    var secondsInPhase by remember { mutableStateOf(4) }

    // Breathing Animation Scale (0.6f to 1.1f)
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathScale"
    )

    LaunchedEffect(isBreathingActive) {
        if (isBreathingActive) {
            val phases = listOf("Breathe In...", "Hold Breath", "Breathe Out...", "Rest & Calm")
            var phaseIdx = 0
            while (isBreathingActive) {
                breathPhase = phases[phaseIdx]
                for (s in 4 downTo 1) {
                    secondsInPhase = s
                    delay(1000L)
                }
                phaseIdx = (phaseIdx + 1) % phases.size
            }
        } else {
            breathPhase = "Tap Start to Begin"
            secondsInPhase = 4
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("box_breathing_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Visual Box Breathing",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { isBreathingActive = !isBreathingActive },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBreathingActive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (isBreathingActive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (isBreathingActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBreathingActive) "Pause" else "Start Guide")
                }
            }

            // Expanding Breathing Circle
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                )

                // Animated Circle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(if (isBreathingActive) breathScale else 0.8f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )

                // Text overlay
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = breathPhase,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    if (isBreathingActive) {
                        Text(
                            text = "$secondsInPhase",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Text(
                text = "Gentle 4-4-4-4 rhythm to soothe your nervous system.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- 3. Tactile Fidget Touch Pad ---
@Composable
fun TactileFidgetSection() {
    var ripples by remember { mutableStateOf(listOf<Offset>()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tactile_fidget_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tactile Sensory Touch Pad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Tap or sweep across the calm surface for soothing visual ripples.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val primaryColor = MaterialTheme.colorScheme.primary
            val primaryContainer = MaterialTheme.colorScheme.primaryContainer

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(primaryContainer.copy(alpha = 0.4f))
                    .border(
                        width = 1.dp,
                        color = primaryColor.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            ripples = ripples.takeLast(10) + offset
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    ripples.forEach { point ->
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.25f),
                            radius = 60f,
                            center = point
                        )
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.5f),
                            radius = 35f,
                            center = point
                        )
                        drawCircle(
                            color = primaryColor,
                            radius = 12f,
                            center = point
                        )
                    }
                }

                if (ripples.isEmpty()) {
                    Text(
                        text = "• Tap Anywhere to Create Calming Ripples •",
                        style = MaterialTheme.typography.labelLarge,
                        color = primaryColor.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        text = "Calming Touch Responsive",
                        style = MaterialTheme.typography.labelMedium,
                        color = primaryColor.copy(alpha = 0.5f),
                        modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)
                    )
                }
            }
        }
    }
}
