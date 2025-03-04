package com.jbubb.soundmeter

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jbubb.soundmeter.ui.theme.SoundMeterTheme
import kotlin.math.abs
import kotlin.math.log10

class MainActivity : ComponentActivity() {

    private var isRecording by mutableStateOf(false)
    private var soundLevel by mutableStateOf(0f)
    private lateinit var audioRecord: AudioRecord
    private var bufferSize = 0
    private var threshold by mutableStateOf(85f) // default threshold

    // request permission
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            startRecording()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            SoundMeterTheme {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Sound Level: %.2f dB".format(soundLevel))

                        BarIndicator(level = soundLevel, threshold = threshold)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Threshold: ${threshold.toInt()} dB")

                        // slider for threshold selection
                        Slider(
                            value = threshold,
                            onValueChange = { threshold = it },
                            valueRange = 50f..120f,
                            steps = 7, // smooth adjustments
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            if (isRecording) {
                                stopRecording()
                            } else {
                                startRecording()
                            }
                            isRecording = !isRecording
                        }) {
                            Text(text = if (isRecording) "Stop Recording" else "Start Recording")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // show warning if sound level exceeds threshold
                        if (soundLevel > threshold) {
                            Text(
                                text = "WARNING: Loud Noise!",
                                color = Color.Red,
                                fontSize = MaterialTheme.typography.headlineLarge.fontSize
                            )
                        }
                    }
                }
            }
        }

    }

    private fun startRecording() {
        bufferSize = AudioRecord.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        audioRecord.startRecording()

        // background thread to read sound levels
        Thread {
            val audioData = ShortArray(bufferSize)
            while (isRecording) {
                val readResult = audioRecord.read(audioData, 0, bufferSize)
                if (readResult > 0) {
                    val amplitude = calculateAmplitude(audioData)
                    soundLevel = amplitudeToDb(amplitude)
                }
            }
            soundLevel = 0f // reset when stopped
        }.start()
    }

    private fun stopRecording() {
        audioRecord.stop()
        audioRecord.release()
    }

    private fun calculateAmplitude(audioData: ShortArray): Float {
        var sum = 0f
        for (i in audioData.indices) {
            sum += abs(audioData[i].toFloat())
        }
        return sum / audioData.size
    }

    private fun amplitudeToDb(amplitude: Float): Float {
        return (20 * log10(amplitude.toDouble())).toFloat()
    }
}

@Composable
fun BarIndicator(level: Float, threshold: Float) {
    val progress = (level / threshold).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(20.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Gray.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Green, Color.Yellow, Color.Red)
                    )
                )
        )
    }
}
