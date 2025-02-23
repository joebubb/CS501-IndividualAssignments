package com.jbubb.hangman.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

@Composable
fun Hangman() {

}

@Composable
fun Letters(letters: List<Char>, removed: Set<Char>, buttonWidth: Int, buttonHeight: Int, rowSize: Int, fontSize: Int) {
    // split the list up into a 2d list
    val rows = mutableListOf<List<Char>>()
    var currentIndex = 0
    while (currentIndex < letters.size) {
        rows.add(letters.slice(currentIndex..min(currentIndex + rowSize - 1, letters.size - 1)))
        currentIndex += rowSize
    }

        Column(horizontalAlignment = Alignment.CenterHorizontally) { // column rows of buttons
            rows.forEach { row ->
                Row { // one row of buttons
                    row.forEach { c ->
                        Row {
                            Box(
                                modifier = Modifier
                                    .width(buttonWidth.dp)
                                    .height(buttonHeight.dp) // style the box based on the input to the function
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Blue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(c.toString(), fontSize = fontSize.sp, color = Color.White)
                            }

                            if (c != row.last()) {
                                Spacer(Modifier.width(8.dp)) // add space on the sides of buttons
                            }
                        }
                    }
                }

                if (row != rows.last()) {
                    Spacer(Modifier.height(8.dp)) // add space between rows
                }
            }
        }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun LetterMenuPreview() {
    Letters(
        letters = ('A'..'Z').toList(),
        removed = setOf(),
        buttonWidth = 70,
        buttonHeight = 60,
        rowSize = 6,
        fontSize = 40
    )
}