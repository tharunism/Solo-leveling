package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Quest
import com.example.ui.LevelUpViewModel
import com.example.ui.theme.*

@Composable
fun QuestsScreen(
    viewModel: LevelUpViewModel,
    modifier: Modifier = Modifier
) {
    val quests by viewModel.quests.collectAsState()
    
    // Categories List
    val categories = listOf("All", "Fitness", "Coding", "Communication", "Business", "Spirituality", "Discipline", "Social Impact")
    var selectedCategory by remember { mutableStateOf("All") }

    // Dialog state controllers
    var isAddDlgOpen by remember { mutableStateOf(false) }
    var activeQuestForVerification by remember { mutableStateOf<Quest?>(null) }

    val filteredQuests = if (selectedCategory == "All") {
        quests
    } else {
        quests.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .safeDrawingPadding()
        ) {
            // Header Board Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "◆ SYSTEM OBJECTIVE BOARD ◆",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "ACTIVE RAIDS",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Add custom quest FAB/Button inside top bar
                Button(
                    onClick = { isAddDlgOpen = true },
                    shape = CutCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ADD QUEST", color = Color.Black, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontally Scrollable Category Filters
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(CutCornerShape(6.dp))
                            .background(if (isSelected) CyberPrimary.copy(alpha = 0.2f) else CyberSurface)
                            .border(
                                1.dp,
                                if (isSelected) CyberPrimary else CyberSurfaceVariant,
                                CutCornerShape(6.dp)
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = category.uppercase(),
                            color = if (isSelected) CyberPrimary else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredQuests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CompassCalibration, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active quests in this category.", color = TextSecondary, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredQuests) { quest ->
                        QuestCardItem(
                            quest = quest,
                            onAbandon = { viewModel.deleteQuest(quest.id) },
                            onTriggerVerification = { activeQuestForVerification = quest }
                        )
                    }
                }
            }
        }

        // Add Quest dialog definition
        if (isAddDlgOpen) {
            AddQuestDialog(
                onDismiss = { isAddDlgOpen = false },
                onAdd = { title, category, difficulty, deadline, verifyType ->
                    viewModel.addNewQuest(title, category, difficulty, deadline, verifyType)
                    isAddDlgOpen = false
                }
            )
        }

        // Interactive AI and GPS Quest referee system verification board dialog window
        activeQuestForVerification?.let { quest ->
            VerificationDialog(
                quest = quest,
                viewModel = viewModel,
                onDismiss = { activeQuestForVerification = null }
            )
        }
    }
}

