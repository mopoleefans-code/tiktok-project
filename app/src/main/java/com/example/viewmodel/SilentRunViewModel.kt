package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.audio.AcousticSoundDetector
import com.example.audio.SoundboardSynthesizer
import com.example.model.AcousticDecoyItem
import com.example.model.AlgorithmBoosterData
import com.example.model.CinemaClipItem
import com.example.model.CreatureAlertLevel
import com.example.model.CreatureDossier
import com.example.model.MoveChoice
import com.example.model.PaystackStoreItem
import com.example.model.QuietRuleItem
import com.example.model.SurvivalZone
import com.example.model.SurvivorComment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class SurvivalUiState(
    val isRunning: Boolean = false,
    val secondsSurvived: Int = 0,
    val bestStreakSeconds: Int = 42,
    val peakDb: Float = 28f,
    val selectedZoneIndex: Int = 0,
    val creatureDistanceMeters: Float = 100f,
    val isCaught: Boolean = false,
    val forceSimulation: Boolean = false,
    val recentDilemmaPrompt: String? = null
)

class SilentRunViewModel(application: Application) : AndroidViewModel(application) {

    val soundDetector = AcousticSoundDetector(application.applicationContext)
    val tiktokAuthManager = com.example.auth.TikTokAuthManager(application.applicationContext)

    // Configured official links from user
    val tiktokProfileUrl = "https://www.tiktok.com/@mopoleemovieclip1"
    val tiktokShortUrl = "https://www.tiktok.com/t/ZPLAoJ4P8/"
    val paystackStoreUrl = "https://paystack.shop/pay/mjzyo3ejz0"

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _currentFeedIndex = MutableStateFlow(0)
    val currentFeedIndex: StateFlow<Int> = _currentFeedIndex.asStateFlow()

    fun setFeedIndex(index: Int) {
        _currentFeedIndex.value = index
    }

    private val _survivalState = MutableStateFlow(SurvivalUiState())
    val survivalState: StateFlow<SurvivalUiState> = _survivalState.asStateFlow()

    private val _boosterState = MutableStateFlow(AlgorithmBoosterData())
    val boosterState: StateFlow<AlgorithmBoosterData> = _boosterState.asStateFlow()

    private var survivalTimerJob: Job? = null
    private var boosterJob: Job? = null

    init {
        startBoosterLiveSimulation()
    }

    val paystackProducts = listOf(
        PaystackStoreItem(
            id = "pay_vip_pass",
            title = "VIP Pro Cinema All-Access Pass",
            priceFormatted = "$5.00 / ₦3,500",
            badge = "BESTSELLER",
            description = "Zero-form instant 1-click checkout. Unlocks full HD streaming of all Mopo Lee Pro Cinema exclusive releases.",
            perks = listOf(
                "Immediate digital access link",
                "Full 4K Ultra-HD uninterrupted streaming",
                "Creator VIP badge in community tactics feed",
                "Zero forms - Instant 1-click Paystack checkout"
            ),
            directPayUrl = paystackStoreUrl
        ),
        PaystackStoreItem(
            id = "pay_man_nobody_knows",
            title = "The Man Nobody Knows: Premiere Pass",
            priceFormatted = "$9.99 / ₦7,500",
            badge = "PREMIERE",
            description = "Exclusive director's cut & early screening of the blockbuster action thriller 'THE MAN NOBODY KNOWS'.",
            perks = listOf(
                "Full movie screening link with behind the scenes",
                "Official high-res movie wallpaper & poster pack",
                "Tactical operative dossier & soundtrack download"
            ),
            directPayUrl = paystackStoreUrl
        ),
        PaystackStoreItem(
            id = "pay_supporter_bundle",
            title = "Elite Movie Clips Supporter Gear",
            priceFormatted = "$19.99 / ₦15,000",
            badge = "LIMITED",
            description = "Support Mopo Lee Pro Cinema production fund and receive executive producer digital credits.",
            perks = listOf(
                "Producer credit in next movie clip description",
                "Exclusive access to private creator live streams",
                "All future movie passes included"
            ),
            directPayUrl = paystackStoreUrl
        )
    )

