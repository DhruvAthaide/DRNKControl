package com.dhruvathaide.drnkcontrol.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dhruvathaide.drnkcontrol.ui.components.FluidButton
import com.dhruvathaide.drnkcontrol.ui.components.glassmorphism
import com.dhruvathaide.drnkcontrol.ui.theme.ElectricAmethyst
import com.dhruvathaide.drnkcontrol.ui.theme.MidnightNavy

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToChallenges: () -> Unit,
    onNavigateToReview: () -> Unit,
    onNavigateToAppSelector: () -> Unit
) {
    val isSafetyModeActive by viewModel.isSafetyModeActive.collectAsState()
    val isServiceEnabled by viewModel.isServiceEnabled.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MidnightNavy, Color(0xFF020408))
                )
            )
    ) {
        // Hero Background Glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-100).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricAmethyst.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App Brand
            Text(
                "DRNK CONTROL",
                color = ElectricAmethyst,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            // The Core Interaction Orb
            SafetyOrb(
                isActive = isSafetyModeActive,
                onToggle = { viewModel.toggleSafetyMode() }
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Status Card
            StatusCard(isServiceEnabled = isServiceEnabled)

            Spacer(modifier = Modifier.weight(1f))

            // Action Center
            ActionCenter(
                onNavigateToReview = onNavigateToReview,
                onNavigateToAppSelector = onNavigateToAppSelector,
                onNavigateToChallenges = onNavigateToChallenges
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SafetyOrb(isActive: Boolean, onToggle: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.1f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(220.dp)
            .scale(pulseScale)
            .shadow(
                elevation = if (isActive) 40.dp else 0.dp,
                shape = CircleShape,
                ambientColor = ElectricAmethyst,
                spotColor = ElectricAmethyst
            )
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = if (isActive) {
                        listOf(ElectricAmethyst, Color(0xFF9D50BB))
                    } else {
                        listOf(Color.White.copy(alpha = 0.05f), Color.White.copy(alpha = 0.02f))
                    }
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)
                ),
                shape = CircleShape
            )
            .clickable { onToggle() }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                if (isActive) "ACTIVE" else "DORMANT",
                color = if (isActive) Color.White else Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (isActive) "🛡️" else "💤",
                fontSize = 48.sp
            )
        }
    }
}

@Composable
fun StatusCard(isServiceEnabled: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(cornerRadius = 24.dp, alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isServiceEnabled) Color(0xFF2EB086).copy(alpha = 0.1f) 
                        else Color(0xFFF24C4C).copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isServiceEnabled) Color(0xFF2EB086) else Color(0xFFF24C4C),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    if (isServiceEnabled) "Sentinel Active" else "Sentinel Offline",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (isServiceEnabled) "Blocking is real-time" else "Permission required",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ActionCenter(
    onNavigateToReview: () -> Unit,
    onNavigateToAppSelector: () -> Unit,
    onNavigateToChallenges: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FluidButton(
            text = "Manage Protected Apps",
            onClick = onNavigateToAppSelector
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .glassmorphism(cornerRadius = 20.dp)
                    .clickable { onNavigateToReview() }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Buffered", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .glassmorphism(cornerRadius = 20.dp)
                    .clickable { onNavigateToChallenges() }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Challenges", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
