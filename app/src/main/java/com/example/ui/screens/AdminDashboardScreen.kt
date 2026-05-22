package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.Quest
import com.example.ui.LevelUpViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: LevelUpViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quests by viewModel.quests.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()

    var activeAdminTab by remember { mutableStateOf("Analytics") } // Analytics, Moderator, Architecture
    
    // Simulating developer/admin configuration parameters
    var logs by remember { mutableStateOf(generateInitialAdminLogs()) }
    var activeUsersCount by remember { mutableStateOf(1054320) }
    var dausCount by remember { mutableStateOf(342890) }
    var totalQuestsProgress by remember { mutableStateOf(92350) }
    
    // Simulate periodic metric change
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(3000)
            activeUsersCount += (-10..15).random()
            dausCount += (-5..10).random()
            if ((1..5).random() == 1) {
                totalQuestsProgress += 1
                val newLog = generateRandomSecurityLog()
                logs = listOf(newLog) + logs.take(15)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "MONARCH CORE ENGINE V3",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = CyberPrimary
                        )
                        Text(
                            text = "ADMINISTRATIVE CONTROL DECK",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CyberPrimary)
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00E676).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF00E676), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ONLINE",
                            color = Color(0xFF00E676),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkNavySurface,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = CyberBg,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // High-fidelity tab header selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkNavySurface)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Analytics", "Moderator Review", "Dev Architecture").forEach { tabName ->
                    val active = activeAdminTab == tabName
                    val indicatorColor = if (active) CyberPrimary else Color.Transparent
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CutCornerShape(4.dp))
                            .background(if (active) CyberPrimary.copy(alpha = 0.1f) else Color.Transparent)
                            .border(1.dp, if (active) CyberPrimary else Color.Transparent, CutCornerShape(4.dp))
                            .clickable { activeAdminTab = tabName }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabName.uppercase(),
                            color = if (active) CyberPrimary else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Divider(color = CyberSurfaceVariant, thickness = 1.dp)

            // Real Content Switcher
            AnimatedContent(
                targetState = activeAdminTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "adminTabSwitch",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { targetTab ->
                when (targetTab) {
                    "Analytics" -> AdminAnalyticsView(
                        activeUsers = activeUsersCount,
                        daus = dausCount,
                        questsCount = totalQuestsProgress,
                        logs = logs,
                        onClearLogs = { logs = emptyList() }
                    )
                    "Moderator Review" -> AdminModeratorQueueView(
                        quests = quests,
                        viewModel = viewModel
                    )
                    "Dev Architecture" -> AdminDevArchitectureView()
                }
            }
        }
    }
}

