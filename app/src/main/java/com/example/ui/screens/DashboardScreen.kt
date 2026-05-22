package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Quest
import com.example.data.UserProgress
import com.example.ui.LevelUpViewModel
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: LevelUpViewModel,
    onNavigateToQuests: () -> Unit,
    onNavigateToGuilds: () -> Unit,
    onNavigateToMentor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val quests by viewModel.quests.collectAsState()
    val aiAdvice by viewModel.aiMentorAdvice.collectAsState()
    val isMentorLoading by viewModel.isAiMentorLoading.collectAsState()

    val completedQuests = quests.filter { it.isCompleted }
    val pendingQuests = quests.filter { !it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .padding(16.dp)
            .safeDrawingPadding()
    ) {
        userProgress?.let { progress ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Character Window
                item {
                    CharacterStatusHeader(progress = progress)
                }

                // Systems Leveling Progress Tracker (Glow Animated XP Bar)
                item {
                    SystemXpModule(progress = progress)
                }

                // Dynamic Wallet & Streak Summary Cards
                item {
                    StatSummaryRow(progress = progress)
                }

                // AI Sovereign Mentor Advice Window
                item {
                    AIMentorPreviewCard(
                        advice = aiAdvice,
                        isLoading = isMentorLoading,
                        onRefreshAdvice = { viewModel.refreshMentorAdvice() },
                        onNavigateToMentor = onNavigateToMentor
                    )
                }

                // Quick Tasks / Daily Objective Quick List Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "◆ PENDING SYSTEM OBJECTIVES (${pendingQuests.size})",
                            color = CyberPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "View All Guild Quests ➔",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.clickable { onNavigateToQuests() }
                        )
                    }
                }

                if (pendingQuests.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = CutCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Terrain,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "GATE IS CLEARED!",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "All quests cleared. Summon AI Shadow Command to generate new S-Rank targets.",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(pendingQuests.take(3)) { quest ->
                        QuickQuestItem(
                            quest = quest,
                            onComplete = { viewModel.completeQuest(quest.id) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp)) // Avoid navigation bar overlap
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = CyberPrimary)
        }
    }
}

@Composable
fun CharacterStatusHeader(progress: UserProgress) {
    // Check Rank color border
    val rankColor = when (progress.rankTitle) {
        "National Rank" -> RankS
        "S Rank" -> RankS
        "A Rank" -> RankA
        "B Rank" -> RankB
        "C Rank" -> RankC
        "D Rank" -> RankD
        else -> RankE
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp))
            .background(CyberSurface)
            .border(
                1.dp,
                Brush.linearGradient(listOf(rankColor, CyberPrimary)),
                CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stylized Shadow Avatar Circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(CyberBg)
                    .border(2.dp, rankColor, CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(CyberSecondary, rankColor, CyberSecondary)
                            )
                        )
                )
            }

            // User Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = progress.nickname.uppercase(),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Hunter Category Icon (System Authorized)
                    Box(
                        modifier = Modifier
                            .clip(CutCornerShape(4.dp))
                            .background(rankColor.copy(alpha = 0.2f))
                            .border(0.5.dp, rankColor, CutCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = progress.rankTitle.uppercase(),
                            color = rankColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CyberPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Current Class: Lone Wolf Monarch (Lvl ${progress.currentLevel})",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun SystemXpModule(progress: UserProgress) {
    val xpPercent = (progress.currentXp.toFloat() / progress.requiredXp.toFloat()).coerceIn(0f, 1f)

    // Animated Level Progress
    val animatedProgress by animateFloatAsState(
        targetValue = xpPercent,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "xpAnim"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurface.copy(alpha = 0.6f))
            .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "SYSTEM PROGRESSION (XP LEVEL)",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "${progress.currentXp} / ${progress.requiredXp} XP",
                color = CyberPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Custom drawn fully-shaded cyberpunk XP Bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(CutCornerShape(4.dp))
                .background(Color(0xFF020617))
                .border(0.5.dp, CyberSurfaceVariant, CutCornerShape(4.dp))
        ) {
            val barWidth = this.size.width
            val barHeight = this.size.height

            // Draw current animated XP segment
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(CyberSecondary, CyberPrimary)
                ),
                size = androidx.compose.ui.geometry.Size(barWidth * animatedProgress, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Drawing high-tech dash metrics
            var dashX = 0f
            val dashStep = barWidth / 10f
            while (dashX < barWidth) {
                drawLine(
                    color = CyberBg.copy(alpha = 0.5f),
                    start = Offset(dashX, 0f),
                    end = Offset(dashX, barHeight),
                    strokeWidth = 1.dp.toPx()
                )
                dashX += dashStep
            }
        }
    }
}

@Composable
fun StatSummaryRow(progress: UserProgress) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Daily login streak tracker
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(CyberSurface)
                .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RankA.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = RankA
                    )
                }
                Column {
                    Text(
                        text = "STREAK",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${progress.dailyStreak} Days Active",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Coins Wallet balance representation
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(CyberSurface)
                .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RankS.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = null,
                        tint = RankS
                    )
                }
                Column {
                    Text(
                        text = "MONARCH WALLET",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${progress.totalCoins} COINS",
                        color = RankS,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun AIMentorPreviewCard(
    advice: String,
    isLoading: Boolean,
    onRefreshAdvice: () -> Unit,
    onNavigateToMentor: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurface)
            .border(
                BorderStroke(1.dp, Brush.radialGradient(listOf(CyberSecondary, Color.Transparent))),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = CyberSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "AI SHADOW COMMAND MENTOR",
                        color = CyberSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(
                    onClick = onRefreshAdvice,
                    modifier = Modifier.size(24.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = CyberSecondary, modifier = Modifier.size(14.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync AI Advice",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "\"$advice\"",
                color = TextPrimary,
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif,
                lineHeight = 16.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Engage System Terminal ➔",
                color = CyberSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .clickable { onNavigateToMentor() }
                    .align(Alignment.End)
            )
        }
    }
}

@Composable
fun QuickQuestItem(
    quest: Quest,
    onComplete: () -> Unit
) {
    // Determine category based color
    val catColor = when(quest.category) {
        "Fitness" -> RankA
        "Coding" -> CyberPrimary
        "Discipline" -> RankD
        else -> CyberSecondary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
            .background(CyberSurface.copy(alpha = 0.5f))
            .border(0.5.dp, CyberSurfaceVariant, CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category tag colored bullet
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(catColor)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = quest.category.uppercase(),
                        color = catColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "⚔ ${quest.xpReward} XP",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Quick Complete Checkmark Box
            IconButton(
                onClick = onComplete,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(CyberPrimary.copy(alpha = 0.1f))
                    .border(0.5.dp, CyberPrimary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Complete Objective",
                    tint = CyberPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
