package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.BoosterAndStoreScreen
import com.example.ui.screens.BoosterDashboardScreen
import com.example.ui.screens.TikTokFeedScreen
import com.example.ui.screens.TikTokLoginScreen
import com.example.ui.screens.TikTokProfileScreen
import com.example.ui.theme.AbyssDark
import com.example.ui.theme.BioGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.SilentRunViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MopoleeBoosterApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MopoleeBoosterApp(viewModel: SilentRunViewModel = viewModel()) {
    val context = LocalContext.current
    val selectedTab by viewModel.selectedTab.collectAsState()
    val boosterState by viewModel.boosterState.collectAsState()
    val tiktokUser by viewModel.tiktokAuthManager.userState.collectAsState()

    var showTikTokLoginModal by remember { mutableStateOf(false) }

    BackHandler(enabled = showTikTokLoginModal || selectedTab != 0) {
        if (showTikTokLoginModal) {
            showTikTokLoginModal = false
        } else {
            viewModel.selectTab(0)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        topBar = {
            if (!showTikTokLoginModal) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFE2C55).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🚀", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "MOPOLEE BOOSTER",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "@mopoleemovieclip1 • TikTok Growth",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    },
                    actions = {
                        // TikTok Login Button in Action Bar
                        Button(
                            onClick = { showTikTokLoginModal = true },
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .height(32.dp)
                                .testTag("topbar_login_tiktok_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tiktokUser.isLoggedIn) BioGreen.copy(alpha = 0.2f) else Color(0xFFFE2C55),
                                contentColor = if (tiktokUser.isLoggedIn) BioGreen else Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (tiktokUser.isLoggedIn) "Logged In ✓" else "TikTok Login",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        // Paystack Store Fast Trigger
                        Button(
                            onClick = { viewModel.openUrl(context, viewModel.paystackStoreUrl) },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(32.dp)
                                .testTag("topbar_paystack_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WarningAmber,
                                contentColor = SurfaceDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Paystack",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SurfaceDark,
                        titleContentColor = TextPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (!showTikTokLoginModal) {
                NavigationBar(
                    containerColor = Color.Black,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("mopolee_bottom_nav")
                ) {
                    // Tab 0: Booster Command Center
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (boosterState.isBoosting) {
                                        Badge(containerColor = BioGreen) {
                                            Text("●", fontSize = 8.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = "Booster"
                                )
                            }
                        },
                        label = {
                            Text(
                                text = "Booster",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("nav_booster_btn")
                    )

                    // Tab 1: Video Posts & Feed
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = "Clips"
                            )
                        },
                        label = {
                            Text(
                                text = "TikTok Feed",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("nav_feed_btn")
                    )

                    // Tab 2: Paystack Store (1-Click Instant)
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Paystack Store"
                            )
                        },
                        label = {
                            Text(
                                text = "Paystack",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = WarningAmber,
                            selectedTextColor = WarningAmber,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("nav_store_btn")
                    )

                    // Tab 3: Authentic TikTok Profile
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { viewModel.selectTab(3) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = {
                            Text(
                                text = "My Profile",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("nav_profile_btn")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showTikTokLoginModal) {
                TikTokLoginScreen(
                    viewModel = viewModel,
                    onBack = { showTikTokLoginModal = false }
                )
            } else {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "booster_nav_transition"
                ) { tab ->
                    when (tab) {
                        0 -> BoosterDashboardScreen(
                            viewModel = viewModel,
                            onOpenPaystack = { viewModel.selectTab(2) },
                            onOpenProfile = { viewModel.selectTab(3) }
                        )
                        1 -> TikTokFeedScreen(
                            viewModel = viewModel,
                            onNavigateToSurvival = { viewModel.selectTab(0) },
                            onOpenPaystackStore = { viewModel.selectTab(2) }
                        )
                        2 -> BoosterAndStoreScreen(viewModel = viewModel)
                        3 -> TikTokProfileScreen(
                            viewModel = viewModel,
                            onOpenTikTokLogin = { showTikTokLoginModal = true },
                            onOpenPaystackStore = { viewModel.selectTab(2) },
                            onSelectClip = { index ->
                                viewModel.setFeedIndex(index)
                                viewModel.selectTab(1)
                            }
                        )
                    }
                }
            }
        }
    }
}
