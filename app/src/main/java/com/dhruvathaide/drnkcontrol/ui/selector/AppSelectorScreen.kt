package com.dhruvathaide.drnkcontrol.ui.selector

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dhruvathaide.drnkcontrol.ui.components.glassmorphism
import com.dhruvathaide.drnkcontrol.ui.theme.ElectricAmethyst
import com.dhruvathaide.drnkcontrol.ui.theme.MidnightNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectorScreen(
    viewModel: AppSelectorViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val apps by viewModel.filteredApps.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MidnightNavy,
                        Color(0xFF0A0E1A) // Deeper black-navy
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // Header Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .glassmorphism(cornerRadius = 12.dp)
                        .size(48.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "PROTECT",
                        color = ElectricAmethyst,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        "Applications",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphism(cornerRadius = 20.dp),
                placeholder = { Text("Search apps...", color = Color.White.copy(alpha = 0.3f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricAmethyst) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricAmethyst.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = ElectricAmethyst
                ),
                shape = RoundedCornerShape(20.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElectricAmethyst)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    itemsIndexed(apps) { index, app ->
                        var isVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            isVisible = true
                        }
                        
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(animationSpec = tween(500, delayMillis = index * 30)) +
                                    slideInVertically(animationSpec = tween(500, delayMillis = index * 30)) { it / 2 }
                        ) {
                            AppItem(
                                app = app,
                                onToggle = { viewModel.toggleAppBlock(app) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppItem(app: AppInfo, onToggle: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .glassmorphism(
                cornerRadius = 24.dp,
                alpha = if (app.isBlocked) 0.15f else 0.05f
            )
            .clickable { 
                isPressed = true
                onToggle()
                // Reset scale after small delay
            }
    ) {
        // Subtle glow if blocked
        if (app.isBlocked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(ElectricAmethyst.copy(alpha = 0.1f), Color.Transparent),
                            radius = 500f
                        )
                    )
            )
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Real App Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = app.icon,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    app.name,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    app.packageName,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            // Modern Toggle
            Box(
                modifier = Modifier
                    .width(52.dp)
                    .height(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (app.isBlocked) ElectricAmethyst else Color.White.copy(alpha = 0.1f)
                    )
                    .padding(4.dp)
            ) {
                val alignment = if (app.isBlocked) Alignment.CenterEnd else Alignment.CenterStart
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(alignment)
                )
            }
        }
    }
    
    if (isPressed) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}
