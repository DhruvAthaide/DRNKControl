package com.dhruvathaide.drnkcontrol.ui.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhruvathaide.drnkcontrol.ui.components.FluidButton
import com.dhruvathaide.drnkcontrol.ui.theme.ElectricAmethyst
import com.dhruvathaide.drnkcontrol.ui.theme.MidnightNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemanticChallengeScreen(
    onSuccess: () -> Unit
) {
    val targetSentence = "The turquoise turtle toggles the temporal trigger"
    var userInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Level 3: The Semantic Check",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Type exactly with 0 typos:",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = targetSentence,
            color = ElectricAmethyst,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        OutlinedTextField(
            value = userInput,
            onValueChange = { 
                userInput = it
                isError = false
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricAmethyst,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                cursorColor = ElectricAmethyst,
                errorBorderColor = Color.Red,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            isError = isError,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            placeholder = { Text("Type here...", color = Color.Gray) }
        )

        if (isError) {
            Text(
                text = "Typo detected. Try again.",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        FluidButton(
            text = "Verify",
            onClick = {
                if (userInput == targetSentence) {
                    onSuccess()
                } else {
                    isError = true
                    userInput = "" // Clear on failure to make it harder
                }
            }
        )
    }
}
