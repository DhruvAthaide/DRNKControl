package com.dhruvathaide.drnkcontrol.ui.challenges

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhruvathaide.drnkcontrol.ui.theme.ElectricAmethyst
import com.dhruvathaide.drnkcontrol.ui.theme.MidnightNavy
import kotlinx.coroutines.delay

@Composable
fun SimonSaysChallengeScreen(
    onSuccess: () -> Unit
) {
    val sequenceLength = 6
    var sequence by remember { mutableStateOf<List<Int>>(emptyList()) }
    var userSequence by remember { mutableStateOf<List<Int>>(emptyList()) }
    var flashingIndex by remember { mutableStateOf<Int?>(null) }
    var isUserTurn by remember { mutableStateOf(false) }

    // Generate sequence once
    LaunchedEffect(Unit) {
        val newSequence = mutableListOf<Int>()
        for (i in 0 until sequenceLength) {
            newSequence.add((0..3).random())
        }
        sequence = newSequence
        
        // Play sequence
        delay(1000)
        for (index in sequence) {
            flashingIndex = index
            delay(500)
            flashingIndex = null
            delay(200)
        }
        isUserTurn = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Level 2: The Pattern",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = if (isUserTurn) "Your Turn! Replicate the pattern." else "Watch carefully...",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        // 2x2 Grid of buttons
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SimonButton(
                    id = 0,
                    isFlashing = flashingIndex == 0,
                    onClick = { handleUserInput(0, sequence, userSequence, { userSequence = it }, { isUserTurn = false; /* handle failure */ }, onSuccess) }
                )
                SimonButton(
                    id = 1,
                    isFlashing = flashingIndex == 1,
                    onClick = { handleUserInput(1, sequence, userSequence, { userSequence = it }, { isUserTurn = false; /* handle failure */ }, onSuccess) }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SimonButton(
                    id = 2,
                    isFlashing = flashingIndex == 2,
                    onClick = { handleUserInput(2, sequence, userSequence, { userSequence = it }, { isUserTurn = false; /* handle failure */ }, onSuccess) }
                )
                SimonButton(
                    id = 3,
                    isFlashing = flashingIndex == 3,
                    onClick = { handleUserInput(3, sequence, userSequence, { userSequence = it }, { isUserTurn = false; /* handle failure */ }, onSuccess) }
                )
            }
        }
    }
}

private fun handleUserInput(
    id: Int,
    sequence: List<Int>,
    currentInput: List<Int>,
    updateInput: (List<Int>) -> Unit,
    onFail: () -> Unit,
    onSuccess: () -> Unit
) {
    val newSeq = currentInput + id
    
    // Check if the current input is correct up to this point
    for (i in newSeq.indices) {
        if (newSeq[i] != sequence[i]) {
            onFail()
            return
        }
    }
    
    // If correct, update input
    updateInput(newSeq)
    
    // If completed the whole sequence
    if (newSeq.size == sequence.size) {
        onSuccess()
    }
}

@Composable
fun SimonButton(
    id: Int,
    isFlashing: Boolean,
    onClick: () -> Unit
) {
    // A base color map
    val baseColor = when (id) {
        0 -> Color(0xFFE53935) // Red
        1 -> Color(0xFF43A047) // Green
        2 -> Color(0xFF1E88E5) // Blue
        else -> Color(0xFFFFB300) // Yellow
    }

    val animatedColor by animateColorAsState(
        targetValue = if (isFlashing) Color.White else baseColor.copy(alpha = 0.5f),
        animationSpec = tween(150), label = "flashingColor"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(animatedColor)
            .clickable { onClick() }
    )
}
