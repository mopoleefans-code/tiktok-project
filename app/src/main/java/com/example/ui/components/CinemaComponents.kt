package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CinemaClipItem
import com.example.model.MoveChoice
import com.example.model.SurvivorComment
import com.example.ui.theme.BioGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarningAmber

@Composable
fun MoveChoiceChip(
    label: String,
    selected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) activeColor.copy(alpha = 0.2f) else SurfaceElevated,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (selected) activeColor else OutlineBorder)
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) activeColor else TextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun RunOrHidePollBox(
    clip: CinemaClipItem,
    onVote: (MoveChoice) -> Unit
) {
    val totalVotes = (clip.runVotes + clip.hideVotes).coerceAtLeast(1)
    val runPct = (clip.runVotes.toFloat() / totalVotes * 100).toInt()
    val hidePct = 100 - runPct

    val animatedRunPct by animateFloatAsState(targetValue = runPct / 100f, label = "runPct")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("run_hide_poll_box"),
        color = SurfaceElevated,
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
                    text = "SURVIVOR DILEMMA: WOULD YOU RUN OR HIDE?",
                    style = MaterialTheme.typography.labelMedium,
                    color = WarningAmber,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(totalVotes / 1000)}K Votes",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }

            // Two Big Interactive Choice Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onVote(MoveChoice.RUN) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("vote_run_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (clip.userVote == MoveChoice.RUN) WarningAmber else SurfaceCard,
                        contentColor = if (clip.userVote == MoveChoice.RUN) SurfaceDark else TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (clip.userVote == MoveChoice.RUN) WarningAmber else OutlineBorder
                        )
                    )
                ) {
                    Text(
                        text = "🏃 RUN ($runPct%)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { onVote(MoveChoice.HIDE) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("vote_hide_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (clip.userVote == MoveChoice.HIDE) NeonCyan else SurfaceCard,
                        contentColor = if (clip.userVote == MoveChoice.HIDE) SurfaceDark else TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (clip.userVote == MoveChoice.HIDE) NeonCyan else OutlineBorder
                        )
                    )
                ) {
                    Text(
                        text = "🤫 HIDE ($hidePct%)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Dual Color Live Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { animatedRunPct },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = WarningAmber,
                    trackColor = NeonCyan
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Run: $runPct%",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarningAmber
                    )
                    Text(
                        text = "Hide: $hidePct%",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                }
            }

            AnimatedVisibility(visible = clip.userVote != null) {
                Text(
                    text = if (clip.userVote == MoveChoice.RUN)
                        "⚡ You chose RUN! Speed and route awareness are critical."
                    else
                        "🤫 You chose HIDE! Absolute audio discipline is your shield.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (clip.userVote == MoveChoice.RUN) WarningAmber else NeonCyan,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SurvivorCommentCard(
    comment: SurvivorComment,
    onUpvote: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when (comment.moveChoice) {
                                    MoveChoice.RUN -> WarningAmber.copy(alpha = 0.2f)
                                    MoveChoice.HIDE -> NeonCyan.copy(alpha = 0.2f)
                                    MoveChoice.DECOY -> BioGreen.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comment.avatarInitials,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (comment.moveChoice) {
                                MoveChoice.RUN -> WarningAmber
                                MoveChoice.HIDE -> NeonCyan
                                MoveChoice.DECOY -> BioGreen
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = comment.author,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = comment.timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                }

                Surface(
                    color = when (comment.moveChoice) {
                        MoveChoice.RUN -> WarningAmber.copy(alpha = 0.15f)
                        MoveChoice.HIDE -> NeonCyan.copy(alpha = 0.15f)
                        MoveChoice.DECOY -> BioGreen.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when (comment.moveChoice) {
                            MoveChoice.RUN -> "🏃 RUN"
                            MoveChoice.HIDE -> "🤫 HIDE"
                            MoveChoice.DECOY -> "🔊 DECOY"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = when (comment.moveChoice) {
                            MoveChoice.RUN -> WarningAmber
                            MoveChoice.HIDE -> NeonCyan
                            MoveChoice.DECOY -> BioGreen
                        },
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = comment.commentText,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onUpvote,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Upvote",
                        tint = if (comment.isUpvoted) NeonCyan else TextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "${comment.upvotes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (comment.isUpvoted) NeonCyan else TextTertiary
                )
            }
        }
    }
}
