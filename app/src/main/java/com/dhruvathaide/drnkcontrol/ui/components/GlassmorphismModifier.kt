package com.dhruvathaide.drnkcontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A refined glassmorphism modifier that applies clipping, a translucent background, 
 * and a subtle border for a modern "glass" look without blurring internal content.
 */
fun Modifier.glassmorphism(
    cornerRadius: Dp = 16.dp,
    alpha: Float = 0.1f,
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    borderWidth: Dp = 1.dp
): Modifier {
    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(Color.White.copy(alpha = alpha))
        .border(
            width = borderWidth,
            color = borderColor,
            shape = RoundedCornerShape(cornerRadius)
        )
}
