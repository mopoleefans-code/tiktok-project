package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CinemaClipItem
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.SilentRunViewModel

@Composable
fun TikTokProfileScreen(
    viewModel: SilentRunViewModel,
    onOpenTikTokLogin: () -> Unit,
    onOpenPaystackStore: () -> Unit,
    onSelectClip: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clips by viewModel.clips.collectAsState()
    val tiktokUser by viewModel.tiktokAuthManager.userState.collectAsState()
    var selectedProfileTab by remember { mutableIntStateOf(0) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("tiktok_profile_screen")
    ) {
        // Top Header matching TikTok screenshot
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showEditProfileDialog = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showEditProfileDialog = true }
            ) {
                Text(
                    text = tiktokUser.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenTikTokLogin,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Add / Login",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = { showEditProfileDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Profile Avatar with "What's up?" bubble & Blue Plus
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                // Profile Avatar (Man Nobody Knows poster art)
                Image(
                    painter = painterResource(id = R.drawable.poster_the_man_nobody_knows),
                    contentDescription = "Profile Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color.LightGray, CircleShape)
                        .clickable { showEditProfileDialog = true }
                )

                // Blue Plus Circle Badge
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00B0FF))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { onOpenTikTokLogin() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Login / Add",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Handle (Dynamic from tiktokUser)
        Text(
            text = "@${tiktokUser.username}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 6.dp)
                .clickable { showEditProfileDialog = true }
        )

        // Stats Row (Dynamic following, follower, likes)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TikTokStatColumn(number = tiktokUser.followingCount, label = "Following")
            Spacer(modifier = Modifier.width(32.dp))
            TikTokStatColumn(number = tiktokUser.followerCount, label = "Follower")
            Spacer(modifier = Modifier.width(32.dp))
            TikTokStatColumn(number = tiktokUser.likesCount, label = "Likes")
        }

        // Action Buttons Row (Edit Profile & Boost / Paystack Store)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showEditProfileDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("edit_profile_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF1F1F1),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "Edit Profile ✏️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    // Instantly cycle through boost tiers (14.8K -> 128K -> 1.4M -> 5.6M)
                    viewModel.tiktokAuthManager.applyNextBoostTier()
                },
                modifier = Modifier
                    .weight(1.2f)
                    .height(42.dp)
                    .testTag("apply_boost_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFE2C55),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Boost Stats 🚀 (${tiktokUser.followerCount})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = onOpenPaystackStore,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("profile_paystack_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00C853),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp)
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
                    fontSize = 12.sp
                )
            }
        }

        // Live Boost Confirmation Ribbon
        if (tiktokUser.followerCount != "2" || tiktokUser.likesCount != "16") {
            Surface(
                color = Color(0xFFFE2C55).copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥 BOOST APPLIED: ${tiktokUser.followerCount} Followers • ${tiktokUser.likesCount} Likes",
                        color = Color(0xFFFE2C55),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Reset ↺",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.tiktokAuthManager.resetToOriginalStats() }
                    )
                }
            }
        }

        // Bio section matching EXACT text in screenshot
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            tiktokUser.bio.lines().forEach { line ->
                Text(
                    text = line,
                    fontSize = 13.sp,
                    color = Color.Black,
                    fontWeight = if (line.contains("MOPO LEE")) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        // Tab Navigation Bar (Grid, Repost, Private, Bookmark, Heart)
        TabRow(
            selectedTabIndex = selectedProfileTab,
            containerColor = Color.White,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedProfileTab]),
                    color = Color.Black,
                    height = 2.dp
                )
            }
        ) {
            Tab(
                selected = selectedProfileTab == 0,
                onClick = { selectedProfileTab = 0 },
                icon = { Icon(Icons.Default.GridOn, contentDescription = "Videos", modifier = Modifier.size(20.dp)) }
            )
            Tab(
                selected = selectedProfileTab == 1,
                onClick = { selectedProfileTab = 1 },
                icon = { Icon(Icons.Default.Repeat, contentDescription = "Reposts", modifier = Modifier.size(20.dp)) }
            )
            Tab(
                selected = selectedProfileTab == 2,
                onClick = { selectedProfileTab = 2 },
                icon = { Icon(Icons.Default.Lock, contentDescription = "Private", modifier = Modifier.size(20.dp)) }
            )
            Tab(
                selected = selectedProfileTab == 3,
                onClick = { selectedProfileTab = 3 },
                icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = "Saved", modifier = Modifier.size(20.dp)) }
            )
            Tab(
                selected = selectedProfileTab == 4,
                onClick = { selectedProfileTab = 4 },
                icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Liked", modifier = Modifier.size(20.dp)) }
            )
        }

        // Video Grid matching the exact post thumbnails and view counts
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
        ) {
            itemsIndexed(clips) { index, clip ->
                TikTokVideoGridItem(
                    clip = clip,
                    onClick = { onSelectClip(index) }
                )
            }
        }
    }

    // Edit Profile Modal Dialog
    if (showEditProfileDialog) {
        EditProfileModal(
            currentUsername = tiktokUser.username,
            currentDisplayName = tiktokUser.displayName,
            currentFollowers = tiktokUser.followerCount,
            currentLikes = tiktokUser.likesCount,
            currentFollowing = tiktokUser.followingCount,
            currentBio = tiktokUser.bio,
            onSave = { u, d, f, l, fo, b ->
                viewModel.tiktokAuthManager.updateProfile(
                    username = u,
                    displayName = d,
                    followerCount = f,
                    likesCount = l,
                    followingCount = fo,
                    bio = b
                )
                showEditProfileDialog = false
            },
            onDismiss = { showEditProfileDialog = false }
        )
    }
}

@Composable
fun EditProfileModal(
    currentUsername: String,
    currentDisplayName: String,
    currentFollowers: String,
    currentLikes: String,
    currentFollowing: String,
    currentBio: String,
    onSave: (String, String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf(currentUsername) }
    var displayName by remember { mutableStateOf(currentDisplayName) }
    var followers by remember { mutableStateOf(currentFollowers) }
    var likes by remember { mutableStateOf(currentLikes) }
    var following by remember { mutableStateOf(currentFollowing) }
    var bio by remember { mutableStateOf(currentBio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit TikTok Profile & Stats",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("TikTok Username (without @)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Profile Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = followers,
                        onValueChange = { followers = it },
                        label = { Text("Followers") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = likes,
                        onValueChange = { likes = it },
                        label = { Text("Likes") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Quick Presets Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            followers = "2"
                            likes = "16"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                    ) {
                        Text("2 Original", fontSize = 10.sp)
                    }
                    Button(
                        onClick = {
                            followers = "14.8K"
                            likes = "89.4K"
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                    ) {
                        Text("14.8K Viral", fontSize = 10.sp)
                    }
                    Button(
                        onClick = {
                            followers = "1.4M"
                            likes = "8.9M"
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                    ) {
                        Text("1.4M Mega", fontSize = 10.sp)
                    }
                }

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(username, displayName, followers, likes, following, bio) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55))
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TikTokStatColumn(
    number: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = number,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun TikTokVideoGridItem(
    clip: CinemaClipItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.75f)
            .padding(1.dp)
            .background(Color.Black)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = clip.thumbnailDrawableResId ?: R.drawable.poster_the_man_nobody_knows),
            contentDescription = clip.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Bottom view count with play arrow matching screenshot
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = clip.viewsDisplay,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