    val zones = listOf(
        SurvivalZone(
            id = "zone_jungle",
            name = "The Damp Jungle",
            environment = "Wet moss, rain mist, dense foliage",
            thresholdDb = 45f,
            description = "Natural rain masks light footsteps. Watch out for snapping twigs.",
            riskLevel = "MODERATE"
        ),
        SurvivalZone(
            id = "zone_warehouse",
            name = "Abandoned Depot",
            environment = "Rusted iron flooring, echoing girders",
            thresholdDb = 38f,
            description = "High metallic resonance. Heavy breathing carries across the hall.",
            riskLevel = "HIGH"
        ),
        SurvivalZone(
            id = "zone_subway",
            name = "Echo Subway Tunnel",
            environment = "Enclosed acoustic chamber",
            thresholdDb = 34f,
            description = "Deadly acoustic multiplier. Even whisper amplitude echoes.",
            riskLevel = "EXTREME"
        ),
        SurvivalZone(
            id = "zone_nest",
            name = "The Queen's Hive",
            environment = "Underground organic cavern",
            thresholdDb = 29f,
            description = "Multiple blind stalkers sleeping on walls. Pure silence required.",
            riskLevel = "NIGHTMARE"
        )
    )

    private val _clips = MutableStateFlow(
        listOf(
            CinemaClipItem(
                id = "clip_original_tiktok",
                title = "Rule #1: Whatever you do, do NOT make a sound... 🤫💀",
                creatorName = "MOPO LEE PRO CINEMA",
                creatorHandle = "@mopoleemovieclip1",
                caption = "Night squad surrounded in the mist. The blind acoustic predator is listening... Would you run or hide? Drop your move below! 👇 #scifi #monster #creature #actionmovie",
                tags = listOf("#scifi", "#monster", "#creature", "#actionmovie", "#movieclips", "#mopoleemovieclip"),
                videoRawResId = R.raw.creature_clip,
                thumbnailDrawableResId = R.drawable.banner_monster,
                runVotes = 14820,
                hideVotes = 26410,
                userVote = null,
                likesCount = 38920,
                isLiked = false,
                episodeTag = "FEATURED CLIP"
            ),
            CinemaClipItem(
                id = "clip_the_man_nobody_knows",
                title = "THE MAN NOBODY KNOWS: Cinema & Exchange",
                creatorName = "MOPO LEE PRO CINEMA",
                creatorHandle = "@mopoleemovieclip1",
                caption = "The ultimate weapon is an unknown enemy. Secrets, power, betrayal, and a man the world never saw coming. 🎬🍿 #actionmovie #themannobodyknows #cinema",
                tags = listOf("#themannobodyknows", "#actionmovie", "#blockbuster", "#cinema", "#trailer"),
                videoRawResId = null,
                thumbnailDrawableResId = R.drawable.poster_the_man_nobody_knows,
                runVotes = 18400,
                hideVotes = 9120,
                userVote = null,
                likesCount = 45200,
                isLiked = true,
                episodeTag = "BLOCKBUSTER PREMIERE"
            ),
            CinemaClipItem(
                id = "clip_jungle_command",
                title = "Jungle Outpost: Episode 2 - Strategic Ambush",
                creatorName = "MOPO LEE PRO CINEMA",
                creatorHandle = "@mopoleemovieclip1",
                caption = "Behind enemy lines in the jungle command hut. The map reveals the extraction coordinates, but shadows are moving outside... 🪖🔥",
                tags = listOf("#military", "#action", "#junglewarfare", "#mopoleemovieclip", "#survival"),
                videoRawResId = null,
                thumbnailDrawableResId = R.drawable.scene_jungle_outpost,
                runVotes = 7210,
                hideVotes = 11450,
                userVote = null,
                likesCount = 18900,
                isLiked = false,
                episodeTag = "EPISODE 2"
            ),
            CinemaClipItem(
                id = "clip_urban_run",
                title = "Urban Extraction: Alley Sprint vs Shadow Ambush",
                creatorName = "MOPO LEE PRO CINEMA",
                creatorHandle = "@mopoleemovieclip1",
                caption = "Gunfire alerted the horde in the city street. Spotlights illuminated their terrifying silhouettes on the rooftops.",
                tags = listOf("#action", "#thriller", "#creaturefeature", "#escape"),
                videoRawResId = null,
                thumbnailDrawableResId = R.drawable.banner_city_run,
                runVotes = 8940,
                hideVotes = 12300,
                userVote = null,
                likesCount = 21450,
                isLiked = false,
                episodeTag = "EPISODE 3"
            )
        )
    )
    val clips: StateFlow<List<CinemaClipItem>> = _clips.asStateFlow()

