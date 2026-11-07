package com.example.auth

import android.content.Context
import android.webkit.CookieManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TikTokUserState(
    val isLoggedIn: Boolean = false,
    val username: String = "mopoleemovieclip1",
    val displayName: String = "Mopolee Movie Clips",
    val followingCount: String = "2",
    val followerCount: String = "2",
    val likesCount: String = "16",
    val bio: String = "🎬 MOPO LEE PRO CINEMA 🍿❤️\n🔥 Your home of movies and entertainment!\n🎥 Action • Romance • Comedy • Drama\n👇 Follow for more movie updates!",
    val profileUrl: String = "https://www.tiktok.com/@mopoleemovieclip1",
    val isVerifiedCreator: Boolean = true
)

class TikTokAuthManager(private val context: Context) {

    private val sharedPrefs = context.getSharedPreferences("tiktok_auth_prefs", Context.MODE_PRIVATE)

    private val _userState = MutableStateFlow(loadSavedState())
    val userState: StateFlow<TikTokUserState> = _userState.asStateFlow()

    private fun loadSavedState(): TikTokUserState {
        val loggedIn = sharedPrefs.getBoolean("is_logged_in", false)
        val username = sharedPrefs.getString("username", "mopoleemovieclip1") ?: "mopoleemovieclip1"
        val displayName = sharedPrefs.getString("display_name", "Mopolee Movie Clips") ?: "Mopolee Movie Clips"
        val followingCount = sharedPrefs.getString("following_count", "2") ?: "2"
        val followerCount = sharedPrefs.getString("follower_count", "2") ?: "2"
        val likesCount = sharedPrefs.getString("likes_count", "16") ?: "16"
        val bio = sharedPrefs.getString("bio", "🎬 MOPO LEE PRO CINEMA 🍿❤️\n🔥 Your home of movies and entertainment!\n🎥 Action • Romance • Comedy • Drama\n👇 Follow for more movie updates!") ?: ""
        val profileUrl = sharedPrefs.getString("profile_url", "https://www.tiktok.com/@$username") ?: ""
        val isVerified = sharedPrefs.getBoolean("is_verified", true)

        return TikTokUserState(
            isLoggedIn = loggedIn,
            username = username,
            displayName = displayName,
            followingCount = followingCount,
            followerCount = followerCount,
            likesCount = likesCount,
            bio = bio,
            profileUrl = profileUrl,
            isVerifiedCreator = isVerified
        )
    }

    fun onLoginSuccess(username: String, displayName: String = "") {
        val cleanUser = username.removePrefix("@").trim()
        val finalDisplayName = displayName.ifBlank { cleanUser }

        sharedPrefs.edit().apply {
            putBoolean("is_logged_in", true)
            putString("username", cleanUser)
            putString("display_name", finalDisplayName)
            putString("profile_url", "https://www.tiktok.com/@$cleanUser")
            apply()
        }

        _userState.value = _userState.value.copy(
            isLoggedIn = true,
            username = cleanUser,
            displayName = finalDisplayName,
            profileUrl = "https://www.tiktok.com/@$cleanUser"
        )
    }

    fun updateProfile(
        username: String,
        displayName: String,
        followingCount: String,
        followerCount: String,
        likesCount: String,
        bio: String
    ) {
        val cleanUser = username.removePrefix("@").trim()

        sharedPrefs.edit().apply {
            putBoolean("is_logged_in", true)
            putString("username", cleanUser)
            putString("display_name", displayName)
            putString("following_count", followingCount)
            putString("follower_count", followerCount)
            putString("likes_count", likesCount)
            putString("bio", bio)
            putString("profile_url", "https://www.tiktok.com/@$cleanUser")
            apply()
        }

        _userState.value = TikTokUserState(
            isLoggedIn = true,
            username = cleanUser,
            displayName = displayName,
            followingCount = followingCount,
            followerCount = followerCount,
            likesCount = likesCount,
            bio = bio,
            profileUrl = "https://www.tiktok.com/@$cleanUser",
            isVerifiedCreator = true
        )
    }

    fun loginAsCreatorChannel() {
        updateProfile(
            username = "mopoleemovieclip1",
            displayName = "Mopolee Movie Clips",
            followingCount = "2",
            followerCount = "2",
            likesCount = "16",
            bio = "🎬 MOPO LEE PRO CINEMA 🍿❤️\n🔥 Your home of movies and entertainment!\n🎥 Action • Romance • Comedy • Drama\n👇 Follow for more movie updates!"
        )
    }

    fun applyBoostedStats(newFollowers: String, newLikes: String) {
        updateProfile(
            username = _userState.value.username,
            displayName = _userState.value.displayName,
            followingCount = _userState.value.followingCount,
            followerCount = newFollowers,
            likesCount = newLikes,
            bio = _userState.value.bio
        )
    }

    fun applyNextBoostTier(): Pair<String, String> {
        val currentFollowers = _userState.value.followerCount
        val (nextFollowers, nextLikes) = when (currentFollowers) {
            "2" -> "14.8K" to "89.4K"
            "14.8K" -> "128.5K" to "650.2K"
            "128.5K" -> "1.4M" to "8.9M"
            "1.4M" -> "5.6M" to "28.3M"
            else -> "14.8K" to "89.4K"
        }
        applyBoostedStats(nextFollowers, nextLikes)
        return nextFollowers to nextLikes
    }

    fun resetToOriginalStats() {
        updateProfile(
            username = _userState.value.username,
            displayName = _userState.value.displayName,
            followingCount = "2",
            followerCount = "2",
            likesCount = "16",
            bio = _userState.value.bio
        )
    }

    fun logout() {
        sharedPrefs.edit().clear().apply()
        try {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()
        } catch (_: Exception) {
        }
        _userState.value = loadSavedState().copy(isLoggedIn = false)
    }
}
