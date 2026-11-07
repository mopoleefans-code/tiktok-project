package com.example.ui.screens

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.CinemaClipItem
import com.example.model.MoveChoice
import com.example.ui.components.RunOrHidePollBox
import com.example.ui.theme.AbyssDark
import com.example.ui.theme.BioGreen
import com.example.ui.theme.DangerCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.SilentRunViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TikTokFeedScreen(
    viewModel: SilentRunViewModel,
    onNavigateToSurvival: () -> Unit,
    onOpenPaystackStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clips by viewModel.clips.collectAsState()
    val currentIndex by viewModel.currentFeedIndex.collectAsState()
    val activeClip = clips.getOrNull(currentIndex) ?: clips.first()

    var showCommentSheet by remember { mutableStateOf(false) }
    var isBookmarked by remember { mutableStateOf(false) }
    var isFollowing by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "discRotate")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "discRot"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -50) {
                        // Swipe Up -> Next Video
                        if (currentIndex < clips.size - 1) {
                            viewModel.setFeedIndex(currentIndex + 1)
                        }
                    } else if (dragAmount > 50) {
                        // Swipe Down -> Prev Video
                        if (currentIndex > 0) {
                            viewModel.setFeedIndex(currentIndex - 1)
                        }
                    }
                }
            }
            .testTag("tiktok_feed_screen")
    ) {
        // Video / Backdrop Layer
        if (activeClip.videoRawResId != null) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        val uri = Uri.parse("android.resource://${ctx.packageName}/${activeClip.videoRawResId}")
                        setVideoURI(uri)
                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            start()
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else if (activeClip.thumbnailDrawableResId != null) {
            Image(
                painter = painterResource(id = activeClip.thumbnailDrawableResId),
                contentDescription = activeClip.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Gradient Shadow & Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Following",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "For You",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom Gradient Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
                .align(Alignment.BottomCenter)
        )

        // Bottom Info Section (Creator, Title, Poll, Audio ticker)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 80.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Creator Handle & Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { viewModel.openUrl(context, viewModel.tiktokProfileUrl) }
            ) {
                Text(
                    text = "@mopoleemovieclip1",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified Creator",
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Caption / Title
            Text(
                text = activeClip.caption,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.95f),
                maxLines = 3
            )

            // Hashtags
            Text(
                text = activeClip.tags.joinToString(" "),
                style = MaterialTheme.typography.labelSmall,
                color = NeonCyan,
                fontWeight = FontWeight.Bold
            )

            // "Would you RUN or HIDE?" Poll Sticker on Video
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(WarningAmber)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Would you RUN or HIDE?",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            onClick = { viewModel.voteOnClip(activeClip.id, MoveChoice.RUN) },
                            color = if (activeClip.userVote == MoveChoice.RUN) WarningAmber else SurfaceDark,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🏃 RUN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeClip.userVote == MoveChoice.RUN) SurfaceDark else Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            onClick = { viewModel.voteOnClip(activeClip.id, MoveChoice.HIDE) },
                            color = if (activeClip.userVote == MoveChoice.HIDE) NeonCyan else SurfaceDark,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🤫 HIDE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeClip.userVote == MoveChoice.HIDE) SurfaceDark else Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Audio Ticker with Disc
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = activeClip.soundTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
        }

        // Right-Side Action Rail (Avatar, Like, Comments, Bookmark, Paystack, Audio Disc)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Avatar with Follow Plus
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clickable { viewModel.openUrl(context, viewModel.tiktokProfileUrl) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎬", fontSize = 22.sp)
                }

                if (!isFollowing) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(DangerCrimson)
                            .clickable { isFollowing = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Follow",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Like Button
            ActionRailItem(
                icon = if (activeClip.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = "${activeClip.likesCount / 1000}.${(activeClip.likesCount % 1000) / 100}K",
                iconColor = if (activeClip.isLiked) DangerCrimson else Color.White,
                onClick = { viewModel.toggleLikeClip(activeClip.id) }
            )

            // Comments Button
            ActionRailItem(
                icon = Icons.Default.ChatBubble,
                label = "2.4K",
                onClick = { showCommentSheet = true }
            )

            // Bookmark Button
            ActionRailItem(
                icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                label = "Save",
                iconColor = if (isBookmarked) WarningAmber else Color.White,
                onClick = { isBookmarked = !isBookmarked }
            )

            // 1-Click Paystack Instant Buy on Video
            ActionRailItem(
                icon = Icons.Default.ElectricBolt,
                label = "Paystack",
                iconColor = WarningAmber,
                onClick = onOpenPaystackStore
            )

            // Spinning Vinyl Disc
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(2.dp, Color.Gray, CircleShape)
                    .rotate(discRotation),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                )
            }
        }

        // Comments Bottom Sheet
        if (showCommentSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCommentSheet = false },
                containerColor = SurfaceDark,
                dragHandle = null
            ) {
                TikTokCommentsSheetContent(
                    viewModel = viewModel,
                    onClose = { showCommentSheet = false }
                )
            }
        }
    }
}

@Composable
fun ActionRailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    iconColor: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TikTokCommentsSheetContent(
    viewModel: SilentRunViewModel,
    onClose: () -> Unit
) {
    val comments by viewModel.comments.collectAsState()
    var commentText by remember { mutableStateOf("") }
    var selectedMove by remember { mutableStateOf(MoveChoice.HIDE) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(520.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${comments.size} Survivor Responses",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClose) {
                Text("✕", color = TextSecondary, fontSize = 18.sp)
            }
        }

        // Comments List
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(comments.size) { i ->
                val comment = comments[i]
                com.example.ui.components.SurvivorCommentCard(
                    comment = comment,
                    onUpvote = { viewModel.toggleUpvoteComment(comment.id) }
                )
            }
        }

        // Add Comment Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Drop your move...", color = TextSecondary, fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (commentText.isNotBlank()) {
                        viewModel.addComment(selectedMove, commentText)
                        commentText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = CircleShape,
                modifier = Modifier.size(46.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text("➤", color = SurfaceDark, fontSize = 16.sp)
            }
        }
    }
}
