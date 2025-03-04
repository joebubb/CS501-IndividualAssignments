package com.jbubb.ballgame

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onSizeChanged

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager // sensor manager for init later
    private var accelerometer: Sensor? = null // accelerometer instead

    private var _x by mutableFloatStateOf(0f)
    private var _y by mutableFloatStateOf(0f)
    private var screenWidth by mutableFloatStateOf(0f) // mutable in case orientation change swithces it
    private var screenHeight by mutableFloatStateOf(0f)
    private var ballRadius by mutableFloatStateOf(0f)

    private val obstacleRect: Rect?
        get() {
            if (screenWidth == 0f || screenHeight == 0f) return null
            return Rect(
                left = screenWidth / 3,
                top = screenHeight / 3,
                right = screenWidth / 3 + 100f,
                bottom = screenHeight / 3 + 100f
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager // get sensor manager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) // get accelerometer

        enableEdgeToEdge()
        setContent {
            BallGameScreen(
                x = _x,
                y = _y,
                onScreenSizeAndBallRadiusChanged = { width, height, radius ->
                    screenWidth = width
                    screenHeight = height
                    ballRadius = radius
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        } // register
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this) // unregister
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val newX = _x - it.values[0] * 0.5f // move left or right
                val newY = _y + it.values[1] * 0.5f // move up or down

                if (!isColliding(newX, newY)) {
                    _x = newX
                    _y = newY
                }
            }
        }
    }

    private fun isColliding(x: Float, y: Float): Boolean {
        val obstacle = obstacleRect ?: return false
        val ballCenterX = screenWidth / 2 + x
        val ballCenterY = screenHeight / 2 + y

        val closestX = ballCenterX.coerceIn(obstacle.left, obstacle.right)
        val closestY = ballCenterY.coerceIn(obstacle.top, obstacle.bottom)
        val dx = ballCenterX - closestX
        val dy = ballCenterY - closestY
        val distanceSquared = dx * dx + dy * dy
        return distanceSquared < (ballRadius * ballRadius) // check if ball touches the obstacle
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun BallGameScreen(
    x: Float,
    y: Float,
    onScreenSizeAndBallRadiusChanged: (width: Float, height: Float, radius: Float) -> Unit
) {
    val ballRadiusPx = with(LocalDensity.current) { 20.dp.toPx() }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                onScreenSizeAndBallRadiusChanged(
                    size.width.toFloat(),
                    size.height.toFloat(),
                    ballRadiusPx
                )
            }
    ) {
        // keep ball inside screen bounds
        val boundedX = x.coerceIn(
            -(size.width / 2 - ballRadiusPx),
            (size.width / 2 - ballRadiusPx)
        )
        val boundedY = y.coerceIn(
            -(size.height / 2 - ballRadiusPx),
            (size.height / 2 - ballRadiusPx)
        )

        // draw ball
        drawCircle(
            color = Color.Blue,
            radius = ballRadiusPx,
            center = Offset(boundedX + size.width / 2, boundedY + size.height / 2)
        )

        // draw obstacle
        val obstacleRect = Rect(
            offset = Offset(size.width / 3, size.height / 3),
            size = androidx.compose.ui.geometry.Size(100f, 100f)
        )

        drawRect(
            color = Color.Red,
            topLeft = obstacleRect.topLeft,
            size = obstacleRect.size
        )
    }
}