    private val _comments = MutableStateFlow(
        listOf(
            SurvivorComment(
                id = "c1",
                author = "Sgt. Miller",
                avatarInitials = "SM",
                moveChoice = MoveChoice.HIDE,
                commentText = "Rule #1 always wins. If you run, your boots on asphalt generate over 65dB of acoustic resonance. You'll never outrun a predator that sprints at 60 km/h. Hide and control your breathing.",
                timestamp = "2h ago",
                upvotes = 342
            ),
            SurvivorComment(
                id = "c2",
                author = "Kira Vance",
                avatarInitials = "KV",
                moveChoice = MoveChoice.RUN,
                commentText = "I would RUN only if I have a clear line of sight to the armored transport! Staying pinned down in an open street means they flank your position by sound triangulating.",
                timestamp = "4h ago",
                upvotes = 189
            ),
            SurvivorComment(
                id = "c3",
                author = "TacticalGhost",
                avatarInitials = "TG",
                moveChoice = MoveChoice.DECOY,
                commentText = "Neither! Throw an acoustic decoy (throw your sidearm or a flashlight into the metal trash bin downwind), wait for the creature to leap, and slip away into the basement.",
                timestamp = "6h ago",
                upvotes = 521
            ),
            SurvivorComment(
                id = "c4",
                author = "Dr. Chen",
                avatarInitials = "DC",
                moveChoice = MoveChoice.HIDE,
                commentText = "Creature auditory receptors close up if you activate an ultrasonic pulse. Otherwise, stay completely frozen. Even a heartbeat over 110 BPM can be detected within 5 meters.",
                timestamp = "8h ago",
                upvotes = 277
            )
        )
    )
    val comments: StateFlow<List<SurvivorComment>> = _comments.asStateFlow()

    val decoys = listOf(
        AcousticDecoyItem(
            id = "decoy_glass",
            title = "Glass Bottle Shatter",
            soundType = "GLASS_SHATTER",
            diversionRangeMeters = 75,
            durationSeconds = 6,
            effectivenessPct = 88,
            description = "High frequency acoustic spike. Diverts blind predator instantly toward impact point."
        ),
        AcousticDecoyItem(
            id = "decoy_clock",
            title = "Mechanical Timer",
            soundType = "ALARM_CLOCK",
            diversionRangeMeters = 50,
            durationSeconds = 12,
            effectivenessPct = 82,
            description = "Rhythmic ticking creates sustained diversion to allow squad repositioning."
        ),
        AcousticDecoyItem(
            id = "decoy_sonar",
            title = "Ultrasonic Chirp Pulse",
            soundType = "ULTRASONIC",
            diversionRangeMeters = 120,
            durationSeconds = 8,
            effectivenessPct = 94,
            description = "Overloads creature tympanic ear shields, creating acute disorientation and temporary paralysis."
        ),
        AcousticDecoyItem(
            id = "decoy_static",
            title = "Radio Static Scrambler",
            soundType = "RADIO_STATIC",
            diversionRangeMeters = 40,
            durationSeconds = 10,
            effectivenessPct = 78,
            description = "White noise mask dampens your footsteps and confusion in enclosed rooms."
        ),
        AcousticDecoyItem(
            id = "decoy_flare",
            title = "Emergency Flare Hiss",
            soundType = "FLARE_HISS",
            diversionRangeMeters = 90,
            durationSeconds = 15,
            effectivenessPct = 85,
            description = "Crackle noise and atmospheric disturbance forces creature to circle perimeter."
        )
    )

