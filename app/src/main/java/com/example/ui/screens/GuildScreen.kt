package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.data.Guild
import com.example.data.UserProgress
import com.example.ui.LevelUpViewModel
import com.example.ui.theme.*

@Composable
fun GuildScreen(
    viewModel: LevelUpViewModel,
    modifier: Modifier = Modifier
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val guilds by viewModel.guilds.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    var activeTab by remember { mutableStateOf("Raids") } // Raids, Chat, Rankings, Build
    var typedMessage by remember { mutableStateOf("") }

    // Guild Creation variables
    var showCreateForm by remember { mutableStateOf(false) }
    var newGuildName by remember { mutableStateOf("") }
    var newGuildMotto by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
    ) {
        userProgress?.let { progress ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .safeDrawingPadding()
            ) {
                // Header Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "◆ COOPERATIVE ALLIANCE GATE ◆",
                            color = CyberPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "GUILD HALL",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    if (progress.guildId == -1) {
                        IconButton(
                            onClick = { showCreateForm = !showCreateForm },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyberPrimary.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.AddHome, contentDescription = "Create Guild", tint = CyberPrimary)
                        }
                    } else {
                        IconButton(
                            onClick = { viewModel.leaveGuild() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ErrorColorRed.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Depart Alliance", tint = ErrorColorRed)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Build / Create Guild Module
                AnimatedVisibility(visible = showCreateForm && progress.guildId == -1) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, CyberPrimary),
                        shape = CutCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("FORGE A NEW ALLIANCE", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            OutlinedTextField(
                                value = newGuildName,
                                onValueChange = { newGuildName = it },
                                label = { Text("Guild Name (e.g., Ahjin Solo)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("guild_name_field")
                            )
                            OutlinedTextField(
                                value = newGuildMotto,
                                onValueChange = { newGuildMotto = it },
                                label = { Text("Sovereign Code Motto (e.g., Arise!)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("guild_motto_field")
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { showCreateForm = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
                                ) {
                                    Text("CANCEL", color = TextSecondary)
                                }
                                Button(
                                    onClick = {
                                        if (newGuildName.isNotBlank()) {
                                            viewModel.joinGuild(1) // joins template
                                            showCreateForm = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                                ) {
                                    Text("ACTIVATE ALLIANCE", color = Color.Black)
                                }
                            }
                        }
                    }
                }

                if (progress.guildId == -1) {
                    // Displays Guild Directory to join high level clans
                    Text(
                        text = "SELECT AN ACTIVE GUILD TO COOPERATE:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(guilds) { guild ->
                            GuildDirectoryCard(
                                guild = guild,
                                onJoin = { viewModel.joinGuild(guild.id) }
                            )
                        }
                    }
                } else {
                    // User already has a Guild! Show tabs
                    val joinedGuild = guilds.find { it.id == progress.guildId }
                    joinedGuild?.let { currentGuild ->
                        // Mini Profile Details
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(0.5.dp, CyberSurfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CyberSecondary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Group, contentDescription = null, tint = CyberSecondary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(currentGuild.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("\"${currentGuild.motto}\" | Lvl ${currentGuild.level}", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom Tab Rows
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberSurface)
                        ) {
                            listOf("Raids", "Chat", "Rankings").forEach { tab ->
                                val selected = activeTab == tab
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) CyberSecondary.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { activeTab = tab }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        tab.uppercase(),
                                        color = if (selected) CyberSecondary else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Tab Layout rendering
                        when (activeTab) {
                            "Raids" -> {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("⚔ CLAN WAR: RAID RAID OBJECTIVES", color = CyberPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    
                                    WarChallengeCard(
                                        title = "Infinite Gates: 50,000 Pushups Collective Challenge",
                                        progress = 0.65f,
                                        timeLeft = "3 Days Left",
                                        reward = "2,000 System Coins"
                                    )

                                    WarChallengeCard(
                                        title = "S-Rank Citadel: Code 2,500 Functional Unit Tests",
                                        progress = 0.42f,
                                        timeLeft = "7 Days Left",
                                        reward = "A-Rank Skill Unlock"
                                    )
                                }
                            }
                            "Chat" -> {
                                // Responsive Real-time chatting channel
                                Column(modifier = Modifier.weight(1f)) {
                                    LazyColumn(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(chatMessages) { msg ->
                                            ChatRowItem(msg = msg, selfName = progress.nickname)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Chat Input Box field
                                    Row(
                                        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = typedMessage,
                                            onValueChange = { typedMessage = it },
                                            placeholder = { Text("Log coordinate status message...", fontSize = 11.sp) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                unfocusedContainerColor = CyberSurface.copy(alpha = 0.5f),
                                                focusedContainerColor = CyberSurface
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f).testTag("guild_chat_text_input")
                                        )

                                        IconButton(
                                            onClick = {
                                                viewModel.sendGuildChatMessage(typedMessage)
                                                typedMessage = ""
                                            },
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(CyberSecondary)
                                        ) {
                                            Icon(Icons.Default.Send, contentDescription = "Send Msg", tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                            "Rankings" -> {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(guilds) { guild ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text("#${guild.rank}", color = CyberPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                                Text(guild.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Text("${guild.xpPoints} XP", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GuildDirectoryCard(
    guild: Guild,
    onJoin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(8.dp))
            .background(CyberSurface)
            .border(1.dp, CyberSurfaceVariant, CutCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(guild.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberPrimary.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("RANK #${guild.rank}", color = CyberPrimary, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(guild.description, color = TextSecondary, fontSize = 11.sp, maxLines = 2)

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = CyberSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Lvl: ${guild.level}", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("Squad: ${guild.membersCount}", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }

                Button(
                    onClick = onJoin,
                    shape = CutCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("JOIN RAIDS", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WarChallengeCard(
    title: String,
    progress: Float,
    timeLeft: String,
    reward: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CyberBg)
            .border(0.5.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(timeLeft, color = ErrorColorRed, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }

            // Progress bar
            LinearProgressIndicator(
                progress = progress,
                color = CyberSecondary,
                trackColor = CyberSurfaceVariant,
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Quest Completed: ${(progress * 100).toInt()}%", color = TextSecondary, fontSize = 9.sp)
                Text("Rewards: $reward", color = RankS, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun ChatRowItem(
    msg: ChatMessage,
    selfName: String
) {
    val isSelf = msg.senderName == "You" || msg.senderName.equals(selfName, ignoreCase = true)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
                .background(if (isSelf) CyberSecondary.copy(alpha = 0.15f) else CyberSurface)
                .border(
                    0.5.dp,
                    if (isSelf) CyberSecondary else CyberSurfaceVariant,
                    CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                )
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = msg.senderName,
                    color = if (isSelf) CyberSecondary else CyberPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = msg.messageText,
                    color = TextPrimary,
                    fontSize = 12.sp
                )
            }
        }
    }
}