// --- Tab 1: Analytics & Live Security Monitor ---
@Composable
fun AdminAnalyticsView(
    activeUsers: Int,
    daus: Int,
    questsCount: Int,
    logs: List<AdminSecurityLog>,
    onClearLogs: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Stat Deck
        Text(
            text = "◆ MASSIVE-SCALE METRIC COUNTERS (M3 VISUALIZERS) ◆",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = CyberPrimary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AdminMetricCard(
                label = "TOTAL HUNTERS REG",
                value = String.format("%,d", activeUsers),
                subText = "+3,124 Today",
                color = CyberPrimary,
                modifier = Modifier.weight(1f)
            )
            AdminMetricCard(
                label = "DAILY ACTIVE (DAU)",
                value = String.format("%,d", daus),
                subText = "32.5% Retention",
                color = CyberSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AdminMetricCard(
                label = "VERIFIED QUESTS OK",
                value = String.format("%,d", questsCount),
                subText = "99.8% AI Autopilot",
                color = Color(0xFF00E676),
                modifier = Modifier.weight(1f)
            )
            AdminMetricCard(
                label = "FRAUD DEFLECT PREVENTED",
                value = "1,842",
                subText = "0.01% Exception",
                color = ErrorColorRed,
                modifier = Modifier.weight(1f)
            )
        }

        // System Core Graphs simulation
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberSurfaceVariant),
            shape = CutCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REAL-TIME TRAFFIC & POSTGRES DB CONNECTIONS",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "LIVE: REDIS CACHING HIT 94%",
                        color = CyberPrimary,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                
                // Visual Sparkline simulation using Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(CyberBg)
                        .border(0.5.dp, CyberSurfaceVariant)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val points = listOf(20f, 45f, 32f, 75f, 60f, 95f, 80f, 120f, 90f, 140f, 110f, 160f, 130f, 185f, 160f, 220f)
                        val maxVal = 240f
                        val pathBrush = Brush.horizontalGradient(listOf(CyberPrimary, CyberSecondary))
                        
                        for (i in 0 until points.size - 1) {
                            val startX = (size.width / (points.size - 1)) * i
                            val startY = size.height - (points[i] / maxVal * size.height)
                            val endX = (size.width / (points.size - 1)) * (i + 1)
                            val endY = size.height - (points[i + 1] / maxVal * size.height)
                            
                            drawLine(
                                brush = pathBrush,
                                start = androidx.compose.ui.geometry.Offset(startX, startY),
                                end = androidx.compose.ui.geometry.Offset(endX, endY),
                                strokeWidth = 3.dp.toPx()
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("10s ago", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    Text("Server Load: 12.8% CPU | PostgreSQL Active Pool: 340/5000", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    Text("Now", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Live Anti-Spam & Fraud logs
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛡 SECURITY GATEWAY SYSTEM ACTIONS (LIVE)",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ErrorColorRed
                )
                Text(
                    text = "CLEAR AUDIT LOGS",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onClearLogs() }
                )
            }

            if (logs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Security systems clean. No fraud flagged.", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            } else {
                logs.forEach { log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberSurface)
                            .border(0.5.dp, if (log.fraudScore >= 80) ErrorColorRed.copy(alpha = 0.5f) else CyberSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (log.fraudScore >= 80) ErrorColorRed.copy(alpha = 0.15f) else Color(0xFFFFD600).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (log.fraudScore >= 80) Icons.Default.Warning else Icons.Default.Security,
                                contentDescription = null,
                                tint = if (log.fraudScore >= 80) ErrorColorRed else Color(0xFFFFD600),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = log.user,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = log.timestamp,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = log.message,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Threat Rate
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (log.fraudScore >= 80) ErrorColorRed.copy(alpha = 0.1f) else Color(0xFFFFD600).copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "SUSP: ${log.fraudScore}%",
                                fontSize = 8.sp,
                                color = if (log.fraudScore >= 80) ErrorColorRed else Color(0xFFFFD600),
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

@Composable
fun AdminMetricCard(
    label: String,
    value: String,
    subText: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(0.5.dp, CyberSurfaceVariant),
        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = label,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subText,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary
            )
        }
    }
}


// --- Tab 2: Moderator Review Queue ---
@Composable
fun AdminModeratorQueueView(
    quests: List<Quest>,
    viewModel: LevelUpViewModel
) {
    // Only fetch quests that are completed and verified is false (which acts as manual review queue submissions in our simulation logic)
    val queueQuests = quests.filter { !it.isCompleted } // Show some un-completed ones to simulate queue
    
    // Fallback simulated list to make the administrative panel extremely lively!
    val simulatedQueue = remember {
        mutableStateListOf(
            SimulatedSubmission(
                id = 101,
                hunterName = "Satoru_Go",
                questTitle = "Infinite Run: Log 10km Marathon Track",
                submittedTime = "2 mins ago",
                proofText = "Logged 10km under intense heat. Used fitness band metadata correctly.",
                coordinates = "35.6762° N, 139.6503° E (Tokyo)",
                confidenceScore = 96,
                rewardXp = 150,
                rewardCoins = 180,
                status = "Pending"
            ),
            SimulatedSubmission(
                id = 102,
                hunterName = "Lee_Min",
                questTitle = "A-Rank Algorithmic Gate: Write Lexical Parser",
                submittedTime = "8 mins ago",
                proofText = "Implemented compiler parsing logic with standard dynamic state tables in Rust. Included automated scripts.",
                coordinates = "37.5665° N, 126.9780° E (Seoul)",
                confidenceScore = 88,
                rewardXp = 100,
                rewardCoins = 120,
                status = "Pending"
            ),
            SimulatedSubmission(
                id = 103,
                hunterName = "GamerGod",
                questTitle = "Discipline Lock: 2 Hour Deep Work Session",
                submittedTime = "15 mins ago",
                proofText = "Finished reading financial scalability ledger. Turned off notifications entirely.",
                coordinates = "40.7128° N, 74.0060° W (New York)",
                confidenceScore = 42, // Low confidence / Suspicious
                rewardXp = 75,
                rewardCoins = 100,
                status = "Pending"
            )
        )
    }

    if (simulatedQueue.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("QUEUES EMPTY!", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(4.dp))
                Text("All hunter proof submissions cleared with 100% precision.", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "◆ QUEUED PROOF SUBMISSIONS FOR MODERATOR REVIEW ◆",
                        fontSize = 10.sp,
                        color = CyberPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${simulatedQueue.size} DISPATCHES",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            items(simulatedQueue) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(1.dp, if (item.confidenceScore < 50) ErrorColorRed.copy(alpha = 0.5f) else CyberSurfaceVariant),
                    shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Card Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(CyberBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(12.dp))
                                }
                                Text(
                                    text = item.hunterName.uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            
                            Text(
                                text = item.submittedTime,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quest Details
                        Text(
                            text = "OBJECTIVE: ${item.questTitle}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CyberPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // User Written Proof Summary
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberBg)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "\"${item.proofText}\"",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Proof Parameters & Metadata
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                labelValue("GPS POSITION LOCK", item.coordinates)
                                Spacer(modifier = Modifier.height(2.dp))
                                labelValue("REWARDS", "+${item.rewardXp} XP | +${item.rewardCoins} Coins")
                            }

                            // Confidence score circular rating
                            val confColor = when {
                                item.confidenceScore >= 90 -> Color(0xFF00E676)
                                item.confidenceScore >= 60 -> Color(0xFFFFD600)
                                else -> ErrorColorRed
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(confColor.copy(alpha = 0.1f))
                                    .border(1.dp, confColor, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${item.confidenceScore}%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = confColor,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "AI TRUST",
                                        fontSize = 7.sp,
                                        color = confColor,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = CyberSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Moderator reviews actions approve/reject
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    simulatedQueue.remove(item)
                                },
                                shape = CutCornerShape(4.dp),
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, ErrorColorRed.copy(alpha = 0.6f))
                            ) {
                                Text("REJECT PROOF (BAN)", color = ErrorColorRed, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    simulatedQueue.remove(item)
                                },
                                shape = CutCornerShape(4.dp),
                                modifier = Modifier.weight(1.3f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                            ) {
                                Text("APPROVE & REWARD ⚔", color = Color.Black, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun labelValue(label: String, value: String) {
    Row {
        Text(text = "$label: ", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = TextSecondary, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = TextPrimary)
    }
}

// --- Tab 3: Developer specs, schemas and cloud setup ---
@Composable
fun AdminDevArchitectureView() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "◆ MASSIVE CLOUD SCALABILITY CONFIGS & ARCHITECTURE ◆",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CyberPrimary
        )

        ArchitectureDetailBox(
            title = "1. ADVANCED DATABASE SCHEMA (SUPABASE / POSTGRES)",
            code = """-- Database schema for 10 million+ concurrent users
-- Organized with indexing over primary keys and shard-friendly attributes

-- 1. Users Progression File Table
CREATE TABLE users_progress (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mobile_number VARCHAR(20),
    avatar_url VARCHAR(255),
    country VARCHAR(50),
    state_city VARCHAR(50),
    age INT CHECK (age >= 13),
    interests TEXT,
    skill_category VARCHAR(100),
    rank_level VARCHAR(20) DEFAULT 'E-Rank',
    current_level INT DEFAULT 1,
    current_xp INT DEFAULT 0,
    coins_balance INT DEFAULT 500,
    reputation_score INT DEFAULT 100,
    fraud_suspicion_rate INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_user_username ON users_progress(username);
CREATE INDEX idx_user_email ON users_progress(email);
CREATE INDEX idx_user_rank ON users_progress(rank_level);

-- 2. Quests Definitions Table
CREATE TABLE quests (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    xp_reward INT NOT NULL,
    coin_reward INT NOT NULL,
    verification_type VARCHAR(20) DEFAULT 'Manual'
);
CREATE INDEX idx_quest_difficulty ON quests(difficulty);

-- 3. Proof Verification Records (Audited)
CREATE TABLE quest_submissions (
    id BIGSERIAL PRIMARY KEY,
    user_id INT REFERENCES users_progress(id) ON DELETE CASCADE,
    quest_id INT REFERENCES quests(id) ON DELETE CASCADE,
    timestamp TIMESTAMP default NOW(),
    proof_image_url VARCHAR(255),
    gps_coordinates POINT,
    ai_validation_confidence INTCheck(ai_validation_confidence BETWEEN 0 AND 100),
    referee_remarks TEXT,
    moderator_status VARCHAR(20) DEFAULT 'Pending' -- Approved, Rejected, Pending
);
CREATE INDEX idx_submissions_status ON quest_submissions(moderator_status);
CREATE INDEX idx_submissions_user ON quest_submissions(user_id);"""
        )

        ArchitectureDetailBox(
            title = "2. BACKEND API SPECIFICATIONS (NODE.JS + EXPRESS.JS)",
            code = """// Secure Express.js middleware and controller routes (Rate-Limited, encrypted JWT tokens)
const express = require('express');
const jwt = require('jsonwebtoken');
const rateLimit = require('express-rate-limit');
const bcrypt = require('bcryptjs');

const app = express();
app.use(express.json());

// 1. Anti-DOS and Spam Rate Limiter
const strictLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 Min window
    max: 100, // limit to 100 requests per IP
    message: { error: "Sovereign firewall blocked request. Spammer rate triggered." }
});

// 2. JWT Verification Guard Middleware
const verifyHunterToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    if (!authHeader) return res.status(401).json({ error: "Access Denied: Missing Gateway credentials." });
    
    const token = authHeader.split(' ')[1];
    jwt.verify(token, process.env.JWT_SECRET_KEY, (err, hunter) => {
        if (err) return res.status(403).json({ error: "Token signature invalidated. Gateway closed." });
        req.hunter = hunter;
        next();
    });
};

// 3. SECURE AUTHENTICATION ENDPOINTS
app.post('/api/auth/register', strictLimiter, async (req, res) => {
    try {
        const { username, email, password, mobile, country, city, age, interests, skills } = req.body;
        
        // Anti-Cheat & Password Hashing
        const salt = await bcrypt.genSalt(12);
        const hashedPassword = await bcrypt.hash(password, salt);
        
        const newHunter = await db.query(
            "INSERT INTO users_progress (username, email, mobile_number, hashed_password, country, state_city, age, interests, skill_category) VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9) RETURNING id, username",
            [username, email, mobile, hashedPassword, country, city, age, interests, skills]
        );
        
        res.status(201).json({ success: true, message: "Cognitive awakening registered.", id: newHunter.rows[0].id });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.post('/api/auth/login', strictLimiter, async (req, res) => {
    // Authenticate credentials, generate multi-device secure token...
    const token = jwt.sign({ hunterId: hunter.id, rank: hunter.rank_level }, process.env.JWT_SECRET_KEY, { expiresIn: '7d' });
    res.json({ token, profile: hunter });
});

// 4. QUEST PROOF AI SUBMISSION FLOW
app.post('/api/quests/submit', verifyHunterToken, async (req, res) => {
    const { questId, proofSummary, coordinates, imageUrl } = req.body;
    
    // AI Verification engine simulator pipeline triggered...
    const verificationConfidence = evaluateWithAI(questId, proofSummary);
    const approved = verificationConfidence >= 70;
    
    // Auto-award if approved securely
    await db.query("INSERT INTO quest_submissions (user_id, quest_id, proof_image_url, ai_validation_confidence, moderator_status) VALUES ($1, $2, $3, $4, $5)",
         [req.hunter.hunterId, questId, imageUrl, verificationConfidence, approved ? 'Approved' : 'Pending']);
         
    res.json({ approved, score: verificationConfidence, message: approved ? "Rewards injected successfully" : "Held in queue for Moderator Review" });
});"""
        )

        ArchitectureDetailBox(
            title = "3. HARDENED CLOUD ARCHITECTURE & FRAUD DEFECTION",
            code = """✦ CLOUD ARCHITECTURE OVERVIEW:
- Edge Network: Cloudflare (DDoS mitigation, CDN for static files, Web Application Firewall)
- Compute: Kubernetes Auto-Scaling Node Groups (Express.js cluster sizing dynamically based on queue sizes)
- Caching: Redis Enterprise Cluster (Caching user level progress, feed items, leaderboard positions. High frequency read buffer)
- Database: Amazon Aurora PostgreSQL / Supabase, hosted on multiple availability zones (Read-replicas for immediate leaderboard sync)
- Storage: Amazon S3 with Cloudinary CDN integration for high-speed media upload and rendering
- Real-time Sync: WebSockets (Express gateway instances running Socket.io with Redis message broker adapters)

✦ SUSPICIOUS BEHAVIOR & FRAUD CRITERIA:
- Verification Speed: Detect quests submitted within too short intervals (< 5 mins per task). Increases fraud rate immediately.
- Spoof Tracking: GPS locked coordinates compared with timestamps. Speed > 250 km/h triggers immediate block.
- Anti-Plagiarism: Text summary vectors matching previously approved submissions are processed for similarity rating resulting in automated ban."
"""
        )
    }
}

@Composable
fun ArchitectureDetailBox(title: String, code: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberSurfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                color = CyberPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .border(0.5.dp, CyberSurfaceVariant)
                    .padding(8.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = Color(0xFFD4D4D4),
                    lineHeight = 13.sp
                )
            }
        }
    }
}

// --- Data Helpers for simulation/logging deck ---
data class AdminSecurityLog(
    val user: String,
    val message: String,
    val timestamp: String,
    val fraudScore: Int
)

data class SimulatedSubmission(
    val id: Int,
    val hunterName: String,
    val questTitle: String,
    val submittedTime: String,
    val proofText: String,
    val coordinates: String,
    val confidenceScore: Int,
    val rewardXp: Int,
    val rewardCoins: Int,
    var status: String
)

fun generateInitialAdminLogs() = listOf(
    AdminSecurityLog("NinjaSlayer", "GPS Location coordinate jumping too fast (320 km/h in 2 mins). Triggered anti-spoof alarm.", "05:14:12", 85),
    AdminSecurityLog("SoloRaid99", "Submitted 3 quests inside 45 seconds. Auto-ratelimiter delayed processing.", "05:12:05", 72),
    AdminSecurityLog("Hunter_Jin", "Text proof summary mirrors historical logs perfectly. Similarity vector 0.98. Quarantined.", "05:08:44", 94),
    AdminSecurityLog("ShadowLord", "Normal Login Authentication authenticated securely via Google Login API.", "05:04:10", 0),
    AdminSecurityLog("WooCommander", "Changed settings. Added mobile verification number +82 (2) 119-9021.", "05:01:22", 1)
)

fun generateRandomSecurityLog(): AdminSecurityLog {
    val users = listOf("ShadowMonarch", "Hunter_H", "SpeedRunner", "ApexLeveler", "ChaBlade")
    val user = users.random()
    val threats = listOf(
        Pair("GPS Coordinates spoofing candidate. Track record in disagreement with standard velocity bounds.", 90),
        Pair("Token authorization renewal. Signature verified.", 0),
        Pair("Multiple concurrent login tokens triggered across Seoul/San Francisco.", 65),
        Pair("Submission proof photo contains copyright watermark metadata. Blocked reward issuance.", 80),
        Pair("Normal email validation sequence authenticated successfully. Multi-device synced.", 2)
    ).random()
    
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return AdminSecurityLog(user, threats.first, sdf.format(Date()), threats.second)
}
