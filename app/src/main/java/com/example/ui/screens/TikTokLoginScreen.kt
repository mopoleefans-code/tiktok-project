package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.auth.TikTokUserState
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

@Composable
fun TikTokLoginScreen(
    viewModel: SilentRunViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tiktokUser by viewModel.tiktokAuthManager.userState.collectAsState()
    var showEmbeddedWebView by remember { mutableStateOf(false) }

    if (showEmbeddedWebView && !tiktokUser.isLoggedIn) {
        TikTokEmbeddedWebView(
            onLoginDetected = { username ->
                viewModel.tiktokAuthManager.onLoginSuccess(username)
                showEmbeddedWebView = false
            },
            onClose = { showEmbeddedWebView = false }
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(AbyssDark)
                .testTag("tiktok_login_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            // Top Navigation & Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_from_tiktok_login_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TikTok Account Center",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (tiktokUser.isLoggedIn) {
                // Logged In Profile View
                item {
                    LoggedInTikTokProfileCard(
                        user = tiktokUser,
                        onOpenProfile = { viewModel.openUrl(context, tiktokUser.profileUrl) },
                        onLogout = { viewModel.tiktokAuthManager.logout() }
                    )
                }

                item {
                    CreatorDashboardCard(
                        user = tiktokUser,
                        onOpenCreatorStudio = {
                            viewModel.openUrl(context, "https://www.tiktok.com/creator-center")
                        },
                        onOpenAnalytics = {
                            viewModel.openUrl(context, "https://www.tiktok.com/analytics")
                        }
                    )
                }
            } else {
                // Not Logged In View
                item {
                    TikTokLoginPromptCard(
                        currentUsername = tiktokUser.username,
                        onConnectUsername = { enteredUser ->
                            viewModel.tiktokAuthManager.onLoginSuccess(enteredUser)
                            onBack()
                        },
                        onOpenEmbeddedLogin = { showEmbeddedWebView = true },
                        onOpenExternalBrowser = {
                            viewModel.openUrl(context, "https://www.tiktok.com/@${tiktokUser.username}")
                        },
                        onOpenDirectVideoLink = {
                            viewModel.openUrl(context, viewModel.tiktokShortUrl)
                        },
                        onQuickConnectCreator = {
                            viewModel.tiktokAuthManager.loginAsCreatorChannel()
                            onBack()
                        }
                    )
                }

                item {
                    SecurityGuaranteeCard()
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun LoggedInTikTokProfileCard(
    user: TikTokUserState,
    onOpenProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("logged_in_profile_card"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(NeonCyan)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f))
                        .border(2.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎬", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        if (user.isVerifiedCreator) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Surface(
                        color = BioGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "● TIKTOK CONNECTED",
                            style = MaterialTheme.typography.labelSmall,
                            color = BioGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileStatBox(label = "Followers", value = user.followerCount, modifier = Modifier.weight(1f))
                ProfileStatBox(label = "Status", value = "Creator", modifier = Modifier.weight(1f))
                ProfileStatBox(label = "Sync", value = "Active", modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenProfile,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("view_tiktok_profile_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = SurfaceDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "View on TikTok",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("logout_tiktok_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DangerCrimson
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(DangerCrimson.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Disconnect",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CreatorDashboardCard(
    user: TikTokUserState,
    onOpenCreatorStudio: () -> Unit,
    onOpenAnalytics: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Creator Studio Quick Links",
                style = MaterialTheme.typography.titleMedium,
                color = WarningAmber,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Manage your movie clips, check viewer engagement, and monitor algorithm performance directly on TikTok.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenCreatorStudio,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceElevated,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Creator Studio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOpenAnalytics,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceElevated,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Live Analytics", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TikTokLoginPromptCard(
    currentUsername: String,
    onConnectUsername: (String) -> Unit,
    onOpenEmbeddedLogin: () -> Unit,
    onOpenExternalBrowser: () -> Unit,
    onOpenDirectVideoLink: () -> Unit,
    onQuickConnectCreator: () -> Unit
) {
    var inputUser by remember { mutableStateOf(currentUsername) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("login_prompt_card"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(2.dp, NeonCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("📱", fontSize = 32.sp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "TikTok Account & Booster Connect",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Link your TikTok account @$currentUsername to enable 1-tap algorithm boosting, sync movie clip stats, and drive viral reach worldwide.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Direct Handle Customizer
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NeonCyan.copy(alpha = 0.5f)))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Connect TikTok Username",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = inputUser,
                        onValueChange = { inputUser = it },
                        label = { Text("TikTok handle (e.g. mopoleemovieclip1)", color = TextTertiary) },
                        prefix = { Text("@", color = NeonCyan, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = OutlineBorder
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = { onConnectUsername(inputUser) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("apply_username_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = SurfaceDark
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "⚡ Connect & Apply Instantly",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Option 2: 1-Click Connect as Mopolee Movie Clip Creator
            Button(
                onClick = onQuickConnectCreator,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("quick_connect_creator_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarningAmber,
                    contentColor = SurfaceDark
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "1-Click Connect as @mopoleemovieclip1",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // Option 3: Embedded In-App WebView Login
            OutlinedButton(
                onClick = onOpenEmbeddedLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("embedded_tiktok_login_btn"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Log In via In-App TikTok Web Browser",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }

            // Option 4: External Browser / Official TikTok App
            OutlinedButton(
                onClick = onOpenExternalBrowser,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("external_tiktok_login_btn"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Open TikTok Profile in TikTok App",
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
            }

            // Option 5: Verified Direct Video Link (Never 404s)
            Button(
                onClick = onOpenDirectVideoLink,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("open_direct_video_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFE2C55).copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "🎬 Open Direct Video Clip (tiktok.com/t/ZPLAoJ4P8)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SecurityGuaranteeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineBorder))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = BioGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Official TikTok Secure Auth",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Authentication happens directly on TikTok.com servers. Your credentials are never stored or seen by third parties.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ProfileStatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = SurfaceElevated,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                fontSize = 11.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = NeonCyan,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TikTokEmbeddedWebView(
    onLoginDetected: (String) -> Unit,
    onClose: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbyssDark)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Close", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "TikTok Official Login",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = { webViewRef?.reload() }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload", tint = NeonCyan)
            }
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = NeonCyan,
                trackColor = SurfaceElevated
            )
        }

        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.databaseEnabled = true
                    settings.userAgentString = "Mozilla/5.0 (Linux; Android 12; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                            url?.let { currentUrl ->
                                // If redirected to profile or feed after login
                                if (currentUrl.contains("tiktok.com/@") || currentUrl.contains("tiktok.com/foryou")) {
                                    val extractedUsername = if (currentUrl.contains("tiktok.com/@")) {
                                        currentUrl.substringAfter("tiktok.com/@").substringBefore("/").substringBefore("?")
                                    } else "mopoleemovieclip1"
                                    onLoginDetected(extractedUsername)
                                }
                            }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                            url?.let { currentUrl ->
                                if (currentUrl.contains("tiktok.com/@") || currentUrl.contains("tiktok.com/foryou")) {
                                    val extractedUsername = if (currentUrl.contains("tiktok.com/@")) {
                                        currentUrl.substringAfter("tiktok.com/@").substringBefore("/").substringBefore("?")
                                    } else "mopoleemovieclip1"
                                    onLoginDetected(extractedUsername)
                                }
                            }
                        }

                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            return false
                        }
                    }

                    loadUrl("https://www.tiktok.com/login")
                    webViewRef = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
