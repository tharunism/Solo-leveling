package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LevelUpViewModel
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AIMentorScreen(
    viewModel: LevelUpViewModel,
    modifier: Modifier = Modifier
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val quests by viewModel.quests.collectAsState()
    val aiAdvice by viewModel.aiMentorAdvice.collectAsState()
    val isMentorLoading by viewModel.isAiMentorLoading.collectAsState()

    val scrollState = rememberScrollState()

    // Calculate completed category counts for custom skills radar chart
    val completedQuests = quests.filter { it.isCompleted }
    val fitnessScore = completedQuests.count { it.category == "Fitness" } * 20 + 20
    val codingScore = completedQuests.count { it.category == "Coding" } * 20 + 30
    val disciplineScore = completedQuests.count { it.category == "Discipline" } * 20 + 25
    val spiritualityScore = completedQuests.count { it.category == "Spirituality" } * 20 + 35
    val businessScore = completedQuests.count { it.category == "Business" || it.category == "Communication" } * 20 + 20

    val scores = listOf(
        fitnessScore.coerceAtMost(100),
        codingScore.coerceAtMost(100),
        disciplineScore.coerceAtMost(100),
        spiritualityScore.coerceAtMost(100),
        businessScore.coerceAtMost(100)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .safeDrawingPadding()
    ) {
        // Log block Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "◆ CENTRAL COMMAND AWAKENING PANEL ◆",
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "AI SHADOW COMMAND",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Icon(Icons.Default.Bolt, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large Mentor Conversation Visual Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, CyberSecondary),
            shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Interactive Mentor Hologram Circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(CyberBg)
                        .border(1.dp, CyberPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = CyberSecondary,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Text(
                    text = "SOVEREIGN SHADOW CONJURER",
                    color = CyberSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                if (isMentorLoading) {
                    CircularProgressIndicator(color = CyberSecondary, modifier = Modifier.size(24.dp))
                    Text(
                        text = "Accessing System Core Core database...",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    Text(
                        text = "\"$aiAdvice\"",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Action Summon Buttons
                Button(
                    onClick = { viewModel.generateAIMission() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary),
                    shape = CutCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().testTag("summon_mission_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                        Text(
                            text = "SUMMON EXCLUSIVE DAILY RAID",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Custom drawn web style capabilities chart
        Text(
            text = "◆ COMPLETED RADAR METRIC",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CyberSurface.copy(alpha = 0.5f))
                .border(0.5.dp, CyberSurfaceVariant, RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            RadarChartDrawing(stats = scores)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart categories explanation list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberSurface)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("STATISTICS ANALYSIS KEYS:", color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("STR (Strength - Fitness Quests Complete)", color = RankA, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("$fitnessScore / 100", color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("INT (Intelligence - Coding Quests Complete)", color = CyberPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("$codingScore / 100", color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("DIS (Discipline - Standard Mindful Activities)", color = RankD, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("$disciplineScore / 100", color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}

@Composable
fun RadarChartDrawing(stats: List<Int>) {
    // 5 stats categories for radar spiderweb: STR, INT, DIS, SPI, LDR
    // Points are computed symmetrically centered
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
        val maxRadius = (size.height / 2f * 0.85f).coerceAtMost(160.dp.toPx())

        // Draw background web pentagram concentric rings
        val rings = 4
        for (i in 1..rings) {
            val radius = maxRadius * (i.toFloat() / rings.toFloat())
            val path = Path()
            for (j in 0 until 5) {
                val angle = (j * 2 * Math.PI / 5) - Math.PI / 2
                val targetX = center.x + radius * cos(angle).toFloat()
                val targetY = center.y + radius * sin(angle).toFloat()
                if (j == 0) path.moveTo(targetX, targetY) else path.lineTo(targetX, targetY)
            }
            path.close()
            drawPath(
                path = path,
                color = CyberSurfaceVariant.copy(alpha = 0.5f),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draw actual User score polygon
        val scorePath = Path()
        for (j in 0 until 5) {
            val scorePercent = stats[j] / 100f
            val radius = maxRadius * scorePercent
            val angle = (j * 2 * Math.PI / 5) - Math.PI / 2
            val targetX = center.x + radius * cos(angle).toFloat()
            val targetY = center.y + radius * sin(angle).toFloat()
            if (j == 0) scorePath.moveTo(targetX, targetY) else scorePath.lineTo(targetX, targetY)
        }
        scorePath.close()

        // Fill user score space with translucent Purple / Blue gradient brush
        drawPath(
            path = scorePath,
            color = CyberSecondary.copy(alpha = 0.25f)
        )
        drawPath(
            path = scorePath,
            color = CyberPrimary,
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Draw radar spider lines from center
        for (j in 0 until 5) {
            val angle = (j * 2 * Math.PI / 5) - Math.PI / 2
            val targetX = center.x + maxRadius * cos(angle).toFloat()
            val targetY = center.y + maxRadius * sin(angle).toFloat()
            drawLine(
                color = CyberSurfaceVariant.copy(alpha = 0.7f),
                start = center,
                end = androidx.compose.ui.geometry.Offset(targetX, targetY),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}
