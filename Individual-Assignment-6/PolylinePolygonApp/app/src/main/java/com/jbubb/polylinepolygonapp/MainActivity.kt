package com.jbubb.polylinepolygonapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TrailMapScreen()
                }
            }
        }
    }
}

@Composable
fun TrailMapScreen() {
    val bostonCommonCenter = LatLng(42.3550, -71.0654)
    val hikingTrailStart = LatLng(42.449337, -71.092702)

    val cameraPositionState = rememberCameraPositionState()
    val scope = rememberCoroutineScope()

    var polylineColor by remember { mutableStateOf(Color.Blue) }
    var polylineWidth by remember { mutableStateOf(5f) }
    var polygonColor by remember { mutableStateOf(Color.Green.copy(alpha = 0.3f)) }
    var polygonStrokeColor by remember { mutableStateOf(Color.Green) }
    var selectedOverlay by remember { mutableStateOf<OverlayType?>(null) }

    val hikingTrail = listOf(
        LatLng(42.44933717488199, -71.09270191325467),
        LatLng(42.447988060911555, -71.0914765413768),
        LatLng(42.448013956768975, -71.09100276615507),
        LatLng(42.447476776054245, -71.0902961334829),
        LatLng(42.44913555596151, -71.08958879662868),
        LatLng(42.44791540081134, -71.08696925332464),
        LatLng(42.44809164427701, -71.08649312793321),
        LatLng(42.44774204973207, -71.0853876524158),
        LatLng(42.44783268554218, -71.0840891573636),
        LatLng(42.44778089366674, -71.0833872681462),
        LatLng(42.44680978807383, -71.08256254831574),
        LatLng(42.44683568441836, -71.08252745385488),
        LatLng(42.44646018637511, -71.08201858417225),
        LatLng(42.44576097712453, -71.0816500923331),
        LatLng(42.4445697137606, -71.08263273723747),
        LatLng(42.44454381647938, -71.08356274045055)
    )

    val bostonCommons = listOf(
        LatLng(42.3573849110177, -71.06334916610749),
        LatLng(42.35524000865051, -71.07196697568243),
        LatLng(42.35239586008706, -71.07068955071234),
        LatLng(42.35262581730656, -71.06741410207106),
        LatLng(42.35253207187499, -71.06475112738116),
        LatLng(42.35535076942302, -71.06338483969938),
        LatLng(42.35650064952925, -71.06222729041346),
        LatLng(42.3573849110177, -71.06334916610749)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.HYBRID)
        ) {
            Polyline(
                points = hikingTrail,
                color = polylineColor,
                width = polylineWidth,
                clickable = true,
                onClick = {
                    selectedOverlay = OverlayType.Polyline(
                        "Rock Circuit Trail",
                        "Don’t let this trail’s relative height (just around 300’ at its peak) and proximity to a major city fool you: this 4.7 mile circuit (4.2 mi. loop + 0.25 connector) is no easy walk in the park. In fact, this mostly-forested Rock Circuit Trail is purposely designed—or so it seems—to seek out and conquer every single steep, rocky crag in Middlesex Fells. Unlike its western cousin, don’t expect to find switchbacks or gentle sloping inclines in the eastern district. In some places, the white blazes marking the route simply lead directly up the jagged fells."
                    )
                }
            )

            Polygon(
                points = bostonCommons,
                fillColor = polygonColor,
                strokeColor = polygonStrokeColor,
                strokeWidth = 4f,
                clickable = true,
                onClick = {
                    selectedOverlay = OverlayType.Polygon(
                        "Boston Common",
                        "The Boston Common is a public park in downtown Boston, Massachusetts. It is the oldest city park in the United States. Boston Common consists of 50 acres (20 ha) of land bounded by five major Boston streets: Tremont Street, Park Street, Beacon Street, Charles Street, and Boylston Street. (from wikipedia site for Boston Common)"
                    )
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.3f)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(bostonCommonCenter, 15f)
                        )
                    }
                },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text("Boston Common")
            }

            Button(
                onClick = {
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(hikingTrailStart, 15f)
                        )
                    }
                },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text("Hiking Trail Start")
            }

            ColorSelector(
                title = "Trail Color",
                colors = listOf(Color.Red, Color.Blue, Color.Magenta),
                selectedColor = polylineColor,
                onColorSelected = { polylineColor = it }
            )

            Text("Trail Width", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = polylineWidth,
                onValueChange = { polylineWidth = it },
                valueRange = 2f..10f,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ColorSelector(
                title = "Park Color",
                colors = listOf(Color.Green, Color.Cyan, Color.Yellow),
                selectedColor = polygonStrokeColor,
                onColorSelected = {
                    polygonColor = it.copy(alpha = 0.3f)
                    polygonStrokeColor = it
                }
            )
        }

        selectedOverlay?.let { overlay ->
            AlertDialog(
                onDismissRequest = { selectedOverlay = null },
                title = { Text(overlay.title) },
                text = { Text(overlay.description) },
                confirmButton = {
                    Button(onClick = { selectedOverlay = null }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun ColorSelector(
    title: String,
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color)
                        .border(
                            width = 2.dp,
                            color = if (color == selectedColor) Color.White else Color.Transparent
                        )
                        .clickable { onColorSelected(color) }
                )
            }
        }
    }
}

sealed class OverlayType(val title: String, val description: String) {
    class Polyline(title: String, description: String) : OverlayType(title, description)
    class Polygon(title: String, description: String) : OverlayType(title, description)
}