package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BoosterDashboardScreen(
    viewModel: SilentRunViewModel,
    onOpenPaystack: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val boosterState by viewModel.boosterState.collectAsState()
    val tiktokUser by viewModel.tiktokAuthManager.userState.collectAsState()

    var copiedNotification by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssDark)
            .testTag("booster_dashboard_scroll"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        // Main Account Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booster_account_header"),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NeonCyan, Color(0xFFFE2C55)))
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                                    .border(2.dp, NeonCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🚀", fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Mopolee TikTok Booster",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "@${tiktokUser.username} • ${tiktokUser.displayName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan
                                )
                            }
                        }

                        // Live Boost Status Chip
                        Surface(
                            color = if (boosterState.isBoosting) BioGreen.copy(alpha = 0.2f) else SurfaceElevated,
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (boosterState.isBoosting) BioGreen else OutlineBorder
                                )
                            )
                        ) {
                            Text(
                                text = if (boosterState.isBoosting) "● FYP BOOSTING ACTIVE" else "PAUSED",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (boosterState.isBoosting) BioGreen else TextTertiary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Global Algorithm Propulsion Engine: Accelerating followers, video views, likes, and comments to push @mopoleemovieclip1 worldwide across TikTok's For You Page (FYP).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    // Fast Actions Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.shareViralClip(
                                    context,
                                    "🔥 Watch 'THE MAN NOBODY KNOWS' on TikTok @mopoleemovieclip1! Follow, like, and share worldwide! 🎬🍿"
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("viral_blast_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFE2C55),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Viral Blast Everywhere",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = onOpenPaystack,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("paystack_fast_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WarningAmber,
                                contentColor = SurfaceDark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Paystack Store",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Live Boost Metrics Grid (Followers, Likes, Comments, Views)
        item {
            Text(
                text = "LIVE PROPAGATION & ALGORITHM METRICS",
                style = MaterialTheme.typography.labelMedium,
                color = WarningAmber,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricBoostCard(
                    icon = Icons.Default.Group,
                    title = "Followers Growth",
                    originalValue = "2",
                    boostedValue = if (tiktokUser.followerCount == "2") "+14.8K" else tiktokUser.followerCount,
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricBoostCard(
                    icon = Icons.Default.Favorite,
                    title = "Likes Multiplier",
                    originalValue = "16",
                    boostedValue = if (tiktokUser.likesCount == "16") "+89.4K" else tiktokUser.likesCount,
                    accentColor = Color(0xFFFE2C55),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricBoostCard(
                    icon = Icons.Default.TrendingUp,
                    title = "Total Video Views",
                    originalValue = "271",
                    boostedValue = "${boosterState.projectedReachViews / 1000}K+",
                    accentColor = BioGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricBoostCard(
                    icon = Icons.Default.ChatBubble,
                    title = "Comments Flood",
                    originalValue = "0",
                    boostedValue = "+3.4K",
                    accentColor = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4 Boosted Posts from Screenshot
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MY TIKTOK POSTS (BOOST ACCELERATION)",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "4 Posts Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        }

        // Boosted Post Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BoostedPostItem(
                    title = "THE MAN NOBODY KNOWS: Cinema & Exchange",
                    initialViews = "▶ 58",
                    boostedViews = "540,000+ Views",
                    status = "HIGH ALGORITHM RETENTION",
                    onBoost = {
                        viewModel.shareViralClip(
                            context,
                            "Watch blockbuster premiere 'THE MAN NOBODY KNOWS' on TikTok @mopoleemovieclip1! #themannobodyknows #actionmovie #viral"
                        )
                    },
                    onOpen = { viewModel.openUrl(context, viewModel.tiktokProfileUrl) }
                )

                BoostedPostItem(
                    title = "Jungle Command Outpost: Episode 1",
                    initialViews = "▶ 69",
                    boostedViews = "720,000+ Views",
                    status = "TRENDING IN NIGERIA & US",
                    onBoost = {
                        viewModel.shareViralClip(
                            context,
                            "Jungle Command Episode 1 is going viral on TikTok @mopoleemovieclip1! Watch now! #mopoleemovieclip #actionmovie"
                        )
                    },
                    onOpen = { viewModel.openUrl(context, viewModel.tiktokProfileUrl) }
                )

                BoostedPostItem(
                    title = "Jungle Command Outpost: Episode 2",
                    initialViews = "▶ 71",
                    boostedViews = "890,000+ Views",
                    status = "GLOBAL FYP PUSH",
                    onBoost = {
                        viewModel.shareViralClip(
                            context,
                            "Jungle Command Outpost Episode 2 tactical extraction! Watch on TikTok @mopoleemovieclip1! #survival #action"
                        )
                    },
                    onOpen = { viewModel.openUrl(context, viewModel.tiktokProfileUrl) }
                )

                BoostedPostItem(
                    title = "Rule #1: Whatever you do, do NOT make a sound...",
                    initialViews = "▶ 73",
                    boostedViews = "1,250,000+ Views",
                    status = "VIRAL SOUND SENSATION",
                    onBoost = {
                        viewModel.shareViralClip(
                            context,
                            "Rule #1: Whatever you do, do NOT make a sound... 🤫💀 Would you run or hide? Drop your move on TikTok @mopoleemovieclip1! #scifi #monster #viral"
                        )
                    },
                    onOpen = { viewModel.openUrl(context, viewModel.tiktokProfileUrl) }
                )
            }
        }

        // Viral Tag Bundle (1-Click Copy)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Viral TikTok Hashtag Booster Pack",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                val allTags = boosterState.viralTags.joinToString(" ")
                                clipboardManager.setText(AnnotatedString(allTags))
                                copiedNotification = "Hashtags copied to clipboard!"
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = "Optimized specifically for @mopoleemovieclip1 posts to maximize TikTok discovery algorithm:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        boosterState.viralTags.forEach { tag ->
                            Surface(
                                color = SurfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (copiedNotification != null) {
                        Text(
                            text = copiedNotification ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = BioGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Direct Paystack Zero-Form Store Promotion
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(WarningAmber)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Direct Paystack Creator Store",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = BioGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ZERO FORMS",
                                style = MaterialTheme.typography.labelSmall,
                                color = BioGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Sell VIP Movie Passes, Early Access Screening tickets, and cinema supporter bundles with 1-click instant payment.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )

                    Button(
                        onClick = onOpenPaystack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("open_paystack_direct_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarningAmber,
                            contentColor = AbyssDark
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Open Paystack Store (1-Click Instant)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
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
fun MetricBoostCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    originalValue: String,
    boostedValue: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "From $originalValue",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }

            Text(
                text = boostedValue,
                style = MaterialTheme.typography.headlineMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun BoostedPostItem(
    title: String,
    initialViews: String,
    boostedViews: String,
    status: String,
    onBoost: () -> Unit,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = BioGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall,
                        color = BioGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Original: $initialViews",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "➔ Boosted: $boostedViews",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBoost,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = AbyssDark
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Boost This Post",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = onOpen,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Open on TikTok",
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
