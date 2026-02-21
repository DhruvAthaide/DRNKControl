package com.dhruvathaide.drnkcontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dhruvathaide.drnkcontrol.ui.theme.FrostedGlass
import com.dhruvathaide.drnkcontrol.ui.theme.FrostedGlassBorder

fun Modifier.glassmorphism(
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = FrostedGlass,
    borderColor: Color = FrostedGlassBorder,
    borderWidth: Dp = 1.dp
): Modifier = composed {
    this.then(
        Modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    )
}
