package com.dhruvathaide.drnkcontrol.ui.challenges

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhruvathaide.drnkcontrol.ui.components.glassmorphism
import com.dhruvathaide.drnkcontrol.ui.theme.ElectricAmethyst
import com.dhruvathaide.drnkcontrol.ui.theme.MidnightNavy
import kotlinx.coroutines.delay

@Composable
fun GyroChallengeScreen(
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var roll by remember { mutableFloatStateOf(0f) }
    var pitch by remember { mutableFloatStateOf(0f) }
    var timeRemaining by remember { mutableIntStateOf(15) }
    var isAligned by remember { mutableStateOf(false) }

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    val magnetometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            val accelerometerReading = FloatArray(3)
            val magnetometerReading = FloatArray(3)

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
                }

                val rotationMatrix = FloatArray(9)
                val inclinationMatrix = FloatArray(9)
                if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerReading, magnetometerReading)) {
                    val orientationAngles = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                    roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not used
            }
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Check alignment
    LaunchedEffect(roll, pitch) {
        // A margin of error of ~5 degrees for "flat"
        isAligned = Math.abs(roll) < 5f && Math.abs(pitch) < 5f
    }

    // Timer logic
    LaunchedEffect(isAligned) {
        if (isAligned) {
            while (timeRemaining > 0 && isAligned) {
                delay(1000)
                timeRemaining--
            }
            if (timeRemaining == 0) {
                onSuccess()
            }
        } else {
            timeRemaining = 15 // Reset if they fail
        }
    }

    val animatedRoll by animateFloatAsState(targetValue = roll, animationSpec = tween(100), label = "roll")
    val animatedPitch by animateFloatAsState(targetValue = pitch, animationSpec = tween(100), label = "pitch")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Level 1: The Horizon",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Hold device flat for $timeRemaining seconds",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        Box(
            modifier = Modifier
                .size(300.dp)
                .glassmorphism(cornerRadius = 150.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                
                // Crosshairs
                drawLine(
                    color = Color.White.copy(alpha = 0.3f),
                    start = Offset(center.x, 0f),
                    end = Offset(center.x, size.height),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.3f),
                    start = Offset(0f, center.y),
                    end = Offset(size.width, center.y),
                    strokeWidth = 2f
                )
                
                // Inner Target Ring
                drawCircle(
                    color = ElectricAmethyst.copy(alpha = 0.5f),
                    radius = 40f,
                    style = Stroke(width = 4f)
                )

                // The Bubble (representing the current pitch/roll)
                // Normalize pitch/roll (roughly -90 to +90) mapped to the canvas size
                val maxOffset = size.width / 2 - 40f // padding
                val xOffset = (animatedRoll / 90f) * maxOffset
                val yOffset = (-animatedPitch / 90f) * maxOffset // Invert Y 
                
                drawCircle(
                    color = if (isAligned) Color.Green else Color.White,
                    radius = 30f,
                    center = Offset(center.x + xOffset, center.y + yOffset)
                )
            }
        }
    }
}
