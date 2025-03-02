package com.jbubb.altitudeapp

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbubb.altitudeapp.ui.theme.AltitudeAppTheme
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager // declare SensorManager
    private var pressureSensor: Sensor? = null // initialize pressure sensor to null

    private var millibars by mutableFloatStateOf(0f)
    private var accuracy by mutableStateOf("Unknown")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager // initialize sensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) // get pressure sensor

        enableEdgeToEdge()
        setContent {
            AltitudeAppTheme {
                AltitudeApp(millibarsToMeters(millibars))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pressureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) // register the listener when the app starts
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this) // unregister listener if the app pauses
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            millibars = it.values[0] // set the millibars to the new sensor reading
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        sensor?.let {
            this.accuracy = when (accuracy) {
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "High"
                SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Low"
                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Medium"
                SensorManager.SENSOR_STATUS_UNRELIABLE -> "Unreliable"
                else -> "Unknown"
            }
        }
    }

    private fun millibarsToMeters(p: Float): Double {
        val p0 = 1013.25
        return 44330 * (1 - (p / p0).pow(1.toDouble() / 5.255))
    }
}

@Composable
fun AltitudeApp(altitude: Double) {
    // support feet and meters
    var unit by remember { mutableStateOf("meters") }
    val stringToShow = when (unit) {
        "meters" -> "${altitude.roundToInt()} m"
        "feet" -> "${(altitude * 3.28).roundToInt()} ft"
        else -> "$altitude ???"
    }

    // shift colors towards black as the user approaches space
    val r = 70 * (1 - (min(1.0, percentTravelledToSpace(altitude))))
    val g = 195 * (1 - (min(1.0, percentTravelledToSpace(altitude))))
    val b = 255 * (1 - (min(1.0, percentTravelledToSpace(altitude))))


    // main box with dynamic background
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(r.toInt(), g.toInt(), b.toInt())
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringToShow, fontSize = 40.sp, color = Color.White)
            Spacer(modifier = Modifier.height(50.dp))
            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                listOf("meters", "feet").forEach { // unit buttons
                    Box(
                        modifier = Modifier
                            .height(50.dp)
                            .width(100.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable {
                                unit = it
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(it, color = Color.White)
                    }
                }
            }
        }
    }
}

fun percentTravelledToSpace(altitude: Double): Double {
    val karmanLineAltitude = 100_000
    val groundAltitude = 0
    return max(groundAltitude.toDouble(), altitude) / karmanLineAltitude.toDouble()
}
