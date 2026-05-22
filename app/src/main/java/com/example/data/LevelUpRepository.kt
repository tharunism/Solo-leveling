package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LevelUpRepository(private val db: LevelUpDatabase) {
    private val dao = db.levelUpDao()

    val userProgressFlow: Flow<UserProgress?> = dao.getUserProgressFlow()
    val allQuestsFlow: Flow<List<Quest>> = dao.getAllQuestsFlow()
    val allGuildsFlow: Flow<List<Guild>> = dao.getAllGuildsFlow()
    val allMarketplaceItemsFlow: Flow<List<MarketplaceItem>> = dao.getAllMarketplaceItemsFlow()

    fun getChatMessagesFlow(guildId: Int): Flow<List<ChatMessage>> = dao.getChatMessagesFlow(guildId)

    // Pre-populate data if database is initialized for the first time
    suspend fun initializeDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        val currentProgress = dao.getUserProgress()
        if (currentProgress == null) {
            // 1. Initial Profile
            dao.insertUserProgress(
                UserProgress(
                    id = 1,
                    nickname = "Jin-Woo",
                    avatarUrl = "avatar_shadow_monarch",
                    currentLevel = 1,
                    currentXp = 25,
                    requiredXp = 100,
                    totalCoins = 350,
                    rankTitle = "E Rank",
                    dailyStreak = 3,
                    lastActiveTimestamp = System.currentTimeMillis(),
                    isSoundEnabled = true,
                    guildId = 1, // Pre-joined into Shadow Monarchs
                    customTheme = "Cyberpunk",
                    mobileNumber = "+1 (555) 018-9321",
                    country = "South Korea",
                    stateCity = "Seoul",
                    age = 24,
                    interests = "Leveling Up, Physical Training, Neural Coding",
                    skillCategory = "Shadow Sovereign",
                    reputationScore = 98,
                    fraudScore = 2
                )
            )

            // 2. Pre-defined Quests
            val initialQuests = listOf(
                Quest(
                    title = "Daily Warmup: 100 Pushups & 100 Squats",
                    category = "Fitness",
                    difficulty = "E-Rank",
                    xpReward = 30,
                    coinReward = 50,
                    deadline = "Daily",
                    verificationType = "GPS",
                    progress = 0f,
                    isCompleted = false
                ),
                Quest(
                    title = "Deep Work Session: Write Compiler Logic",
                    category = "Coding",
                    difficulty = "C-Rank",
                    xpReward = 80,
                    coinReward = 120,
                    deadline = "Daily",
                    verificationType = "AI",
                    progress = 0f,
                    isCompleted = false
                ),
                Quest(
                    title = "Weekly Core Challenge: 10km Run",
                    category = "Fitness",
                    difficulty = "B-Rank",
                    xpReward = 150,
                    coinReward = 200,
                    deadline = "Weekly",
                    verificationType = "GPS",
                    progress = 0f,
                    isCompleted = false
                ),
                Quest(
                    title = "Elevator Pitch Practice: 3 Minutes Out Loud",
                    category = "Communication",
                    difficulty = "E-Rank",
                    xpReward = 20,
                    coinReward = 30,
                    deadline = "Daily",
                    verificationType = "Manual",
                    progress = 0f,
                    isCompleted = false
                ),
                Quest(
                    title = "Read 5 Chapters of Strategic Leadership Book",
                    category = "Business",
                    difficulty = "C-Rank",
                    xpReward = 75,
                    coinReward = 100,
                    deadline = "2 Days",
                    verificationType = "Manual",
                    progress = 0f,
                    isCompleted = false
                ),
                Quest(
                    title = "Mind Sanctuary: 20 Minutes Meditation",
                    category = "Spirituality",
                    difficulty = "D-Rank",
                    xpReward = 40,
                    coinReward = 60,
                    deadline = "Daily",
                    verificationType = "Manual",
                    progress = 1.0f,
                    isCompleted = true,
                    isVerified = true
                )
            )
            dao.insertQuests(initialQuests)

            // 3. Initial Guilds
            val initialGuilds = listOf(
                Guild(1, "Shadow Monarchs", "The ultimate guild for lone-wolf hunters pushing core human potential.", 5, 24500, 158000, 1, "Arise, Hunter!"),
                Guild(2, "Ahjin Guild", "A professional elite association for technical & strategic leveling.", 4, 18200, 120000, 2, "Beyond Limits"),
                Guild(3, "Fiend Guild", "Heavy fitness, martial arts, endurance training specialists.", 3, 9800, 75000, 3, "Unstoppable Force"),
                Guild(4, "White Tiger Association", "Corporate growth, business scaling, strategic finance elite.", 3, 8500, 68000, 4, "Ferocious Precision")
            )
            dao.insertGuilds(initialGuilds)

            // 4. Initial Marketplace Items
            val initialItems = listOf(
                MarketplaceItem(name = "Monarch's S-Rank Awakening Badge", description = "Unlock premium golden neon dashboard visual filters & prestige title.", cost = 500, category = "Reward"),
                MarketplaceItem(name = "Spell Book: Clean Code Mastery", description = "Unlock A-Rank exclusive algorithms and software metrics quests.", cost = 250, category = "Skill"),
                MarketplaceItem(name = "Elixir of Perpetual Focus (Double XP Rune)", description = "Get double XP for the next 3 client-side quest submissions.", cost = 300, category = "Upgrade"),
                MarketplaceItem(name = "Elite Partner Software Engineering Internship", description = "LevelUp Nation verified fast-track placement code voucher.", cost = 1200, category = "Internship"),
                MarketplaceItem(name = "Digital Hermit S-Rank Discipline title", description = "Acquire high-ranking social media profile badge.", cost = 400, category = "Reward")
            )
            dao.insertMarketplaceItems(initialItems)

            // 5. Initial Chat Messages
            val initialChats = listOf(
                ChatMessage(id = 0, guildId = 1, senderName = "Cha Hae-In", senderAvatar = "avatar_cha", messageText = "Welcome back, Hunter Jin-Woo! Are you ready for today's raid?", timestamp = System.currentTimeMillis() - 600000),
                ChatMessage(id = 0, guildId = 1, senderName = "Woo Jin-Chul", senderAvatar = "avatar_woo", messageText = "Hunters, S-Rank gate has appeared. Log your pushups immediately.", timestamp = System.currentTimeMillis() - 300000),
                ChatMessage(id = 0, guildId = 1, senderName = "You", senderAvatar = "avatar_shadow_monarch", messageText = "Understood. The shadow army registers compliance.", timestamp = System.currentTimeMillis() - 10000)
            )
            for (chat in initialChats) {
                dao.insertChatMessage(chat)
            }
        }
    }

    // --- Core RPG Leveling Calculations ---

    suspend fun completeQuest(questId: Int, imageProofPath: String? = null): LevelUpResult = withContext(Dispatchers.IO) {
        val user = dao.getUserProgress() ?: return@withContext LevelUpResult.Error("User progress not found")
        val allQuests = dao.getAllQuestsFlow().firstOrNull() ?: emptyList()
        val quest = allQuests.find { it.id == questId } ?: return@withContext LevelUpResult.Error("Quest not found")

        if (quest.isCompleted) {
            return@withContext LevelUpResult.Error("Quest is already completed")
        }

        // Complete Quest
        val updatedQuest = quest.copy(
            isCompleted = true,
            isVerified = true,
            imageUrlProof = imageProofPath,
            progress = 1.0f
        )
        dao.updateQuest(updatedQuest)

        // Give XP and Coins
        var newXp = user.currentXp + quest.xpReward
        var newLevel = user.currentLevel
        var reqXp = user.requiredXp
        var levelUpOccurred = false
        val levelsAcheived = mutableListOf<Int>()

        while (newXp >= reqXp) {
            newLevel += 1
            newXp -= reqXp
            reqXp = (reqXp * 1.5).toInt()
            levelUpOccurred = true
            levelsAcheived.add(newLevel)
        }

        // Determine hunter rank based on levels
        val rank = when {
            newLevel >= 30 -> "National Rank"
            newLevel >= 25 -> "S Rank"
            newLevel >= 20 -> "A Rank"
            newLevel >= 15 -> "B Rank"
            newLevel >= 10 -> "C Rank"
            newLevel >= 5 -> "D Rank"
            else -> "E Rank"
        }

        val updatedUser = user.copy(
            currentLevel = newLevel,
            currentXp = newXp,
            requiredXp = reqXp,
            totalCoins = user.totalCoins + quest.coinReward,
            rankTitle = rank,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        dao.updateUserProgress(updatedUser)

        return@withContext LevelUpResult.Success(
            xpGained = quest.xpReward,
            coinsGained = quest.coinReward,
            leveledUp = levelUpOccurred,
            newLevel = newLevel,
            newRank = rank
        )
    }

    // --- Dynamic Quest Creation ---

    suspend fun addNewQuest(title: String, category: String, difficulty: String, deadline: String, verificationType: String) = withContext(Dispatchers.IO) {
        val xp = when(difficulty) {
            "S-Rank" -> 200
            "A-Rank" -> 150
            "B-Rank" -> 100
            "C-Rank" -> 70
            "D-Rank" -> 40
            else -> 20
        }
        val coins = (xp * 1.2).toInt()

        val newQuest = Quest(
            title = title,
            category = category,
            difficulty = difficulty,
            xpReward = xp,
            coinReward = coins,
            deadline = deadline,
            verificationType = verificationType,
            progress = 0f,
            isCompleted = false
        )
        dao.insertQuest(newQuest)
    }

    suspend fun deleteQuest(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteQuestById(id)
    }

    // --- Store Purchases ---

    suspend fun buyMarketItem(itemId: Int): PurchaseResult = withContext(Dispatchers.IO) {
        val user = dao.getUserProgress() ?: return@withContext PurchaseResult.Failure("Profile not found")
        val itemsFlow = dao.getAllMarketplaceItemsFlow().firstOrNull() ?: emptyList()
        val item = itemsFlow.find { it.id == itemId } ?: return@withContext PurchaseResult.Failure("Item not found")

        if (item.isPurchased) {
            return@withContext PurchaseResult.Failure("Item already purchased")
        }

        if (user.totalCoins < item.cost) {
            return@withContext PurchaseResult.Failure("Insufficient Coin Budget")
        }

        // Deduct coins & set purchased
        dao.updateUserProgress(user.copy(totalCoins = user.totalCoins - item.cost))
        
        val randomVoucher = "ARW-${(1000..9999).random()}-${(10..99).random()}"
        dao.updateMarketplaceItem(item.copy(isPurchased = true, unlockCode = randomVoucher))

        return@withContext PurchaseResult.Success(item.name, randomVoucher)
    }

    // --- Guild & Chat System ---

    suspend fun joinGuild(guildId: Int) = withContext(Dispatchers.IO) {
        val user = dao.getUserProgress() ?: return@withContext
        dao.updateUserProgress(user.copy(guildId = guildId))
    }

    suspend fun leaveGuild() = withContext(Dispatchers.IO) {
        val user = dao.getUserProgress() ?: return@withContext
        dao.updateUserProgress(user.copy(guildId = -1))
    }

    suspend fun sendGuildChatMessage(guildId: Int, senderName: String, text: String) = withContext(Dispatchers.IO) {
        val message = ChatMessage(
            guildId = guildId,
            senderName = senderName,
            senderAvatar = "avatar_shadow_monarch",
            messageText = text,
            timestamp = System.currentTimeMillis()
        )
        dao.insertChatMessage(message)
    }

    suspend fun changeSettings(
        name: String,
        sound: Boolean,
        theme: String,
        mobileNumber: String = "+1 (555) 018-9321",
        country: String = "South Korea",
        stateCity: String = "Seoul",
        age: Int = 24,
        interests: String = "Leveling Up, Physical Training, Neural Coding",
        skillCategory: String = "Shadow Sovereign"
    ) = withContext(Dispatchers.IO) {
        val user = dao.getUserProgress() ?: return@withContext
        dao.updateUserProgress(
            user.copy(
                nickname = name,
                isSoundEnabled = sound,
                customTheme = theme,
                mobileNumber = mobileNumber,
                country = country,
                stateCity = stateCity,
                age = age,
                interests = interests,
                skillCategory = skillCategory
            )
        )
    }

    // --- Gemini AI Mentor & Verification Logic ---

    suspend fun getAIMentorAdvice(userProfile: UserProgress, recentQuests: List<Quest>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "DUMMY_KEY_LEVELUP") {
            return@withContext getOfflineMentorAdvice(userProfile, recentQuests)
        }

        val completedCount = recentQuests.count { it.isCompleted }
        val pendingCount = recentQuests.count { !it.isCompleted }
        val categories = recentQuests.map { it.category }.distinct().joinToString()

        val prompt = """
            You are the Sovereign Shadow Mentor from Solo Leveling. You guide a real-world Hunter (user) on their path of self-improvement.
            Current Stats:
            Hunter Level: ${userProfile.currentLevel}
            Rank: ${userProfile.rankTitle}
            Streak days: ${userProfile.dailyStreak}
            Coin Wallet balance: ${userProfile.totalCoins}
            Completed Quests count: $completedCount
            Pending Quests count: $pendingCount
            Active disciplines: $categories
            
            Provide a short, intense roleplay mentoring instruction (3-4 sentences maximum). Be strict, motivating, futuristic, and references "awakening", "arise", or "leveling system" styled precisely like the Solo Leveling series. Do NOT use markdown markdown list boxes or headers. Keep the structure direct and engaging.
        """.trimIndent()

        try {
            val response = GeminiNetwork.apiService.generateContent(
                apiKey,
                GeminiRequest(contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))))
            )
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                text.trim()
            } else {
                getOfflineMentorAdvice(userProfile, recentQuests)
            }
        } catch (e: Exception) {
            getOfflineMentorAdvice(userProfile, recentQuests)
        }
    }

    /**
     * AI Quest generation using the Gemini API.
     */
    suspend fun generateAIDailyMission(): Map<String, String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val offlineMissions = listOf(
            mapOf("title" to "Sovereign Cardio: 20-min High Intensity Boxing Study", "category" to "Fitness", "difficulty" to "C-Rank", "deadline" to "Daily", "verify" to "Manual"),
            mapOf("title" to "A-Rank Insight: Analyze architectural clean architecture templates", "category" to "Coding", "difficulty" to "A-Rank", "deadline" to "Daily", "verify" to "AI"),
            mapOf("title" to "Monarch Presence: Present a strategic debate mockup to a colleague", "category" to "Communication", "difficulty" to "B-Rank", "deadline" to "Daily", "verify" to "Manual"),
            mapOf("title" to "Discipline Lockdown: Wake up before 6:00 AM & study financial ledger", "category" to "Spirituality", "difficulty" to "D-Rank", "deadline" to "Daily", "verify" to "Manual")
        )
        val selected = offlineMissions.random()

        if (apiKey.isEmpty() || apiKey == "DUMMY_KEY_LEVELUP") {
            return@withContext selected
        }

        val prompt = """
            Generate exactly one unique daily self-improvement mission for a Solo Leveling RPG-based task tracker.
            It must be realistic but sound gamified, heroic, and extreme.
            Provide the output as raw JSON only with these string fields: "title", "category" (Must be either: Fitness, Coding, Communication, Business, Spirituality, Discipline, Social Impact), "difficulty" (E-Rank, D-Rank, C-Rank, B-Rank, A-Rank, S-Rank), "deadline" (always "Daily"), "verify" (AI, GPS, or Manual).
            Do not enclose in standard markdown codeblocks (no ```json). Keep the JSON simple and clean without tabs.
        """.trimIndent()

        try {
            val response = GeminiNetwork.apiService.generateContent(
                apiKey,
                GeminiRequest(contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))))
            )
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                // Parse simple JSON fields manually or with regex to avoid parsing failures on dynamic responses
                val titleRegex = """"title"\s*:\s*"([^"]+)"""".toRegex()
                val catRegex = """"category"\s*:\s*"([^"]+)"""".toRegex()
                val diffRegex = """"difficulty"\s*:\s*"([^"]+)"""".toRegex()
                val verifyRegex = """"verify"\s*:\s*"([^"]+)"""".toRegex()

                val title = titleRegex.find(text)?.groupValues?.get(1) ?: selected["title"]!!
                val category = catRegex.find(text)?.groupValues?.get(1) ?: selected["category"]!!
                val difficulty = diffRegex.find(text)?.groupValues?.get(1) ?: selected["difficulty"]!!
                val verify = verifyRegex.find(text)?.groupValues?.get(1) ?: selected["verify"]!!

                mapOf(
                    "title" to title,
                    "category" to category,
                    "difficulty" to difficulty,
                    "deadline" to "Daily",
                    "verify" to verify
                )
            } else {
                selected
            }
        } catch (e: Exception) {
            selected
        }
    }

    /**
     * AI Quest Verification using the Gemini API.
     */
    suspend fun verifyProofWithAI(questTitle: String, userWrittenSummary: String): AIVerificationResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "DUMMY_KEY_LEVELUP") {
            return@withContext getOfflineVerification(questTitle, userWrittenSummary)
        }

        val prompt = """
            You are the Monarch System AI Quest Referee. Review this self-improvement task proof submission.
            Quest Title: "$questTitle"
            Hunter's Submitted Proof Text: "$userWrittenSummary"

            Decide if this proof is realistic, genuine, and meets reasonable performance metrics.
            Generate a short evaluation.
            Format response as exact JSON:
            {
               "approved": true/false,
               "systemFeedback": "3-sentence gaming evaluation with rank rating",
               "score": 0 to 100
            }
            Output raw JSON ONLY. No markdown formatted wrappers.
        """.trimIndent()

        try {
            val response = GeminiNetwork.apiService.generateContent(
                apiKey,
                GeminiRequest(contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))))
            )
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                val approved = text.contains("\"approved\"\\s*:\\s*true".toRegex())
                val score = """"score"\s*:\s*(\d+)""".toRegex().find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 85
                val feedback = """"systemFeedback"\s*:\s*"([^"]+)"""".toRegex().find(text)?.groupValues?.get(1) ?: "AI scanned verified proof successfully. Monarch status authenticated."
                AIVerificationResult(approved, feedback, score)
            } else {
                getOfflineVerification(questTitle, userWrittenSummary)
            }
        } catch (e: Exception) {
            getOfflineVerification(questTitle, userWrittenSummary)
        }
    }

    private fun getOfflineMentorAdvice(user: UserProgress, recentQuests: List<Quest>): String {
        val advices = listOf(
            "Hunter, the spacing between your Awakening levels is narrowing. The Shadow System registers your active ${user.dailyStreak}-day discipline streak. Arise and log more exercise instantly!",
            "E-Rank limits are meant to be broken. I command you to engage in higher difficulty raids—your physical form needs coding and conditioning optimization.",
            "You have earned ${user.totalCoins} virtual coins, Hunter Jin-Woo. Convert them in the Monarch Market for S-Rank resources before your focus disintegrates.",
            "A S-Rank gate has appeared containing deep intellectual obstacles. Your active quests are waiting. Fail, and you will face immediate system penalties."
        )
        return advices.random()
    }

    private fun getOfflineVerification(title: String, summary: String): AIVerificationResult {
        val isApproved = summary.length >= 8
        val feedback = if (isApproved) {
            "System scanning complete! Verified genuine intellectual/physical endeavor for quest \"$title\". System logs approved. Rank upgrade authorized."
        } else {
            "System scan failed! Proof explanation too short or lacking authentication vectors. Retake training immediately."
        }
        val score = if (isApproved) (75..98).random() else 35
        return AIVerificationResult(isApproved, feedback, score)
    }
}

// --- Result Wrappers ---

sealed class LevelUpResult {
    data class Success(
        val xpGained: Int,
        val coinsGained: Int,
        val leveledUp: Boolean,
        val newLevel: Int,
        val newRank: String
    ) : LevelUpResult()
    data class Error(val message: String) : LevelUpResult()
}

sealed class PurchaseResult {
    data class Success(val itemName: String, val code: String) : PurchaseResult()
    data class Failure(val message: String) : PurchaseResult()
}

data class AIVerificationResult(
    val approved: Boolean,
    val feedback: String,
    val score: Int
)
