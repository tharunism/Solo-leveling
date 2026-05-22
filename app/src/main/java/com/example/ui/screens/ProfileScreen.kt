package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LevelUpViewModel
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: LevelUpViewModel,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val quests by viewModel.quests.collectAsState()

    var nickname by remember { mutableStateOf("") }
    var soundEnabled by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf("Cyberpunk") }
    
    // Immersive registration user data fields
    var mobileNumber by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var stateCity by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }
    var skillCategory by remember { mutableStateOf("") }

    val completedQuestsCount = quests.count { it.isCompleted }
    val totalQuestsCount = quests.size
    val scrollState = rememberScrollState()

    // Synchronize initial form configuration states
    LaunchedEffect(userProgress) {
        userProgress?.let {
            nickname = it.nickname
            soundEnabled = it.isSoundEnabled
            selectedTheme = it.customTheme
            mobileNumber = it.mobileNumber
            country = it.country
            stateCity = it.stateCity
            ageText = it.age.toString()
            interests = it.interests
            skillCategory = it.skillCategory
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .padding(16.dp)
            .safeDrawingPadding()
    ) {
        // Upper Title section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "◆ COGNITIVE ARCHIVE TERMINAL ◆",
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "HUNTER FILES",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        userProgress?.let { progress ->
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // Config Profile Form section
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(0.5.dp, CyberSurfaceVariant),
                    modifier = Modifier.fillMaxWidth(),
                    shape = CutCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "EDIT IDENTITY CHARACTER FILE",
                            color = CyberPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )

                        // Nickname edit input
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = { nickname = it },
                            label = { Text("Update Hero Nickname", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                focusedBorderColor = CyberPrimary,
                                unfocusedTextColor = TextSecondary,
                                unfocusedBorderColor = CyberSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("profile_nickname_field")
                        )

                        // Mobile number edit input
                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = { mobileNumber = it },
                            label = { Text("Verified Mobile Number", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                focusedBorderColor = CyberPrimary,
                                unfocusedTextColor = TextSecondary,
                                unfocusedBorderColor = CyberSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Country input
                            OutlinedTextField(
                                value = country,
                                onValueChange = { country = it },
                                label = { Text("Country", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    focusedBorderColor = CyberPrimary,
                                    unfocusedTextColor = TextSecondary,
                                    unfocusedBorderColor = CyberSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.2f)
                            )
                            // State/City input
                            OutlinedTextField(
                                value = stateCity,
                                onValueChange = { stateCity = it },
                                label = { Text("State / City", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    focusedBorderColor = CyberPrimary,
                                    unfocusedTextColor = TextSecondary,
                                    unfocusedBorderColor = CyberSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Age input
                            OutlinedTextField(
                                value = ageText,
                                onValueChange = { ageText = it },
                                label = { Text("Hunter Age", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    focusedBorderColor = CyberPrimary,
                                    unfocusedTextColor = TextSecondary,
                                    unfocusedBorderColor = CyberSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(0.7f)
                            )
                            // Skill category input
                            OutlinedTextField(
                                value = skillCategory,
                                onValueChange = { skillCategory = it },
                                label = { Text("Hunter Class Discipline", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    focusedBorderColor = CyberPrimary,
                                    unfocusedTextColor = TextSecondary,
                                    unfocusedBorderColor = CyberSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.3f)
                            )
                        }

                        // Interests input
                        OutlinedTextField(
                            value = interests,
                            onValueChange = { interests = it },
                            label = { Text("Interests (Comma Separated list)", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                focusedBorderColor = CyberPrimary,
                                unfocusedTextColor = TextSecondary,
                                unfocusedBorderColor = CyberSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Audio Settings toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("SYSTEM CHIME EFFECTS (AUDIO)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Play success levels dynamic haptics and chords.", color = TextSecondary, fontSize = 9.sp)
                            }

                            Switch(
                                checked = soundEnabled,
                                onCheckedChange = { soundEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberPrimary,
                                    checkedTrackColor = CyberPrimary.copy(alpha = 0.3f),
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = CyberBg
                                ),
                                modifier = Modifier.testTag("sound_vibration_toggle")
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Theme options selector
                        Text(
                            text = "PREMIUM SYSTEM VISUAL SKIN:",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Cyberpunk", "Shadow", "Neon").forEach { themeName ->
                                val active = selectedTheme == themeName
                                val activeColor = when (themeName) {
                                    "Shadow" -> CyberSecondary
                                    "Neon" -> Color(0xFFFF6D00)
                                    else -> CyberPrimary
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(CutCornerShape(4.dp))
                                        .background(if (active) activeColor.copy(alpha = 0.15f) else CyberBg)
                                        .border(
                                            1.dp,
                                            if (active) activeColor else CyberSurfaceVariant,
                                            CutCornerShape(4.dp)
                                        )
                                        .clickable { selectedTheme = themeName }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = themeName.uppercase(),
                                        color = if (active) activeColor else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Save update action
                        Button(
                            onClick = {
                                viewModel.editProfile(
                                    nickname = nickname,
                                    isSoundEnabled = soundEnabled,
                                    customTheme = selectedTheme,
                                    mobileNumber = mobileNumber,
                                    country = country,
                                    stateCity = stateCity,
                                    age = ageText.toIntOrNull() ?: 24,
                                    interests = interests,
                                    skillCategory = skillCategory
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                            shape = CutCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth().testTag("save_profile_button")
                        ) {
                            Text(
                                "SAVED PROFILE RE-AWAKEN",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Part 4: Anti-cheat / Trust progression card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, CyberSurfaceVariant),
                    shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🛡 SECURITY GATEWAY & MONARCH METADATA",
                            color = CyberPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("HUNTER REPUTATION STATUS", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                Text("${progress.reputationScore} / 100", color = Color(0xFF00E676), fontSize = 15.sp, fontWeight = FontWeight.Black)
                            }
                            Column {
                                Text("FRAUD / ANTI-CHEAT FLAG", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                Text("${progress.fraudScore} % (SUSPICION)", color = if (progress.fraudScore > 20) ErrorColorRed else TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                // Global administrative triggers
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "◆ CENTRAL PROTOCOLS COMMAND",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    Button(
                        onClick = onNavigateToAdmin,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary),
                        shape = CutCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("admin_panel_button")
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LAUNCH SYSADMIN CONTROL DECK",
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Historical Raid achievements logs
                Text(
                    text = "◆ SYSTEM MILESTONES & LEVEL HISTORY",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RaidStatBox(
                        value = "$completedQuestsCount / $totalQuestsCount",
                        label = "GATES CLEARED",
                        color = CyberPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    RaidStatBox(
                        value = "${progress.dailyStreak} DAYS",
                        label = "ACTIVE STREAK",
                        color = Color(0xFFFF6D00),
                        modifier = Modifier.weight(1f)
                    )
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
fun RaidStatBox(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberSurface)
            .border(0.5.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
