package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LevelUpViewModel
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    viewModel: LevelUpViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoginTab by remember { mutableStateOf(true) }
    var nickname by remember { mutableStateOf("Jin-Woo") }
    var email by remember { mutableStateOf("hunter@levelupnation.com") }
    var password by remember { mutableStateOf("arise123") }

    // Dialog state variables
    var isForgotPasswordOpen by remember { mutableStateOf(false) }
    var isOtpDialogOpen by remember { mutableStateOf(false) }

    // Avatar list
    val avatars = listOf(
        Pair("avatar_shadow_monarch", "Shadow Monarch"),
        Pair("avatar_cha", "S-Class Blade"),
        Pair("avatar_woo", "Prefect Commander"),
        Pair("avatar_thomas", "National Legend")
    )
    var selectedAvatar by remember { mutableStateOf(avatars[0].first) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
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
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "◆ COGNITIVE AWAKENING TERMINAL ◆",
                color = CyberPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "AWAKEN YOUR PROFILE",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Glowing Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(CutCornerShape(8.dp))
                    .background(CyberSurface)
                    .border(1.dp, CyberSurfaceVariant, CutCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CutCornerShape(6.dp))
                        .background(if (isLoginTab) CyberPrimary.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { isLoginTab = true }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LOG IN",
                        color = if (isLoginTab) CyberPrimary else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CutCornerShape(6.dp))
                        .background(if (!isLoginTab) CyberSecondary.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { isLoginTab = false }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CREATE ACCOUNT",
                        color = if (!isLoginTab) CyberSecondary else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Text Inputs
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Hunter Email ID", fontFamily = FontFamily.Monospace) },
                    leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = CyberPrimary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextSecondary,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberSurfaceVariant,
                        focusedLabelColor = CyberPrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedContainerColor = CyberSurface.copy(alpha = 0.6f),
                        unfocusedContainerColor = CyberSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("email_input"),
                    shape = CutCornerShape(8.dp)
                )

                // Nickname field (only displayed on signup)
                AnimatedVisibility(visible = !isLoginTab) {
                    Column {
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = { nickname = it },
                            label = { Text("Choose Hero Name / Nickname", fontFamily = FontFamily.Monospace) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyberSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextSecondary,
                                focusedBorderColor = CyberSecondary,
                                unfocusedBorderColor = CyberSurfaceVariant,
                                focusedLabelColor = CyberSecondary,
                                unfocusedLabelColor = TextSecondary,
                                focusedContainerColor = CyberSurface.copy(alpha = 0.6f),
                                unfocusedContainerColor = CyberSurface.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("nickname_input"),
                            shape = CutCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Avatar Picker
                        Text(
                            text = "SELECT HUNTER CLASS AVATAR:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            avatars.forEach { avatar ->
                                val isSelected = selectedAvatar == avatar.first
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CyberSecondary.copy(alpha = 0.2f) else CyberSurface)
                                        .border(
                                            1.dp,
                                            if (isSelected) CyberSecondary else CyberSurfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedAvatar = avatar.first }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        // Simple stylized SVG representation for avatars inside Compose
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.sweepGradient(
                                                        if (avatar.first == "avatar_shadow_monarch")
                                                            listOf(CyberSecondary, CyberPrimary, Color.Black)
                                                        else
                                                            listOf(CyberSecondary, CyberTertiary)
                                                    )
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = avatar.second,
                                            color = if (isSelected) TextPrimary else TextSecondary,
                                            fontSize = 9.sp,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                 // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Secured Password Token", fontFamily = FontFamily.Monospace) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyberPrimary) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextSecondary,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberSurfaceVariant,
                        focusedLabelColor = CyberPrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedContainerColor = CyberSurface.copy(alpha = 0.6f),
                        unfocusedContainerColor = CyberSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("password_input"),
                    shape = CutCornerShape(8.dp)
                )

                // Forgot Password link option
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text = "FORGOT GATE PASSWORD?",
                        color = CyberPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clickable { isForgotPasswordOpen = true }
                            .padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = {
                    viewModel.editProfile(nickname, true, if (isLoginTab) "Cyberpunk" else "Shadow")
                    onAuthSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(54.dp)
                    .clip(CutCornerShape(8.dp))
                    .testTag("submit_auth_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLoginTab) CyberPrimary else CyberSecondary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLoginTab) "AWAKEN ACCOUNT" else "INITIALIZE SOVEREIGN ENTRY",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Auth option
            OutlinedButton(
                onClick = {
                    viewModel.editProfile("G-Hunter", true, "Neon")
                    onAuthSuccess()
                },
                border = BorderStroke(1.dp, CyberSurfaceVariant),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
                    .clip(CutCornerShape(8.dp))
                    .testTag("google_login_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "G  ",
                        color = CyberPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "AUTHENTICATE WITH GOOGLE SECURE",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mobile SMS OTP Sign-In trigger option
            OutlinedButton(
                onClick = { isOtpDialogOpen = true },
                border = BorderStroke(1.dp, CyberSecondary),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
                    .clip(CutCornerShape(8.dp))
                    .testTag("otp_login_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneIphone,
                        contentDescription = null,
                        tint = CyberSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MOBILE SMS OTP SIGN IN",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Overlay dialogs for Forgot Password & OTP Codes simulation
        if (isForgotPasswordOpen) {
            ForgotPasswordDialog(
                onDismiss = { isForgotPasswordOpen = false },
                onSendResetLink = { targetEmail ->
                    // Simulate password encryption/hashing and dispatch of a link
                    viewModel.editProfile("Jin-Woo", true, "Cyberpunk")
                    isForgotPasswordOpen = false
                }
            )
        }

        if (isOtpDialogOpen) {
            MobileOtpDialog(
                onDismiss = { isOtpDialogOpen = false },
                onSuccess = { verifiedMobile ->
                    viewModel.editProfile("SMS-Hunter", true, "Shadow", mobileNumber = verifiedMobile)
                    isOtpDialogOpen = false
                    onAuthSuccess()
                }
            )
        }
    }
}

// --- High-Fidelity Cryptographic Forgot Password Dialog ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSendResetLink: (String) -> Unit
) {
    var resetEmail by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (resultMessage == null) {
                Button(
                    onClick = {
                        if (resetEmail.isBlank() || !resetEmail.contains("@")) {
                            resultMessage = "SYSTEM ERROR: Invalid security email format!"
                        } else {
                            resultMessage = "SUCCESS: Encrypted password keys dispatched to $resetEmail. Please check spam or system archives."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                ) {
                    Text("GENERATE LINK", color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                }
            } else {
                Button(
                    onClick = {
                        onSendResetLink(resetEmail)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                ) {
                    Text("CLOSE GATES", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (resultMessage == null) {
                TextButton(onClick = onDismiss) {
                    Text("CANCEL", color = TextSecondary, fontFamily = FontFamily.Monospace)
                }
            }
        },
        icon = {
            Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(36.dp))
        },
        title = {
            Text(
                "CRYPTOGRAPHIC GATE RESET",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (resultMessage == null) {
                    Text(
                        text = "Enter your registered email below to generate a secure SHA-256 OTP gateway authorization link.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Enter Email ID", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            focusedBorderColor = CyberPrimary,
                            unfocusedTextColor = TextSecondary,
                            unfocusedBorderColor = CyberSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = resultMessage!!,
                        fontSize = 12.sp,
                        color = if (resultMessage!!.startsWith("SUCCESS")) Color(0xFF00E676) else ErrorColorRed,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    )
                }
            }
        },
        containerColor = CyberSurface,
        shape = CutCornerShape(8.dp)
    )
}

// --- Secure SMS OTP Authentication Simulation ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileOtpDialog(
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit
) {
    var mobileNum by remember { mutableStateOf("+1 ") }
    var otpSent by remember { mutableStateOf(false) }
    var challengeCode by remember { mutableStateOf("") }
    var userInputCode by remember { mutableStateOf("") }
    var attemptsLeft by remember { mutableStateOf(3) }
    var errText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (!otpSent) {
                        if (mobileNum.length < 9) {
                            errText = "VALIDATION ERROR: Insufficient digits."
                        } else {
                            // Generate random 6 character code
                            challengeCode = (100000..999999).random().toString()
                            otpSent = true
                            errText = "SUCCESS: Code $challengeCode sent safely."
                        }
                    } else {
                        if (userInputCode == challengeCode) {
                            onSuccess(mobileNum)
                        } else {
                            attemptsLeft--
                            if (attemptsLeft <= 0) {
                                errText = "ACCESS DENIED: Brute force vector locked. Dialog closing."
                                onDismiss()
                            } else {
                                errText = "INVALID PIN: $attemptsLeft tries remaining!"
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (otpSent) CyberSecondary else CyberPrimary
                )
            ) {
                Text(
                    text = if (otpSent) "VERIFY OTP" else "DISPATCH OTP",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ABANDON RAID", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
        },
        icon = {
            Icon(
                imageVector = if (otpSent) Icons.Default.Sms else Icons.Default.PhoneIphone,
                contentDescription = null,
                tint = if (otpSent) CyberSecondary else CyberPrimary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = if (otpSent) "ENTER SMS DIGITS" else "AUTHENTICATE VIA OTP",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!otpSent) {
                    Text(
                        text = "Initialize multi-device credentials through high-security SMS. Carrier network rates may apply.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    OutlinedTextField(
                        value = mobileNum,
                        onValueChange = { mobileNum = it },
                        label = { Text("Enter Mobile Phone (with Country Code)", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            focusedBorderColor = CyberPrimary,
                            unfocusedTextColor = TextSecondary,
                            unfocusedBorderColor = CyberSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "We have dispatched a unique, encrypted pin directly to your terminal of choice ($mobileNum). Check your system notices.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    // Simulated SMS alert display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(0.5.dp, CyberSecondary, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "✉ [ALERT DISPATCH]: Your secure activation passcode token is: $challengeCode",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    OutlinedTextField(
                        value = userInputCode,
                        onValueChange = { userInputCode = it },
                        label = { Text("6-Digit OTP Code", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            focusedBorderColor = CyberSecondary,
                            unfocusedTextColor = TextSecondary,
                            unfocusedBorderColor = CyberSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                errText?.let {
                    Text(
                        text = it,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (it.startsWith("SUCCESS")) Color(0xFF00E676) else ErrorColorRed,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        containerColor = CyberSurface,
        shape = CutCornerShape(8.dp)
    )
}
