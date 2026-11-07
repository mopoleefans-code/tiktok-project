package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CreatureAlertLevel
import com.example.ui.theme.AbyssDark
import com.example.ui.theme.BioGreen
import com.example.ui.theme.DangerCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.SilentRunViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SurvivalScreen(
    viewModel: SilentRunViewModel,
    modifier: Modifier = Modifier
) {
    val survivalState by viewModel.survivalState.collectAsState()
    val currentDb by viewModel.soundDetector.currentDb.collectAsState()
    val waveforms by viewModel.soundDetector.waveformHistory.collectAsState()
    val isSimulated by viewModel.soundDetector.isSimulationActive.collectAsState()

    val currentZone = viewModel.zones[survivalState.selectedZoneIndex]
    val alertLevel = viewModel.getAlertLevel(currentDb, currentZone.thresholdDb)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleSimulationMode(false)
            viewModel.startSurvivalChallenge()
        } else {
            viewModel.toggleSimulationMode(true)
            viewModel.startSurvivalChallenge()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("survival_screen_scroll"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        // Top Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.survival_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.survival_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Zone Selector Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.select_zone_header),
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(viewModel.zones) { index, zone ->
                        val selected = index == survivalState.selectedZoneIndex
                        Surface(
                            onClick = { viewModel.selectZone(index) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) NeonCyan.copy(alpha = 0.2f) else SurfaceCard,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (selected) NeonCyan else OutlineBorder
                                )
                            ),
                            modifier = Modifier.testTag("zone_chip_$index")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = zone.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) NeonCyan else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Limit: ${zone.thresholdDb.toInt()} dB • ${zone.riskLevel}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) NeonCyan else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Radar and Audio Gauge Card
        item {
            AcousticRadarCard(
                survivalState = survivalState,
                currentDb = currentDb,
                thresholdDb = currentZone.thresholdDb,
                alertLevel = alertLevel,
                waveforms = waveforms
            )
        }

        // Primary Start/Stop CTA Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!survivalState.isRunning) {
                    Button(
                        onClick = {
                            if (!viewModel.soundDetector.hasRecordPermission() && !survivalState.forceSimulation) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                viewModel.startSurvivalChallenge()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_survival_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = AbyssDark
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.btn_start_survival),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.stopSurvivalChallenge() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("stop_survival_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerCrimson,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_stop_survival),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Caught Game Over Dialog / Banner
        if (survivalState.isCaught) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creature_caught_card"),
                    colors = CardDefaults.cardColors(containerColor = DangerCrimson.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(DangerCrimson)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Caught alert",
                            tint = DangerCrimson,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = stringResource(R.string.creature_status_caught),
                            style = MaterialTheme.typography.titleMedium,
                            color = DangerCrimson,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "You survived ${survivalState.secondsSurvived}s in ${currentZone.name}. Peak sound reached ${survivalState.peakDb.toInt()} dB.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                        Button(
                            onClick = { viewModel.startSurvivalChallenge() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DangerCrimson,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("retry_survival_button")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.btn_retry))
                        }
                    }
                }
            }
        }

        // Tactical Dilemma Alert (Random quick-reaction event during survival)
        if (survivalState.recentDilemmaPrompt != null && !survivalState.isCaught) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(WarningAmber)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚠️", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = survivalState.recentDilemmaPrompt ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarningAmber,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Survival Statistics Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = stringResource(R.string.stat_time_survived),
                    value = "${survivalState.secondsSurvived}s",
                    highlightColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.stat_best_streak),
                    value = "${survivalState.bestStreakSeconds}s",
                    highlightColor = BioGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.stat_rating),
                    value = viewModel.calculateStealthRank(survivalState.secondsSurvived, survivalState.peakDb).substringBefore(" "),
                    highlightColor = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Noise Simulation Testing Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Simulation Mode (Test Audio Triggers)",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = survivalState.forceSimulation || isSimulated,
                            onCheckedChange = { active -> viewModel.toggleSimulationMode(active) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = SurfaceElevated
                            )
                        )
                    }

                    Text(
                        text = "Trigger simulated noise spikes to test creature detection proximity without speaking aloud.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.triggerSimulationNoise("BREATH") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sim_breath_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Breathe (+16dB)", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.triggerSimulationNoise("FOOTSTEP") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sim_footstep_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Step (+26dB)", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.triggerSimulationNoise("BRANCH") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sim_branch_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Snap (+38dB)", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AcousticRadarCard(
    survivalState: com.example.viewmodel.SurvivalUiState,
    currentDb: Float,
    thresholdDb: Float,
    alertLevel: CreatureAlertLevel,
    waveforms: List<Float>
) {
    val statusColor by animateColorAsState(
        targetValue = when (alertLevel) {
            CreatureAlertLevel.SAFE -> BioGreen
            CreatureAlertLevel.CAUTION -> WarningAmber
            CreatureAlertLevel.DANGER -> DangerCrimson
            CreatureAlertLevel.CAUGHT -> DangerCrimson
        },
        label = "statusColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "radarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("acoustic_radar_card"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Status and Alert Level Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(statusColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (alertLevel) {
                            CreatureAlertLevel.SAFE -> "ALL CLEAR (Safe)"
                            CreatureAlertLevel.CAUTION -> "CAUTION (Creature Alert)"
                            CreatureAlertLevel.DANGER -> "DANGER (Immediate Vicinity)"
                            CreatureAlertLevel.CAUGHT -> "STRIKE! Detected!"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${currentDb.toInt()} dB",
                    style = MaterialTheme.typography.headlineMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // Radar Display Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AbyssDark),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = minOf(size.width, size.height) * 0.44f

                    // Concentric Range Rings
                    for (i in 1..4) {
                        val r = maxRadius * (i / 4f)
                        drawCircle(
                            color = NeonCyan.copy(alpha = 0.15f),
                            radius = r,
                            center = center,
                            style = Stroke(width = 1.5f)
                        )
                    }

                    // Crosshair Lines
                    drawLine(
                        color = NeonCyan.copy(alpha = 0.15f),
                        start = Offset(center.x - maxRadius, center.y),
                        end = Offset(center.x + maxRadius, center.y),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = NeonCyan.copy(alpha = 0.15f),
                        start = Offset(center.x, center.y - maxRadius),
                        end = Offset(center.x, center.y + maxRadius),
                        strokeWidth = 1f
                    )

                    // Sweeping Radar Line
                    val sweepRad = Math.toRadians(sweepAngle.toDouble())
                    val endX = center.x + maxRadius * cos(sweepRad).toFloat()
                    val endY = center.y + maxRadius * sin(sweepRad).toFloat()
                    drawLine(
                        color = NeonCyan.copy(alpha = 0.7f),
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = 2f
                    )

                    // Creature Blip (moves closer to center as creatureDistance decreases)
                    val creatureNormDist = (survivalState.creatureDistanceMeters / 100f).coerceIn(0.1f, 1f)
                    val blipRadius = maxRadius * creatureNormDist
                    val blipAngle = 45.0 // upper right
                    val blipX = center.x + blipRadius * cos(Math.toRadians(blipAngle)).toFloat()
                    val blipY = center.y - blipRadius * sin(Math.toRadians(blipAngle)).toFloat()

                    drawCircle(
                        color = DangerCrimson,
                        radius = if (survivalState.isRunning) 6f else 4f,
                        center = Offset(blipX, blipY)
                    )
                    drawCircle(
                        color = DangerCrimson.copy(alpha = 0.35f),
                        radius = if (survivalState.isRunning) 14f else 8f,
                        center = Offset(blipX, blipY)
                    )

                    // User Center Blip
                    drawCircle(
                        color = NeonCyan,
                        radius = 4f,
                        center = center
                    )
                }

                // Overlay Text for Distance
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "CREATURE PROXIMITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${survivalState.creatureDistanceMeters.toInt()} METERS",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (survivalState.creatureDistanceMeters < 30) DangerCrimson else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "TARGET LOCK",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                )
            }

            // Real-time Audio Waveform Display Canvas
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ACOUSTIC OSCILLOSCOPE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "Threshold: ${thresholdDb.toInt()} dB",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarningAmber
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AbyssDark)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barWidth = size.width / waveforms.size
                        waveforms.forEachIndexed { index, amp ->
                            val barHeight = (size.height * amp).coerceAtLeast(4f)
                            val left = index * barWidth
                            val top = (size.height - barHeight) / 2f
                            drawRect(
                                color = if (currentDb > thresholdDb) DangerCrimson else NeonCyan,
                                topLeft = Offset(left + 2f, top),
                                size = androidx.compose.ui.geometry.Size(barWidth - 4f, barHeight)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    highlightColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = highlightColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
