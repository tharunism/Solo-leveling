package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LevelUpViewModel(application: Application) : AndroidViewModel(application) {
    
    // Database and Repository reference
    private val database: LevelUpDatabase = Room.databaseBuilder(
        application,
        LevelUpDatabase::class.java,
        "levelup_nation_database"
    )
    .fallbackToDestructiveMigration()
    .build()

    val repository = LevelUpRepository(database)

    // Reactive State Holders
    val userProgress: StateFlow<UserProgress?> = repository.userProgressFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val quests: StateFlow<List<Quest>> = repository.allQuestsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val guilds: StateFlow<List<Guild>> = repository.allGuildsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val marketItems: StateFlow<List<MarketplaceItem>> = repository.allMarketplaceItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat room messages flows dynamically depending on joined guild
    private val _currentGuildId = MutableStateFlow(-1)
    val chatMessages: StateFlow<List<ChatMessage>> = _currentGuildId
        .flatMapLatest { guildId ->
            if (guildId != -1) {
                repository.getChatMessagesFlow(guildId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Mentor Advice State
    private val _aiMentorAdvice = MutableStateFlow("Establish your target discipline, Hunter. The Shadow army registers your potential.")
    val aiMentorAdvice: StateFlow<String> = _aiMentorAdvice.asStateFlow()

    private val _isAiMentorLoading = MutableStateFlow(false)
    val isAiMentorLoading: StateFlow<Boolean> = _isAiMentorLoading.asStateFlow()

    // AI Quest Verification states
    private val _isAiVerifying = MutableStateFlow(false)
    val isAiVerifying: StateFlow<Boolean> = _isAiVerifying.asStateFlow()

    private val _aiVerificationResult = MutableStateFlow<AIVerificationResult?>(null)
    val aiVerificationResult: StateFlow<AIVerificationResult?> = _aiVerificationResult.asStateFlow()

    // Celebration/Notification triggers
    private val _levelUpCelebration = MutableStateFlow<LevelUpResult.Success?>(null)
    val levelUpCelebration: StateFlow<LevelUpResult.Success?> = _levelUpCelebration.asStateFlow()

    private val _purchaseSuccess = MutableStateFlow<PurchaseResult.Success?>(null)
    val purchaseSuccess: StateFlow<PurchaseResult.Success?> = _purchaseSuccess.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            // Populate database with mock and startup data if empty
            repository.initializeDatabaseIfNeeded()
            
            // Sync current joined guild with the chat messages flow
            userProgress.collect { progress ->
                _currentGuildId.value = progress?.guildId ?: -1
            }
        }
        
        // Initial call to populate AI advice after a slight delay
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            refreshMentorAdvice()
        }
    }

    // --- Action Methods ---

    fun completeQuest(questId: Int, imageProofPath: String? = null) {
        viewModelScope.launch {
            val result = repository.completeQuest(questId, imageProofPath)
            when (result) {
                is LevelUpResult.Success -> {
                    if (result.leveledUp) {
                        _levelUpCelebration.value = result
                    } else {
                        _toastMessage.value = "Quest Completed! +${result.xpGained} XP | +${result.coinsGained} Coins"
                    }
                }
                is LevelUpResult.Error -> {
                    _toastMessage.value = result.message
                }
            }
        }
    }

    fun addNewQuest(title: String, category: String, difficulty: String, deadline: String, verificationType: String) {
        viewModelScope.launch {
            if (title.isBlank()) {
                _toastMessage.value = "Quest description cannot be empty!"
                return@launch
            }
            repository.addNewQuest(title, category, difficulty, deadline, verificationType)
            _toastMessage.value = "New $difficulty Quest added successfully!"
        }
    }

    fun deleteQuest(id: Int) {
        viewModelScope.launch {
            repository.deleteQuest(id)
            _toastMessage.value = "Quest abandoned and removed!"
        }
    }

    fun buyMarketItem(itemId: Int) {
        viewModelScope.launch {
            val purchase = repository.buyMarketItem(itemId)
            when (purchase) {
                is PurchaseResult.Success -> {
                    _purchaseSuccess.value = purchase
                }
                is PurchaseResult.Failure -> {
                    _toastMessage.value = purchase.message
                }
            }
        }
    }

    fun joinGuild(guildId: Int) {
        viewModelScope.launch {
            repository.joinGuild(guildId)
            val guild = repository.allGuildsFlow.firstOrNull()?.find { it.id == guildId }
            _toastMessage.value = "Joined guild: ${guild?.name ?: "Shadow Guild"}"
        }
    }

    fun leaveGuild() {
        viewModelScope.launch {
            repository.leaveGuild()
            _toastMessage.value = "Departed from guild."
        }
    }

    fun sendGuildChatMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val progress = userProgress.value ?: return@launch
            if (progress.guildId == -1) {
                _toastMessage.value = "Create or join a guild to communicate!"
                return@launch
            }
            repository.sendGuildChatMessage(progress.guildId, progress.nickname, text)
        }
    }

    fun editProfile(
        nickname: String,
        isSoundEnabled: Boolean,
        customTheme: String,
        mobileNumber: String = "+1 (555) 018-9321",
        country: String = "South Korea",
        stateCity: String = "Seoul",
        age: Int = 24,
        interests: String = "Leveling Up, Physical Training, Neural Coding",
        skillCategory: String = "Shadow Sovereign"
    ) {
        viewModelScope.launch {
            repository.changeSettings(
                nickname,
                isSoundEnabled,
                customTheme,
                mobileNumber,
                country,
                stateCity,
                age,
                interests,
                skillCategory
            )
            _toastMessage.value = "Hunter files updated successfully!"
        }
    }

    fun clearLevelUpCelebration() {
        _levelUpCelebration.value = null
    }

    fun clearPurchaseSuccess() {
        _purchaseSuccess.value = null
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    // --- Gemini Interactive Methods ---

    fun refreshMentorAdvice() {
        viewModelScope.launch {
            val progress = userProgress.value ?: return@launch
            val questList = quests.value
            _isAiMentorLoading.value = true
            val advice = repository.getAIMentorAdvice(progress, questList)
            _aiMentorAdvice.value = advice
            _isAiMentorLoading.value = false
        }
    }

    fun generateAIMission() {
        viewModelScope.launch {
            _isAiMentorLoading.value = true
            val mission = repository.generateAIDailyMission()
            repository.addNewQuest(
                title = mission["title"] ?: "AI Daily Mission",
                category = mission["category"] ?: "Discipline",
                difficulty = mission["difficulty"] ?: "C-Rank",
                deadline = mission["deadline"] ?: "Daily",
                verificationType = mission["verify"] ?: "AI"
            )
            val advice = repository.getAIMentorAdvice(userProgress.value ?: return@launch, quests.value)
            _aiMentorAdvice.value = "New Objective added, Hunter. " + advice
            _isAiMentorLoading.value = false
            _toastMessage.value = "AI Shadow Command assigned a new objective!"
        }
    }

    fun triggerAIVerification(questId: Int, summaryOfTask: String) {
        viewModelScope.launch {
            if (summaryOfTask.isBlank() || summaryOfTask.length < 8) {
                _toastMessage.value = "Provide a detailed summary (at least 8 characters) to scan!"
                return@launch
            }
            _isAiVerifying.value = true
            val quest = quests.value.find { it.id == questId } ?: return@launch
            val result = repository.verifyProofWithAI(quest.title, summaryOfTask)
            _aiVerificationResult.value = result
            _isAiVerifying.value = false

            if (result.approved) {
                // Instantly complete quest
                completeQuest(questId, imageProofPath = "AI_VERIFIED_SCORE_${result.score}")
            } else {
                _toastMessage.value = "AI Verification Failed. Try adding more substantial description."
            }
        }
    }

    fun clearVerificationResult() {
        _aiVerificationResult.value = null
    }
}