    val quietRules = listOf(
        QuietRuleItem(
            ruleNumber = 1,
            title = "Absolute Audio Discipline",
            directive = "Whatever you do, do NOT make a sound...",
            explanation = "Acoustic predators have evolved oversized tympanic membranes that perceive subtle air vibrations down to 18 dB from up to 200 meters away.",
            survivalTip = "Breathe through an open mouth to eliminate nasal whistling. Step heel-to-toe on natural soft soil."
        ),
        QuietRuleItem(
            ruleNumber = 2,
            title = "Surface Vector Awareness",
            directive = "Every surface is an acoustic amplifier.",
            explanation = "Asphalt, metal gratings, and dry autumn foliage carry vibrational shockwaves directly into the creature's claw sensors.",
            survivalTip = "Walk exclusively on sand, thick moss, or shallow running water."
        ),
        QuietRuleItem(
            ruleNumber = 3,
            title = "Acoustic Decoy Dominance",
            directive = "Never move without a diversion ready.",
            explanation = "Predators commit 100% of their kinetic energy to the loudest immediate sound source.",
            survivalTip = "Throw decoys in opposing directions to clear your extraction route."
        ),
        QuietRuleItem(
            ruleNumber = 4,
            title = "Heart Rate Regulation",
            directive = "Panic is audible.",
            explanation = "Under 3 meters distance, high-amplitude cardiovascular pulse vibrations can be picked up by the predator's lateral sensory line.",
            survivalTip = "Practice 4-second box breathing to maintain heart rate below 75 BPM."
        ),
        QuietRuleItem(
            ruleNumber = 5,
            title = "Frequency Resonance Overload",
            directive = "Their hearing is also their greatest vulnerability.",
            explanation = "Frequencies between 2.2 kHz and 4.5 kHz cause acute neural hemorrhaging and force immediate defensive posture.",
            survivalTip = "Use ultrasonic whistles or feedback loops to survive close-quarters ambushes."
        )
    )

    val creatureDossier = CreatureDossier(
        codename = "Apex Acoustic Stalker (Strigoi Sonaris)",
        scientificName = "Titanus Auditus Exstinctor",
        auditoryRangeMeters = 350,
        acousticSensitivity = "18 dB to 140 kHz (Omnidirectional)",
        locomotionSpeedKmh = 65,
        weakness = "High-pitch frequency resonance over 2.5 kHz ruptures ear plates",
        overview = "Bipedal apex predator possessing thick chitinous armor and entirely absent visual apparatus. Relies on hypersensitive acoustic plates and echolocation to pinpoint prey in pitch-black environments with millimeter accuracy."
    )

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun selectZone(zoneIndex: Int) {
        _survivalState.value = _survivalState.value.copy(selectedZoneIndex = zoneIndex)
    }

    fun toggleSimulationMode(active: Boolean) {
        _survivalState.value = _survivalState.value.copy(forceSimulation = active)
        if (_survivalState.value.isRunning) {
            soundDetector.stopDetection()
            soundDetector.startDetection(viewModelScope, active)
        }
    }

