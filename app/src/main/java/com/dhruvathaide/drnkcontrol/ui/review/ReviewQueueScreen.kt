package com.dhruvathaide.drnkcontrol.ui.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dhruvathaide.drnkcontrol.data.GhostMessageEntity
import com.dhruvathaide.drnkcontrol.ui.components.FluidButton
import com.dhruvathaide.drnkcontrol.ui.components.glassmorphism
import com.dhruvathaide.drnkcontrol.ui.theme.ElectricAmethyst
import com.dhruvathaide.drnkcontrol.ui.theme.MidnightNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReviewQueueScreen(
    viewModel: ReviewViewModel = hiltViewModel(),
    onEmergencyWipe: () -> Unit
) {
    val messages by viewModel.pendingMessages.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ghost Queue",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = onEmergencyWipe,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
            ) {
                Text("BURN ALL", color = Color.White, fontWeight = FontWeight.Black)
            }
        }

        if (messages.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No pending messages. You are safe.", color = Color.Gray, fontSize = 18.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(messages, key = { it.id }) { message ->
                    GhostMessageCard(
                        message = message,
                        onSend = { viewModel.sendMessage(message) },
                        onDelete = { viewModel.deleteMessage(message.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun GhostMessageCard(
    message: GhostMessageEntity,
    onSend: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeString = dateFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(cornerRadius = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message.appSource,
                color = ElectricAmethyst,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = timeString,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message.messageContent,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onSend,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricAmethyst)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send Now", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Now")
            }
        }
    }
}
