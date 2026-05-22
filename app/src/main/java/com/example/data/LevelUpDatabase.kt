package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- Room Entities ---

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1, // Only 1 local user profile
    val nickname: String = "Jin-Woo",
    val avatarUrl: String = "avatar_shadow_monarch",
    val currentLevel: Int = 1,
    val currentXp: Int = 0,
    val requiredXp: Int = 100,
    val totalCoins: Int = 500,
    val rankTitle: String = "E Rank",
    val dailyStreak: Int = 0,
    val lastActiveTimestamp: Long = 0L,
    val isSoundEnabled: Boolean = true,
    val guildId: Int = -1, // -1 means no guild
    val customTheme: String = "Cyberpunk", // Cyberpunk, Shadow, Neon
    val mobileNumber: String = "+1 (555) 018-9321",
    val country: String = "South Korea",
    val stateCity: String = "Seoul",
    val age: Int = 24,
    val interests: String = "Leveling Up, Physical Training, Neural Coding",
    val skillCategory: String = "Shadow Sovereign",
    val reputationScore: Int = 98,
    val fraudScore: Int = 2 // Low cheat suspicion
)

@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Fitness, Coding, Communication, Business, Spirituality, Discipline, Social Impact
    val difficulty: String, // E-Rank, D-Rank, C-Rank, B-Rank, A-Rank, S-Rank
    val xpReward: Int,
    val coinReward: Int,
    val isCompleted: Boolean = false,
    val progress: Float = 0f, // 0.0 to 1.0
    val deadline: String, // format e.g., "Daily", "Weekly", "2 Days"
    val verificationType: String = "Manual", // AI, GPS, Manual
    val imageUrlProof: String? = null,
    val isVerified: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "guilds")
data class Guild(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val level: Int = 1,
    val membersCount: Int = 1,
    val xpPoints: Int = 0,
    val rank: Int = 1,
    val motto: String = ""
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val guildId: Int,
    val senderName: String,
    val senderAvatar: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "marketplace_items")
data class MarketplaceItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val cost: Int,
    val category: String, // Reward, Skill, Upgrade, Internship
    val isPurchased: Boolean = false,
    val unlockCode: String = ""
)

// --- DAO Definitions ---

@Dao
interface LevelUpDao {
    // User progress
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgressFlow(): Flow<UserProgress?>

    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    suspend fun getUserProgress(): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(progress: UserProgress)

    @Update
    suspend fun updateUserProgress(progress: UserProgress)

    // Quests
    @Query("SELECT * FROM quests ORDER BY timestamp DESC")
    fun getAllQuestsFlow(): Flow<List<Quest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: Quest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<Quest>)

    @Update
    suspend fun updateQuest(quest: Quest)

    @Query("DELETE FROM quests WHERE id = :id")
    suspend fun deleteQuestById(id: Int)

    // Guilds
    @Query("SELECT * FROM guilds ORDER BY xpPoints DESC")
    fun getAllGuildsFlow(): Flow<List<Guild>>

    @Query("SELECT * FROM guilds WHERE id = :guildId LIMIT 1")
    suspend fun getGuildById(guildId: Int): Guild?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuilds(guilds: List<Guild>)

    // Marketplace Items
    @Query("SELECT * FROM marketplace_items ORDER BY cost ASC")
    fun getAllMarketplaceItemsFlow(): Flow<List<MarketplaceItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplaceItems(items: List<MarketplaceItem>)

    @Update
    suspend fun updateMarketplaceItem(item: MarketplaceItem)

    // Chat
    @Query("SELECT * FROM chat_messages WHERE guildId = :guildId ORDER BY timestamp ASC")
    fun getChatMessagesFlow(guildId: Int): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)
}

// --- App Database Class ---

@Database(
    entities = [
        UserProgress::class,
        Quest::class,
        Guild::class,
        ChatMessage::class,
        MarketplaceItem::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LevelUpDatabase : RoomDatabase() {
    abstract fun levelUpDao(): LevelUpDao
}