    fun openUrl(context: Context, url: String) {
        try {
            val sanitizedUrl = when {
                url.endsWith("/@mopoleemovieclip") || url.contains("/@mopoleemovieclip?") -> {
                    url.replace("@mopoleemovieclip", "@mopoleemovieclip1")
                }
                else -> url
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sanitizedUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    fun shareViralClip(context: Context, clipTitle: String) {
        try {
            val textToShare = "$clipTitle\n\n🎬 Watch on TikTok: $tiktokProfileUrl\n🍿 Paystack VIP Store: $paystackStoreUrl\n\n#themannobodyknows #mopoleemovieclip #scifi #actionmovie #viral #fyp"
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToShare)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Mopo Lee Movie Clip Worldwide")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
            _boosterState.value = _boosterState.value.copy(
                sharesInitiated = _boosterState.value.sharesInitiated + 1,
                projectedReachViews = _boosterState.value.projectedReachViews + 25000L
            )
        } catch (_: Exception) {
        }
    }

    private fun startBoosterLiveSimulation() {
        boosterJob?.cancel()
        boosterJob = viewModelScope.launch {
            while (true) {
                delay(3000)
                if (_boosterState.value.isBoosting) {
                    val viewsGain = (120..450).random()
                    val followersGain = (1..5).random()
                    _boosterState.value = _boosterState.value.copy(
                        projectedReachViews = _boosterState.value.projectedReachViews + viewsGain,
                        activeFollowersGained = _boosterState.value.activeFollowersGained + followersGain
                    )
                }
            }
        }
    }

    fun toggleBooster() {
        val newStatus = !_boosterState.value.isBoosting
        _boosterState.value = _boosterState.value.copy(isBoosting = newStatus)
    }

    fun voteOnClip(clipId: String, choice: MoveChoice) {
        _clips.value = _clips.value.map { clip ->
            if (clip.id == clipId) {
                val wasRun = clip.userVote == MoveChoice.RUN
                val wasHide = clip.userVote == MoveChoice.HIDE
                val isRun = choice == MoveChoice.RUN
                val isHide = choice == MoveChoice.HIDE

                var newRun = clip.runVotes
                var newHide = clip.hideVotes

                if (wasRun && !isRun) newRun--
                if (wasHide && !isHide) newHide--

                if (!wasRun && isRun) newRun++
                if (!wasHide && isHide) newHide++

                clip.copy(
                    runVotes = newRun,
                    hideVotes = newHide,
                    userVote = choice
                )
            } else clip
        }
    }

    fun toggleLikeClip(clipId: String) {
        _clips.value = _clips.value.map { clip ->
            if (clip.id == clipId) {
                clip.copy(
                    isLiked = !clip.isLiked,
                    likesCount = if (clip.isLiked) clip.likesCount - 1 else clip.likesCount + 1
                )
            } else clip
        }
    }

    fun addComment(choice: MoveChoice, text: String) {
        if (text.isBlank()) return
        val newComment = SurvivorComment(
            id = UUID.randomUUID().toString(),
            author = "You (Survivor)",
            avatarInitials = "ME",
            moveChoice = choice,
            commentText = text.trim(),
            timestamp = "Just now",
            upvotes = 1,
            isUpvoted = true
        )
        _comments.value = listOf(newComment) + _comments.value
    }

    fun toggleUpvoteComment(commentId: String) {
        _comments.value = _comments.value.map { c ->
            if (c.id == commentId) {
                c.copy(
                    isUpvoted = !c.isUpvoted,
                    upvotes = if (c.isUpvoted) c.upvotes - 1 else c.upvotes + 1
                )
            } else c
        }
    }

    fun startSurvivalChallenge() {
        val forceSim = _survivalState.value.forceSimulation || !soundDetector.hasRecordPermission()
        soundDetector.startDetection(viewModelScope, forceSim)

        _survivalState.value = _survivalState.value.copy(
            isRunning = true,
            secondsSurvived = 0,
            peakDb = 25f,
            creatureDistanceMeters = 100f,
            isCaught = false,
            recentDilemmaPrompt = null
        )

        survivalTimerJob?.cancel()
        survivalTimerJob = viewModelScope.launch {
            val currentZone = zones[_survivalState.value.selectedZoneIndex]
            val threshold = currentZone.thresholdDb

            while (_survivalState.value.isRunning && !_survivalState.value.isCaught) {
                delay(1000)
                val currentDb = soundDetector.currentDb.value
                val peak = maxOf(_survivalState.value.peakDb, currentDb)
                val newSeconds = _survivalState.value.secondsSurvived + 1
                val newBest = maxOf(_survivalState.value.bestStreakSeconds, newSeconds)

                var distance = _survivalState.value.creatureDistanceMeters
                if (currentDb > threshold) {
                    val excess = currentDb - threshold
                    val speed = (excess * 0.8f).coerceAtLeast(3f)
                    distance = (distance - speed).coerceAtLeast(0f)
                } else {
                    distance = (distance + 1.2f).coerceAtMost(100f)
                }

                val caught = distance <= 0f || currentDb > 78f

                val dilemma = if (newSeconds > 0 && newSeconds % 15 == 0 && distance > 25f) {
                    "A rusted pipe rattles above! Freeze in place or crawl under cover?"
                } else null

                if (caught) {
                    soundDetector.triggerVibrate(600)
                }

                _survivalState.value = _survivalState.value.copy(
                    secondsSurvived = newSeconds,
                    bestStreakSeconds = newBest,
                    peakDb = peak,
                    creatureDistanceMeters = distance,
                    isCaught = caught,
                    recentDilemmaPrompt = dilemma
                )

                if (caught) {
                    soundDetector.stopDetection()
                    break
                }
            }
        }
    }

    fun stopSurvivalChallenge() {
        soundDetector.stopDetection()
        survivalTimerJob?.cancel()
        _survivalState.value = _survivalState.value.copy(
            isRunning = false,
            recentDilemmaPrompt = null
        )
    }

    fun triggerSimulationNoise(type: String) {
        val spike = when (type) {
            "BREATH" -> 16f
            "FOOTSTEP" -> 26f
            "BRANCH" -> 38f
            "HOLD_BREATH" -> -8f
            else -> 20f
        }
        soundDetector.triggerSimulationNoise(spike)
    }

    fun triggerDecoy(decoy: AcousticDecoyItem) {
        viewModelScope.launch {
            SoundboardSynthesizer.playSynthesizedDecoy(decoy.soundType)
            if (_survivalState.value.isRunning && !_survivalState.value.isCaught) {
                val newDistance = (_survivalState.value.creatureDistanceMeters + decoy.diversionRangeMeters * 0.4f).coerceAtMost(100f)
                _survivalState.value = _survivalState.value.copy(
                    creatureDistanceMeters = newDistance,
                    recentDilemmaPrompt = "Creature diverted ${decoy.diversionRangeMeters}m away by ${decoy.title}!"
                )
            }
        }
    }

    fun getAlertLevel(currentDb: Float, threshold: Float): CreatureAlertLevel {
        return when {
            _survivalState.value.isCaught -> CreatureAlertLevel.CAUGHT
            currentDb >= threshold + 15f -> CreatureAlertLevel.DANGER
            currentDb >= threshold -> CreatureAlertLevel.CAUTION
            else -> CreatureAlertLevel.SAFE
        }
    }

    fun calculateStealthRank(seconds: Int, peakDb: Float): String {
        return when {
            seconds >= 60 && peakDb < 38f -> "S-Tier (Ghost)"
            seconds >= 40 && peakDb < 45f -> "A-Tier (Shadow Stalker)"
            seconds >= 20 -> "B-Tier (Tactical Scout)"
            else -> "F-Tier (Creature Food)"
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundDetector.stopDetection()
        survivalTimerJob?.cancel()
        boosterJob?.cancel()
    }
}
