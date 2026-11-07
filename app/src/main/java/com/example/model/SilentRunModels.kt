package com.example.model

enum class MoveChoice {
    RUN,
    HIDE,
    DECOY
}

enum class CreatureAlertLevel {
    SAFE,       // < 40 dB
    CAUTION,    // 40 - 55 dB
    DANGER,     // 55 - 70 dB
    CAUGHT      // > 70 dB or sustained noise
}

data class CinemaClipItem(
    val id: String,
    val title: String,
    val creatorName: String,
    val creatorHandle: String,
    val caption: String,
    val tags: List<String>,
    val videoRawResId: Int? = null,
    val thumbnailDrawableResId: Int? = null,
    val runVotes: Int,
    val hideVotes: Int,
    val userVote: MoveChoice? = null,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val episodeTag: String? = null,
    val viewsDisplay: String = "73",
    val soundTitle: String = "original sound - mopoleemovieclip1"
)

data class SurvivorComment(
    val id: String,
    val author: String,
    val avatarInitials: String,
    val moveChoice: MoveChoice,
    val commentText: String,
    val timestamp: String,
    val upvotes: Int,
    val isUpvoted: Boolean = false
)

data class SurvivalZone(
    val id: String,
    val name: String,
    val environment: String,
    val thresholdDb: Float,
    val description: String,
    val riskLevel: String
)

data class AcousticDecoyItem(
    val id: String,
    val title: String,
    val soundType: String,
    val diversionRangeMeters: Int,
    val durationSeconds: Int,
    val effectivenessPct: Int,
    val description: String,
    val isAvailable: Boolean = true
)

data class QuietRuleItem(
    val ruleNumber: Int,
    val title: String,
    val directive: String,
    val explanation: String,
    val survivalTip: String
)

data class CreatureDossier(
    val codename: String,
    val scientificName: String,
    val auditoryRangeMeters: Int,
    val acousticSensitivity: String,
    val locomotionSpeedKmh: Int,
    val weakness: String,
    val overview: String
)

data class PaystackStoreItem(
    val id: String,
    val title: String,
    val priceFormatted: String,
    val badge: String,
    val description: String,
    val perks: List<String>,
    val directPayUrl: String = "https://paystack.shop/pay/mjzyo3ejz0"
)

data class AlgorithmBoosterData(
    val isBoosting: Boolean = true,
    val projectedReachViews: Long = 1845200L,
    val viralVelocityMultiplier: Float = 4.5f,
    val activeFollowersGained: Int = 12480,
    val sharesInitiated: Int = 3490,
    val topTrendingRegions: List<String> = listOf("Global", "United States", "Nigeria", "United Kingdom", "Brazil", "South Africa"),
    val viralTags: List<String> = listOf("#themannobodyknows", "#mopoleemovieclip", "#scifi", "#actionmovie", "#creature", "#fyp", "#viral")
)
