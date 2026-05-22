package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.LevelUpViewModel
import com.example.ui.theme.*

@Composable
fun MarketplaceScreen(
    viewModel: LevelUpViewModel,
    modifier: Modifier = Modifier
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val marketItems by viewModel.marketItems.collectAsState()
    val purchaseSuccess by viewModel.purchaseSuccess.collectAsState()

    val categories = listOf("All", "Reward", "Skill", "Upgrade", "Internship")
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredItems = if (selectedCategory == "All") {
        marketItems
    } else {
        marketItems.filter { it.category.equals(selectedCategory, ignoreCase = true) }
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
                        text = "◆ SYSTEM TRADING GATEWAY ◆",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "MONARCH EXCHANGE",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Icon(Icons.Default.Storefront, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // User funds panel
            userProgress?.let { progress ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = CutCornerShape(8.dp),
                    border = BorderStroke(0.5.dp, CyberSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AVAILABLE TO SPEND:",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalActivity, contentDescription = null, tint = RankS, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${progress.totalCoins} COINS",
                                color = RankS,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
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

            // Shop Directory List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredItems) { item ->
                    MarketItemRowCard(
                        item = item,
                        userBalance = userProgress?.totalCoins ?: 0,
                        onBuy = { viewModel.buyMarketItem(item.id) }
                    )
                }
            }
        }

        // Purchase Success Dialog Modal
        purchaseSuccess?.let { success ->
            VoucherSuccessDialog(
                voucherDetails = success,
                onDismiss = { viewModel.clearPurchaseSuccess() }
            )
        }
    }
}

@Composable
fun MarketItemRowCard(
    item: MarketplaceItem,
    userBalance: Int,
    onBuy: () -> Unit
) {
    val canAfford = userBalance >= item.cost
    val catColor = when (item.category) {
        "Upgrade" -> RankC
        "Skill" -> CyberSecondary
        "Internship" -> RankA
        else -> RankS
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(8.dp))
            .background(CyberSurface)
            .border(0.5.dp, if (item.isPurchased) CyberSurfaceVariant else catColor.copy(alpha = 0.3f), CutCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(catColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.category.uppercase(),
                        color = catColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Cost or complete chip
                if (item.isPurchased) {
                    Text(
                        text = "ACQUIRED",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalMall, contentDescription = null, tint = RankS, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.cost} Coins",
                            color = RankS,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Item metadata details
            Text(
                text = item.name,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Button purchase trigger
            if (!item.isPurchased) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onBuy,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = catColor,
                        disabledContainerColor = CyberSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("purchase_item_button"),
                    shape = CutCornerShape(4.dp)
                ) {
                    Text(
                        text = if (canAfford) "CLAIM REWARD" else "INSUFFICIENT FUNDS (${item.cost - userBalance} COINS LACKING)",
                        color = if (canAfford) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Display claim registration details
                AnimatedVisibility(visible = item.unlockCode.isNotBlank()) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberBg)
                                .border(0.5.dp, CyberSurfaceVariant, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "CLAIM VOUCHER TOKEN: ${item.unlockCode}",
                                color = CyberPrimary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoucherSuccessDialog(
    voucherDetails: PurchaseResult.Success,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CyberSurface)
                .border(2.dp, RankS, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(RankS.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = RankS, modifier = Modifier.size(28.dp))
                }

                Text(
                    text = "ACQUISITION AUTHORIZED!",
                    color = RankS,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "Your leveling tokens have successfully been bartered for this premium item slot.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberBg)
                        .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = voucherDetails.itemName,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "VOUCHER: ${voucherDetails.code}",
                        color = CyberPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = RankS),
                    shape = CutCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().testTag("voucher_success_dismiss")
                ) {
                    Text("SECURE VOUCHER IN STORAGE", color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
