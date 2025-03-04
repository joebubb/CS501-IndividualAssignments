package com.jbubb.compassapp

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jbubb.compassapp.ui.theme.CompassAppTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager // declare sensor manager to be initialized later

    // declare sensors that will be used
    private var magnetometer: Sensor? = null
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    // vectors to store data for math
    private var magneticFieldVector by mutableStateOf(listOf(0f, 0f, 0f))
    private var accelerometerVector by mutableStateOf(listOf(0f, 0f, 0f))

    // for the level
    private var bubbleX by mutableFloatStateOf(0f)
    private var bubbleY by mutableFloatStateOf(0f)

    // switchable mode
    private var mode by mutableStateOf("level")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize the sensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // get the sensors
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        enableEdgeToEdge()
        setContent {
            CompassAppTheme {
                // render the view for the mode
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ModeToggleButton(mode) { mode = it }

                    if (mode == "compass") {
                        CompassApp(calculateAzimuth(accelerometerVector, magneticFieldVector))
                    } else {
                        LevelApp(bubbleX, bubbleY)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // start listening to the sensors
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this) // stop listening
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (event.sensor.type) {
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    // set magnetic vector
                    magneticFieldVector = listOf(it.values[0], it.values[1], it.values[2])
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    // set accelerometer vector variable
                    accelerometerVector = listOf(it.values[0], it.values[1], it.values[2])

                    // get the accelerometer values needed for the level
                    val x = it.values[0]
                    val y = it.values[1]

                    bubbleX = -x * 10f
                    bubbleY = y * 10f
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // not worrying about accuracy
    }

    private fun calculateAzimuth(accVector: List<Float>, magVector: List<Float>): Float {
        // convert to float lists
        val accelerometerArray = floatArrayOf(
            accVector[0],
            accVector[1],
            accVector[2]
        )
        val magnetometerArray = floatArrayOf(
            magVector[0],
            magVector[1],
            magVector[2]
        )

        // calculate rotation matrix
        val rotationMatrix = FloatArray(9)
        val success = SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerArray,
            magnetometerArray
        )

        // if successful calculate orientation
        return if (success) {
            val orientationAngles = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            // azimuth
            val azimuthInDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            (azimuthInDegrees + 360) % 360
        } else {
            // usually won't fail
            0f
        }
    }
}

@Composable
fun CompassApp(deg: Float) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.compass_needle),
            contentDescription = "compass needle",
            modifier = Modifier.rotate(-deg) // rotate the needle by the calculated azimuth
        )
    }
}

@Composable
fun LevelApp(xTilt: Float, yTilt: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .border(2.dp, Color.Black, CircleShape)
        ) {
            //cross hairs
            Box(
                modifier = Modifier
                    .size(width = 2.dp, height = 200.dp)
                    .background(Color.Black)
            )
            Box(
                modifier = Modifier
                    .size(width = 200.dp, height = 2.dp)
                    .background(Color.Black)
            )

            // limit the bubble movement
            val maxOffset = 80f
            val limitedX = xTilt.coerceIn(-maxOffset, maxOffset)
            val limitedY = yTilt.coerceIn(-maxOffset, maxOffset)

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = limitedX.dp, y = limitedY.dp)
                    .background(Color.Blue, CircleShape)
            )
        }
    }
}

@Composable
fun ModeToggleButton(mode: String, onModeChange: (String) -> Unit) {
    // switches modes
    Button(
        onClick = {
            onModeChange(if (mode == "compass") "level" else "compass")
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = if (mode == "compass") "Switch to Level" else "Switch to Compass")
    }
}
