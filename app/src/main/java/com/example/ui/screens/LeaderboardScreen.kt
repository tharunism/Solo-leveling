package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Global") } // Global, Country, Collegiate

    // Mock data for hunters representation
    val globalHunters = listOf(
        HunterRankData("Sung Jin-Woo", "National Rank", "45,800 XP", "USA / Ahjin", true),
        HunterRankData("Thomas Andre", "National Rank", "38,200 XP", "USA / Scavenger", true),
        HunterRankData("Cha Hae-In", "S Rank", "29,400 XP", "KOR / White Tiger", false),
        HunterRankData("Christopher Reed", "S Rank", "27,200 XP", "USA / Fiend", false),
        HunterRankData("Woo Jin-Chul", "A Rank", "22,500 XP", "KOR / Monitoring", false),
        HunterRankData("Lennart Niermann", "A Rank", "19,800 XP", "GER / Richter", false),
        HunterRankData("You (Jin-Woo)", "E Rank", "1,250 XP", "KOR / Shadow", false)
    )

    val countryHunters = listOf(
        HunterRankData("Sung Jin-Woo", "National Rank", "45,800 XP", "USA / Ahjin", true),
        HunterRankData("Cha Hae-In", "S Rank", "29,400 XP", "KOR / White Tiger", false),
        HunterRankData("Woo Jin-Chul", "A Rank", "22,500 XP", "KOR / Monitoring", false)
    )

    val collegiateHunters = listOf(
        HunterRankData("Alex Sterner", "A Rank", "12,900 XP", "Stanford Univ", false),
        HunterRankData("Mina Takahashi", "B Rank", "11,200 XP", "Tokyo Tech", false),
        HunterRankData("Devon Patel", "B Rank", "9,800 XP", "MIT Guild", false)
    )

    val activeHuntersList = when (selectedTab) {
        "Country" -> countryHunters
        "Collegiate" -> collegiateHunters
        else -> globalHunters
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .padding(16.dp)
            .safeDrawingPadding()
    ) {
        // Title block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "◆ COGNITIVE LEADERBOARD ◆",
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "HUNTER ARENA",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Icon(Icons.Default.Leaderboard, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Segment selector tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(8.dp))
                .background(CyberSurface)
                .border(1.dp, CyberSurfaceVariant, CutCornerShape(8.dp))
                .padding(4.dp)
        ) {
            listOf("Global", "Country", "Collegiate").forEach { tab ->
                val selected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CutCornerShape(6.dp))
                        .background(if (selected) CyberPrimary.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { selectedTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        tab.uppercase(),
                        color = if (selected) CyberPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Highlighting top three podium cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // #2 place card
            if (activeHuntersList.size > 1) {
                Box(modifier = Modifier.weight(1f)) {
                    LeaderPodiumCard(rank = 2, hunter = activeHuntersList[1], color = RankA)
                }
            }
            // #1 place card
            if (activeHuntersList.isNotEmpty()) {
                Box(modifier = Modifier.weight(1.1f)) {
                    LeaderPodiumCard(rank = 1, hunter = activeHuntersList[0], color = RankS)
                }
            }
            // #3 place card
            if (activeHuntersList.size > 2) {
                Box(modifier = Modifier.weight(0.9f)) {
                    LeaderPodiumCard(rank = 3, hunter = activeHuntersList[2], color = RankC)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "◆ LEADERBOARD REGISTER",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // General rankings list details
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(activeHuntersList) { index, hunter ->
                HunterScoreRow(rankIndex = index + 1, hunter = hunter)
            }
        }
    }
}

@Composable
fun LeaderPodiumCard(
    rank: Int,
    hunter: HunterRankData,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurface)
            .border(2.dp, color, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "#$rank PLACE",
                color = color,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = hunter.name,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = hunter.xp,
                color = CyberPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HunterScoreRow(
    rankIndex: Int,
    hunter: HunterRankData
) {
    val rankBadgeColor = when (hunter.rankTitle) {
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
            .clip(CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
            .background(CyberSurface.copy(alpha = 0.6f))
            .border(0.5.dp, CyberSurfaceVariant, CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Rank position index
                Text(
                    text = "#$rankIndex",
                    color = CyberPrimary,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    Text(hunter.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(hunter.clan, color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                }
            }

            // XP and category details
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(rankBadgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(hunter.rankTitle.uppercase(), color = rankBadgeColor, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                Text(
                    text = hunter.xp,
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class HunterRankData(
    val name: String,
    val rankTitle: String,
    val xp: String,
    val clan: String,
    val isNationalLevel: Boolean
)
