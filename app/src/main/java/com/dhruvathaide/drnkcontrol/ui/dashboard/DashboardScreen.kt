package com.dhruvathaide.drnkcontrol.ui.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dhruvathaide.drnkcontrol.ui.components.glassmorphism
import com.dhruvathaide.drnkcontrol.ui.theme.ElectricAmethyst
import com.dhruvathaide.drnkcontrol.ui.theme.MidnightNavy

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val isSafetyModeActive by viewModel.isSafetyModeActive.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        Text(
            text = "DRNK Control",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        GlowingOrbToggle(
            isActive = isSafetyModeActive,
            onClick = { viewModel.toggleSafetyMode() }
        )

        Spacer(modifier = Modifier.height(64.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphism(cornerRadius = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Status",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isSafetyModeActive) "PROTECTED" else "VULNERABLE",
                    color = if (isSafetyModeActive) ElectricAmethyst else Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
            }
        }
    }
}

@Composable
fun GlowingOrbToggle(
    isActive: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "orb_pulse_alpha"
    )

    val orbColor = if (isActive) ElectricAmethyst else Color.DarkGray
    val glowColor = if (isActive) ElectricAmethyst.copy(alpha = pulseAlpha) else Color.Transparent

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(240.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // Outer Glow Layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor, Color.Transparent),
                    radius = size.minDimension / 2
                )
            )
            
            // Core Orb
            drawCircle(
                color = orbColor,
                radius = size.minDimension / 3
            )
            
            // Subtle Ring
            drawCircle(
                color = if (isActive) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                radius = size.minDimension / 2.8f,
                style = Stroke(width = 4f)
            )
        }
        
        Text(
            text = if (isActive) "LOCKED" else "TAP TO LOCK",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = if (isActive) 24.sp else 16.sp,
            letterSpacing = 2.sp
        )
    }
}
