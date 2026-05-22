package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.data.*
import com.example.ui.LevelUpViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enforcing system edge-to-edge full screen drawing
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: LevelUpViewModel = viewModel()
                val navController = rememberNavController()

                // Profile and active state monitoring
                val userProgress by viewModel.userProgress.collectAsState()
                val levelUpEvent by viewModel.levelUpCelebration.collectAsState()
                val toastMsg by viewModel.toastMessage.collectAsState()

                // Basic alert message banner
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(toastMsg) {
                    toastMsg?.let {
                        snackbarHostState.showSnackbar(
                            message = it,
                            duration = SnackbarDuration.Short
                        )
                        viewModel.clearToastMessage()
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CyberBg)
                        .drawBehind {
                            // Top-left soft cyan blur glow
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(CyberPrimary.copy(alpha = 0.15f), Color.Transparent),
                                    center = Offset(size.width * 0.2f, size.height * 0.2f),
                                    radius = size.width * 0.6f
                                ),
                                radius = size.width * 0.6f,
                                center = Offset(size.width * 0.2f, size.height * 0.2f)
                            )
                            // Bottom-right soft purple blur glow
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(CyberSecondary.copy(alpha = 0.12f), Color.Transparent),
                                    center = Offset(size.width * 0.8f, size.height * 0.7f),
                                    radius = size.width * 0.7f
                                ),
                                radius = size.width * 0.7f,
                                center = Offset(size.width * 0.8f, size.height * 0.7f)
                            )
                        }
                ) {
                    // NavHost routing container
                    NavHost(
                        navController = navController,
                        startDestination = "landing",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Landing Onboarding Route
                        composable("landing") {
                            LandingScreen(
                                onNavigateToAuth = { navController.navigate("auth") }
                            )
                        }

                        // Authenticate Account Route
                        composable("auth") {
                            AuthScreen(
                                viewModel = viewModel,
                                onAuthSuccess = {
                                    navController.navigate("main") {
                                        popUpTo("landing") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Main Functional Frame (Dashboard, Quests, Guild, Mentor, Exchange, Leaderboards)
                        composable("main") {
                            MainHubScaffold(
                                viewModel = viewModel,
                                navController = navController
                            )
                        }

                        // Monarch System Admin Central Console Deck
                        composable("admin_dashboard") {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }

                    // Golden level up RPG screen overlay celebration
                    levelUpEvent?.let { celebration ->
                        SovereignLevelUpModal(
                            result = celebration,
                            onDismiss = { viewModel.clearLevelUpCelebration() }
                        )
                    }

                    // Theme responsive standard design system snackbar
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 100.dp)
                            .padding(horizontal = 16.dp)
                    ) { data ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CutCornerShape(8.dp))
                                .background(CyberSurface)
                                .border(1.dp, CyberPrimary, CutCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "◆ SYSTEM ALERT: " + data.visuals.message.uppercase(),
                                color = CyberPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Scaffold Bottom Bar Container ---

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainHubScaffold(
    viewModel: LevelUpViewModel,
    navController: NavHostController
) {
    // Current Active Bottom Menu state
    var activeModuleTab by remember { mutableStateOf("Dashboard") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CyberBottomNavigation(
                activeTab = activeModuleTab,
                onTabSelected = { activeModuleTab = it }
            )
        },
        containerColor = CyberBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeModuleTab) {
                "Dashboard" -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToQuests = { activeModuleTab = "Quests" },
                    onNavigateToGuilds = { activeModuleTab = "Guilds" },
                    onNavigateToMentor = { activeModuleTab = "Mentor" }
                )
                "Quests" -> QuestsScreen(
                    viewModel = viewModel
                )
                "Guilds" -> GuildScreen(
                    viewModel = viewModel
                )
                "Leaderboard" -> LeaderboardScreen()
                "Exchange" -> MarketplaceScreen(
                    viewModel = viewModel
                )
                "Mentor" -> AIMentorScreen(
                    viewModel = viewModel
                )
                "Profile" -> ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToAdmin = { navController.navigate("admin_dashboard") }
                )
            }
        }
    }
}

// --- Sleek Custom Cyberpunk Bottom Navigation Bar ---

@Composable
fun CyberBottomNavigation(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        NavigationItemData("Dashboard", Icons.Default.Dashboard, "home_tab"),
        NavigationItemData("Quests", Icons.Default.Terrain, "quests_tab"),
        NavigationItemData("Guilds", Icons.Default.Shield, "guild_tab"),
        NavigationItemData("Leaderboard", Icons.Default.EmojiEvents, "leaderboard_tab"),
        NavigationItemData("Exchange", Icons.Default.Storefront, "shop_tab"),
        NavigationItemData("Mentor", Icons.Default.Psychology, "mentor_tab"),
        NavigationItemData("Profile", Icons.Default.Person, "profile_tab")
    )

    // Using explicit navigation bar window padding as mandated by frontend instructions
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(68.dp)
            .background(DarkNavySurface)
            .border(1.dp, CyberSurfaceVariant.copy(alpha = 0.8f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = activeTab == item.label
                val activeColor = if (isSelected) CyberPrimary else TextSecondary
                val itemAnimScale by animateFloatAsState(if (isSelected) 1.15f else 1f, label = "activeAnim")

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clickable { onTabSelected(item.label) }
                        .testTag(item.tag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) CyberPrimary.copy(alpha = 0.1f) else Color.Transparent)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = activeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        color = activeColor,
                        fontSize = 8.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}

data class NavigationItemData(
    val label: String,
    val icon: ImageVector,
    val tag: String
)

// --- Full Screen RPG Level Up Celebration Modal Screen ---

@Composable
fun SovereignLevelUpModal(
    result: LevelUpResult.Success,
    onDismiss: () -> Unit
) {
    // Repeating sparkles visual transition values
    val infiniteTransition = rememberInfiniteTransition(label = "levelupSpark")
    val sparkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spark"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .clickable { onDismiss() }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Drawn glowing backgrounds
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = RankS.copy(alpha = 0.12f * sparkAlpha),
                    radius = size.width / 1.3f
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Warning/Status border
                Box(
                    modifier = Modifier
                        .clip(CutCornerShape(8.dp))
                        .background(RankS.copy(alpha = 0.15f))
                        .border(1.5.dp, RankS, CutCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "◆ AWAKENING LEVEL UP REGISTER ◆",
                        color = RankS,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Giant Animated Visual Level Up Indicator label
                Text(
                    text = "LEVEL UP",
                    color = RankS,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    fontFamily = FontFamily.SansSerif
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SYSTEM CONGRATULATES YOUR EVOLUTION",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Progression stats grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(CyberSurface.copy(alpha = 0.7f))
                        .border(0.5.dp, CyberSurfaceVariant, CutCornerShape(12.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LevelProgressDetailRow("HUNTER RE-AWAKENED LEVEL:", "Lvl ${result.newLevel - 1} ➔ Lvl ${result.newLevel}", CyberPrimary)
                    Divider(color = CyberSurfaceVariant, thickness = 0.5.dp)
                    LevelProgressDetailRow("SYSTEM AUTHORIZED RANK:", result.newRank.uppercase(), RankS)
                    Divider(color = CyberSurfaceVariant, thickness = 0.5.dp)
                    LevelProgressDetailRow("AWAKENING BONUS:", "+${result.coinsGained} COINS", Color(0xFF00E676))
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "Tap anywhere to return to the active Raid",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun LevelProgressDetailRow(
    label: String,
    value: String,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
