package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LandingScreen(
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Animation States for Glowing Grid Background
    val infiniteTransition = rememberInfiniteTransition(label = "grid")
    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    // Glowing Title Aura Value
    val titleGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleGlow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .drawBehind {
                // Drawing an aesthetic sci-fi technical diagonal background grid
                val width = size.width
                val height = size.height
                val gridStep = 80.dp.toPx()

                // Drawing lines
                var x = gridOffset
                while (x < width) {
                    drawLine(
                        color = CyberPrimary.copy(alpha = 0.05f),
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1.dp.toPx()
                    )
                    x += gridStep
                }
                var y = gridOffset
                while (y < height) {
                    drawLine(
                        color = CyberPrimary.copy(alpha = 0.05f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                    y += gridStep
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Game System Prompt Logo
            Box(
                modifier = Modifier
                    .clip(CutCornerShape(8.dp))
                    .background(Color(0xFFFFD600).copy(alpha = 0.1f))
                    .border(1.dp, Color(0xFFFFD600), CutCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "◆ SYSTEM AWAKENING ACTIVATED ◆",
                    color = Color(0xFFFFD600),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cyberpunk glowing title header
            Text(
                text = "LEVELUP NATION",
                color = CyberPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.sp,
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = CyberPrimary.copy(alpha = 0.15f * titleGlow),
                        radius = size.width / 1.5f,
                        center = center
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "TURN YOUR REAL LIFE INTO A LEVELING SYSTEM",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(30.dp))

            // RPG Status Display Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp))
                    .background(CyberSurface.copy(alpha = 0.85f))
                    .border(
                        BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                listOf(CyberPrimary, CyberSecondary)
                            )
                        ),
                        CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ALERT: RANK ASSESSMENT PENDING",
                        color = CyberTertiary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "A magical gateway has connected to your everyday actions. Complete real-world challenges to earn XP, unlock legendary stats, and climb from unawakened E-Rank to the National level Elite.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Giant Call-To-Action Button Stack
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary Start Quest CTA (RPG Cut Shape)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(56.dp)
                        .clip(CutCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CyberPrimary, CyberSecondary)
                            )
                        )
                        .clickable { onNavigateToAuth() }
                        .testTag("start_quest_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "START SYSTEM RAID (SIGN IN)",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Secondary Join Guild CTA
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(50.dp)
                        .clip(CutCornerShape(8.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberPrimary, CutCornerShape(8.dp))
                        .clickable { onNavigateToAuth() }
                        .testTag("join_guild_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = CyberPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ESTABLISH GUILD SQUAD",
                            color = CyberPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Tertiary Become Elite CTA
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(50.dp)
                        .clip(CutCornerShape(8.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberSecondary, CutCornerShape(8.dp))
                        .clickable { onNavigateToAuth() }
                        .testTag("become_elite_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = CyberSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SUMMON SHADOW COMMAND",
                            color = CyberSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Glowing Global RPG Counters Section
            Text(
                text = "◆ LIVE WORLD RAID LOG ◆",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface.copy(alpha = 0.5f))
                    .border(1.dp, CyberSurfaceVariant, CutCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCounterItem(
                    label = "ACTIVE HUNTERS WORLDWIDE",
                    value = "1,489,230",
                    color = CyberPrimary
                )
                Divider(color = CyberSurfaceVariant, thickness = 0.5.dp)
                StatCounterItem(
                    label = "COMPLETED OBSTACLES",
                    value = "12,402,192",
                    color = Color(0xFFFFD600)
                )
                Divider(color = CyberSurfaceVariant, thickness = 0.5.dp)
                StatCounterItem(
                    label = "XP MANIFESTED BY HUNTERS",
                    value = "2,459,203,190 SP",
                    color = CyberSecondary
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StatCounterItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.sp
        )
    }
}
