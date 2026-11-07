package com.example.ui.screens

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.model.CinemaClipItem
import com.example.model.MoveChoice
import com.example.ui.components.MoveChoiceChip
import com.example.ui.components.RunOrHidePollBox
import com.example.ui.components.SurvivorCommentCard
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
fun CinemaScreen(
    viewModel: SilentRunViewModel,
    onNavigateToSurvival: () -> Unit,
    onNavigateToBooster: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clips by viewModel.clips.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val mainClip = clips.firstOrNull()

    var userCommentText by remember { mutableStateOf("") }
    var selectedMoveForComment by remember { mutableStateOf(MoveChoice.HIDE) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("cinema_screen_scroll"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Creator Channel Profile Card with verified links
        item {
            ChannelHeader(
                onOpenTikTok = { viewModel.openUrl(context, viewModel.tiktokProfileUrl) },
                onOpenPaystack = { viewModel.openUrl(context, viewModel.paystackStoreUrl) },
                onOpenBooster = onNavigateToBooster
            )
        }

        // Main Video Player Card (Plays creature_clip.mp4 from user's TikTok)
        item {
            if (mainClip != null) {
                MainVideoPlayerCard(
                    clip = mainClip,
                    onVote = { choice -> viewModel.voteOnClip(mainClip.id, choice) },
                    onToggleLike = { viewModel.toggleLikeClip(mainClip.id) },
                    onNavigateToSurvival = onNavigateToSurvival,
                    onOpenPaystack = { viewModel.openUrl(context, viewModel.paystackStoreUrl) }
                )
            }
        }

        // Drop Your Move Comment Input Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("post_move_card"),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Drop Your Move Below! 👇",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "If you were with the night squad when the creature struck, what would you do?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MoveChoiceChip(
                            label = "Run 🏃",
                            selected = selectedMoveForComment == MoveChoice.RUN,
                            activeColor = WarningAmber,
                            onClick = { selectedMoveForComment = MoveChoice.RUN }
                        )
                        MoveChoiceChip(
                            label = "Hide 🤫",
                            selected = selectedMoveForComment == MoveChoice.HIDE,
                            activeColor = NeonCyan,
                            onClick = { selectedMoveForComment = MoveChoice.HIDE }
                        )
                        MoveChoiceChip(
                            label = "Decoy 🔊",
                            selected = selectedMoveForComment == MoveChoice.DECOY,
                            activeColor = BioGreen,
                            onClick = { selectedMoveForComment = MoveChoice.DECOY }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userCommentText,
                            onValueChange = { userCommentText = it },
                            placeholder = {
                                Text(
                                    stringResource(R.string.input_placeholder_move),
                                    color = TextTertiary,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_field"),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = OutlineBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = NeonCyan
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (userCommentText.isNotBlank()) {
                                    viewModel.addComment(selectedMoveForComment, userCommentText)
                                    userCommentText = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(NeonCyan, CircleShape)
                                .testTag("submit_move_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = stringResource(R.string.btn_post_move),
                                tint = SurfaceDark
                            )
                        }
                    }
                }
            }
        }

        // Survivor Comments Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.comments_header),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Top Survivor Tactics",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan
                )
            }
        }

        // Comments Stream
        items(comments) { comment ->
            SurvivorCommentCard(
                comment = comment,
                onUpvote = { viewModel.toggleUpvoteComment(comment.id) }
            )
        }

        // Blockbuster Catalog from Mopo Lee Pro Cinema (The Man Nobody Knows, Jungle Outpost, etc.)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "More Mopo Lee Pro Cinema Releases",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                clips.drop(1).forEach { clip ->
                    MovieReleaseCard(
                        clip = clip,
                        onOpenPaystack = { viewModel.openUrl(context, viewModel.paystackStoreUrl) },
                        onOpenTikTok = { viewModel.openUrl(context, viewModel.tiktokProfileUrl) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ChannelHeader(
    onOpenTikTok: () -> Unit,
    onOpenPaystack: () -> Unit,
    onOpenBooster: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .testTag("channel_header_card"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f))
                        .border(2.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎬",
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MOPO LEE PRO CINEMA 🍿❤️",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "@mopoleemovieclip1 • Action • Romance • Comedy • Drama",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // Bio quote from the creator's real profile
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🔥 Your home of movies and entertainment! Follow for more movie updates!",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonCyan,
                    modifier = Modifier.padding(10.dp),
                    fontWeight = FontWeight.Medium
                )
            }

            // 3 Fast Links Row (TikTok, Paystack Store, Algorithm Booster)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenTikTok,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("header_tiktok_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.2f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NeonCyan))
                ) {
                    Text(
                        text = "📱 TikTok",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onOpenPaystack,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("header_paystack_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarningAmber,
                        contentColor = SurfaceDark
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Paystack",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onOpenBooster,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("header_booster_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BioGreen.copy(alpha = 0.2f),
                        contentColor = BioGreen
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BioGreen))
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Booster",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainVideoPlayerCard(
    clip: CinemaClipItem,
    onVote: (MoveChoice) -> Unit,
    onToggleLike: () -> Unit,
    onNavigateToSurvival: () -> Unit,
    onOpenPaystack: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            videoViewRef?.stopPlayback()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("main_video_card"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
    ) {
        Column {
            // Video Player Area with Watermark
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            val mediaController = MediaController(ctx)
                            mediaController.setAnchorView(this)
                            setMediaController(null)
                            if (clip.videoRawResId != null) {
                                val uri = Uri.parse("android.resource://${ctx.packageName}/${clip.videoRawResId}")
                                setVideoURI(uri)
                                setOnPreparedListener { mp ->
                                    mp.isLooping = true
                                    start()
                                    isPlaying = true
                                }
                            }
                            videoViewRef = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Watermark branding across the top right
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.watermark_brand),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Overlay Controls (Play/Pause button)
                IconButton(
                    onClick = {
                        videoViewRef?.let { vv ->
                            if (vv.isPlaying) {
                                vv.pause()
                                isPlaying = false
                            } else {
                                vv.start()
                                isPlaying = true
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(38.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .testTag("toggle_play_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Playing" else "Paused",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Live Cinematic Badge
                Surface(
                    color = DangerCrimson.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "LIVE CINEMA CLIP",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Clip Description & Caption
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = clip.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = clip.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                // Hashtags
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    clip.tags.forEach { tag ->
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Action Bar (Likes, Challenge CTA, Paystack 1-Click Buy)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onToggleLike() }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (clip.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like clip",
                            tint = if (clip.isLiked) DangerCrimson else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${clip.likesCount / 1000}.${(clip.likesCount % 1000) / 100}K",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = onNavigateToSurvival,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = SurfaceDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("test_silence_cta_button")
                        ) {
                            Text(
                                text = "Silence Test 🤫",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = onOpenPaystack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WarningAmber,
                                contentColor = SurfaceDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("paystack_cta_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Paystack",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // The Central Dilemma Poll: Would you RUN or HIDE?
                RunOrHidePollBox(
                    clip = clip,
                    onVote = onVote
                )
            }
        }
    }
}

@Composable
fun MovieReleaseCard(
    clip: CinemaClipItem,
    onOpenPaystack: () -> Unit,
    onOpenTikTok: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("movie_release_${clip.id}"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                if (clip.thumbnailDrawableResId != null) {
                    Image(
                        painter = painterResource(id = clip.thumbnailDrawableResId),
                        contentDescription = clip.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Watermark on movie releases
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "MOPO LEE PRO CINEMA",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (clip.episodeTag != null) {
                    Surface(
                        color = WarningAmber,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = clip.episodeTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = SurfaceDark,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = clip.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = clip.caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenPaystack,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarningAmber,
                            contentColor = SurfaceDark
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "1-Click Paystack Pass",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onOpenTikTok,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceElevated,
                            contentColor = NeonCyan
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NeonCyan))
                    ) {
                        Text(
                            text = "TikTok Clip",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
