package com.dhruvathaide.drnkcontrol.ui.lockdown

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhruvathaide.drnkcontrol.ui.challenges.GyroChallengeScreen
import com.dhruvathaide.drnkcontrol.ui.challenges.SemanticChallengeScreen
import com.dhruvathaide.drnkcontrol.ui.challenges.SimonSaysChallengeScreen
import com.dhruvathaide.drnkcontrol.ui.components.FluidButton
import com.dhruvathaide.drnkcontrol.ui.components.glassmorphism
import com.dhruvathaide.drnkcontrol.ui.theme.ElectricAmethyst
import com.dhruvathaide.drnkcontrol.ui.theme.MidnightNavy
import com.dhruvathaide.drnkcontrol.ui.theme.DRNKControlTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockOverlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableResource(android.R.color.transparent)

        val targetPackage = intent.getStringExtra("TARGET_PACKAGE") ?: "Unknown App"

        setContent {
            DRNKControlTheme {
                LockdownUI(
                    targetApp = targetPackage,
                    onUnlocked = { finish() },
                    onEmergencyClose = {
                        val startMain = android.content.Intent(android.content.Intent.ACTION_MAIN)
                        startMain.addCategory(android.content.Intent.CATEGORY_HOME)
                        startMain.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(startMain)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LockdownUI(targetApp: String, onUnlocked: () -> Unit, onEmergencyClose: () -> Unit) {
    var challengeStep by remember { mutableStateOf(0) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy.copy(alpha = 0.9f)) // Deep darkened background
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Red Pulsing Glow (Lockdown Warning)
        val infiniteTransition = rememberInfiniteTransition(label = "warning")
        val glowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.05f, targetValue = 0.15f,
            animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
            label = "glow"
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(Color.Red.copy(alpha = glowAlpha), Color.Transparent)))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AnimatedContent(
                targetState = challengeStep,
                transitionSpec = {
                    fadeIn(tween(500)) + scaleIn(tween(500)) togetherWith
                    fadeOut(tween(500)) + scaleOut(tween(500))
                },
                label = "challengeTransition"
            ) { step ->
                when (step) {
                    0 -> LockdownIntro(
                        appName = targetApp,
                        onStart = { challengeStep = 1 },
                        onBack = onEmergencyClose
                    )
                    1 -> GyroChallengeScreen(onSuccess = { challengeStep = 2 })
                    2 -> SimonSaysChallengeScreen(onSuccess = { challengeStep = 3 })
                    3 -> SemanticChallengeScreen(onSuccess = onUnlocked)
                }
            }
        }
    }
}

@Composable
private fun LockdownIntro(appName: String, onStart: () -> Unit, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(cornerRadius = 40.dp, alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .glassmorphism(cornerRadius = 100.dp, alpha = 0.2f),
                contentAlignment = Alignment.Center
            ) {
                Text("🧊", fontSize = 42.sp)
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "SESSIONS FROZEN",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    appName.uppercase(),
                    color = ElectricAmethyst,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
            
            Text(
                "Access is restricted under Safety Mode. Complete the sobriety sequence to unlock.",
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                lineHeight = 24.sp
            )
            
            FluidButton(
                text = "Begin Sequence",
                onClick = onStart
            )
            
            Text(
                "Exit Application",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { onBack() }
            )
        }
    }
}