@Composable
fun QuestCardItem(
    quest: Quest,
    onAbandon: () -> Unit,
    onTriggerVerification: () -> Unit
) {
    // Styling colors depending on difficulty
    val rankColor = when(quest.difficulty) {
        "S-Rank" -> RankS
        "A-Rank" -> RankA
        "B-Rank" -> RankB
        "C-Rank" -> RankC
        "D-Rank" -> RankD
        else -> RankE
    }

    val catColor = when(quest.category) {
        "Fitness" -> RankA
        "Coding" -> CyberPrimary
        "Discipline" -> RankD
        else -> CyberSecondary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp))
            .background(if (quest.isCompleted) CyberSurface.copy(alpha = 0.3f) else CyberSurface)
            .border(
                1.dp,
                if (quest.isCompleted) SolidColor(CyberSurfaceVariant) else Brush.linearGradient(listOf(rankColor, CyberSurfaceVariant)),
                CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(catColor)
                    )
                    Text(
                        text = quest.category.uppercase(),
                        color = catColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Difficulty Indicator Rank Tag
                Box(
                    modifier = Modifier
                        .clip(CutCornerShape(4.dp))
                        .background(rankColor.copy(alpha = 0.15f))
                        .border(1.dp, rankColor, CutCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = quest.difficulty,
                        color = rankColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quest Description/Title
            Text(
                text = quest.title,
                color = if (quest.isCompleted) TextSecondary else TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            Divider(color = CyberSurfaceVariant, thickness = 0.5.dp)

            Spacer(modifier = Modifier.height(10.dp))

            // Rewards and Parameters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // XP Reward log
                    Column {
                        Text("XP REWARD", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text("+${quest.xpReward}", color = CyberPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    // Coin Reward log
                    Column {
                        Text("COINS REWARD", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text("+${quest.coinReward}", color = RankS, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    // Method validation log
                    Column {
                        Text("VERIFICATION", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            val icon = when(quest.verificationType) {
                                "AI" -> Icons.Default.Psychology
                                "GPS" -> Icons.Default.LocationOn
                                else -> Icons.Default.Verified
                            }
                            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(11.dp))
                            Text(quest.verificationType, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // Action buttons complete/status check
                if (quest.isCompleted) {
                    Row(
                        modifier = Modifier
                            .clip(CutCornerShape(6.dp))
                            .background(Color(0xFF00E676).copy(alpha = 0.1f))
                            .border(1.dp, Color(0xFF00E676), CutCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CLEARED", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Abandon quest option
                        IconButton(
                            onClick = onAbandon,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CyberSurfaceVariant)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Erase", tint = ErrorColorRed, modifier = Modifier.size(14.dp))
                        }

                        // Complete / Trigger systems verification gate
                        Button(
                            onClick = onTriggerVerification,
                            shape = CutCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = rankColor)
                        ) {
                            Text(
                                "ARise ⚔",
                                color = if (quest.difficulty == "S-Rank") Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

val ErrorColorRed = Color(0xFFFF1744)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, category: String, difficulty: String, deadline: String, verificationType: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Fitness") }
    var selectedDifficulty by remember { mutableStateOf("E-Rank") }
    var selectedVerification by remember { mutableStateOf("Manual") }

    val categories = listOf("Fitness", "Coding", "Communication", "Business", "Spirituality", "Discipline", "Social Impact")
    val difficulties = listOf("E-Rank", "D-Rank", "C-Rank", "B-Rank", "A-Rank", "S-Rank")
    val verifications = listOf("Manual", "AI", "GPS")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(12.dp))
                .background(CyberSurface)
                .border(1.dp, CyberPrimary, CutCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "◆ COMMENCE SYSTEM RAID FORUM ◆",
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "CREATE CUSTOM QUEST",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Input Box
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What is your real-life obstacle? (e.g., 50 pushups, code feature X)", fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        focusedBorderColor = CyberPrimary,
                        unfocusedTextColor = TextSecondary,
                        unfocusedBorderColor = CyberSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_quest_title_input")
                )

                // Category select
                Column {
                    Text("CHOOSE DISCIPLINE CATEGORY:", color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(categories) { cat ->
                            val active = cat == selectedCategory
                            Box(
                                modifier = Modifier
                                    .clip(CutCornerShape(4.dp))
                                    .background(if (active) CyberPrimary.copy(alpha = 0.2f) else CyberBg)
                                    .border(0.5.dp, if (active) CyberPrimary else CyberSurfaceVariant, CutCornerShape(4.dp))
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(cat, color = if (active) CyberPrimary else TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }

                // Difficulty select
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("DIFFICULTY LIMIT:", color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberBg)
                                .border(0.5.dp, CyberSurfaceVariant, RoundedCornerShape(4.dp))
                                .padding(4.dp)
                        ) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                items(difficulties) { diff ->
                                    val active = diff == selectedDifficulty
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (active) RankB.copy(alpha = 0.2f) else Color.Transparent)
                                            .clickable { selectedDifficulty = diff }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(diff, color = if (active) RankB else TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Verification type select
                Column {
                    Text("INTELLIGENT REFEREE VALIDATION TYPE:", color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        verifications.forEach { type ->
                            val active = type == selectedVerification
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CutCornerShape(4.dp))
                                    .background(if (active) CyberPrimary.copy(alpha = 0.15f) else CyberBg)
                                    .border(1.dp, if (active) CyberPrimary else CyberSurfaceVariant, CutCornerShape(4.dp))
                                    .clickable { selectedVerification = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    type.uppercase() + if (type == "AI") " (SCAN)" else "",
                                    color = if (active) CyberPrimary else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // CTA Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = CutCornerShape(4.dp),
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, CyberSurfaceVariant)
                    ) {
                        Text("CANCEL", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = { onAdd(title, selectedCategory, selectedDifficulty, "Daily", selectedVerification) },
                        shape = CutCornerShape(4.dp),
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                    ) {
                        Text("AWAKEN SYSTEM QUEST", color = Color.Black, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun VerificationDialog(
    quest: Quest,
    viewModel: LevelUpViewModel,
    onDismiss: () -> Unit
) {
    var textProofSummary by remember { mutableStateOf("") }
    var locationLocked by remember { mutableStateOf(false) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var isPhotoUploaded by remember { mutableStateOf(false) }

    val isVerifying by viewModel.isAiVerifying.collectAsState()
    val aiResult by viewModel.aiVerificationResult.collectAsState()

    // Sound effect simulation toggle
    val userProfile by viewModel.userProgress.collectAsState()
    val playSound = userProfile?.isSoundEnabled ?: true

    Dialog(onDismissRequest = {
        viewModel.clearVerificationResult()
        onDismiss()
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CyberSurface)
                .border(2.dp, CyberSecondary, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "◆ SOVEREIGN REFEREE SYSTEM V3 ◆",
                    color = CyberSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "VALIDATING: \"${quest.title}\"",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Divider(color = CyberSurfaceVariant)

                // Render dynamic layout based on verification status
                if (aiResult != null) {
                    // Verification success/fail animation screen
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (aiResult!!.approved) Color(0xFF00E676).copy(alpha = 0.15f) else ErrorColorRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (aiResult!!.approved) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (aiResult!!.approved) Color(0xFF00E676) else ErrorColorRed,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = if (aiResult!!.approved) "AWAKENING VERIFIED!" else "SUBMISSION RETRACTED!",
                            color = if (aiResult!!.approved) Color(0xFF00E676) else ErrorColorRed,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = aiResult!!.feedback,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        if (aiResult!!.approved) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberPrimary.copy(alpha = 0.15f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "MONARCH SCORE: ${aiResult!!.score} / 100",
                                    color = CyberPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                viewModel.clearVerificationResult()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (aiResult!!.approved) Color(0xFF00E676) else ErrorColorRed),
                            shape = CutCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("DISMISS GATEWAY", color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                } else if (isVerifying) {
                    // Loading scanner visualizer
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 24.dp)
                    ) {
                        CircularProgressIndicator(color = CyberSecondary)
                        Text(
                            text = "SCANNING VECTORS FOR AUTHENTIC INTENT...",
                            color = CyberSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        LinearProgressIndicator(
                            color = CyberPrimary,
                            trackColor = CyberSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                        )
                    }
                } else {
                    // User Verification Input Screen
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. Photo Proof Upload Simulate Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberBg)
                                .border(1.dp, if (isPhotoUploaded) Color(0xFF00E676) else CyberSurfaceVariant, RoundedCornerShape(8.dp))
                                .clickable {
                                    isUploadingPhoto = true
                                    isPhotoUploaded = true
                                    isUploadingPhoto = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUploadingPhoto) {
                                CircularProgressIndicator(color = CyberPrimary, modifier = Modifier.size(18.dp))
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(
                                        imageVector = if (isPhotoUploaded) Icons.Default.CheckCircle else Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        tint = if (isPhotoUploaded) Color(0xFF00E676) else CyberPrimary
                                    )
                                    Text(
                                        text = if (isPhotoUploaded) "PROOF VECTOR ATTACHED (1 FILE)" else "ATTACH PROOF PHOTO OR VIDEO",
                                        color = if (isPhotoUploaded) Color(0xFF00E676) else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // 2. Mock GPS Verification indicator
                        if (quest.verificationType == "GPS") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (locationLocked) Color(0xFF020617).copy(alpha = 0.5f) else Color(0xFF1E1E2F))
                                    .border(0.5.dp, if (locationLocked) Color(0xFF00E676) else Color(0xFFFF6D00), RoundedCornerShape(8.dp))
                                    .clickable { locationLocked = true }
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.LocationSearching, contentDescription = null, tint = if (locationLocked) Color(0xFF00E676) else Color(0xFFFF6D00))
                                    Column {
                                        Text(
                                            text = if (locationLocked) "GPS METADATA LOCKED IN" else "REQUEST ABSOLUTE GPS GEOLOCK",
                                            color = if (locationLocked) Color(0xFF00E676) else TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = if (locationLocked) "Coordinates: 37.7749° N, 122.4194° W SECURE" else "Geodata required for S-Rank & B-Rank movement raids.",
                                            color = TextSecondary,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }

                        // 3. User Narrative Log Summary field
                        OutlinedTextField(
                            value = textProofSummary,
                            onValueChange = { textProofSummary = it },
                            label = { Text("What did you achieve? Write summary log...", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextSecondary,
                                focusedBorderColor = CyberSecondary,
                                unfocusedBorderColor = CyberSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("proof_summary_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Trigger action triggers
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = onDismiss,
                                shape = CutCornerShape(4.dp),
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, CyberSurfaceVariant)
                            ) {
                                Text("CANCEL", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }

                            Button(
                                onClick = {
                                    // Make real verification request using summary
                                    viewModel.triggerAIVerification(quest.id, textProofSummary)
                                },
                                shape = CutCornerShape(4.dp),
                                modifier = Modifier.weight(1.5f),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary)
                            ) {
                                Text("INITIATE SCAN ⚖", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
