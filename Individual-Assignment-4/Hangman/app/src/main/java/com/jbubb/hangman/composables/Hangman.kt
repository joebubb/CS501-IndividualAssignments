package com.jbubb.hangman.composables

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbubb.hangman.R
import java.util.Locale
import kotlin.math.min

@Composable
fun HangmanApp() {
    val words = listOf(
        "APPLE", "BANANA", "CHERRY", "DRAGONFRUIT", "ELDERBERRY",
        "FIG", "GRAPE", "HONEYDEW", "KIWI", "LEMON",
        "MANGO", "NECTARINE", "ORANGE", "PAPAYA", "QUINCE",
        "RASPBERRY", "STRAWBERRY", "TANGERINE", "UGLI", "VANILLA",
        "WATERMELON", "XIGUA", "YAM", "ZUCCHINI", "APRICOT",
        "BLUEBERRY", "CANTALOUPE", "DATE", "GRAPEFRUIT", "PEAR"
    ) // define words
    var chosenWord by rememberSaveable { mutableStateOf(words.random()) } // start by choosing one random word
    var livesLost by rememberSaveable { mutableIntStateOf(0) } // start with lives at 0
    var guessed by rememberSaveable { mutableStateOf(setOf<Char>()) } // no guessed characters

    val guessChar = { c: Char ->
        guessed += setOf(c)
        if (c !in chosenWord) {
            livesLost += 1
        }
    }

    val newGame = {
        guessed = setOf()
        chosenWord = words.random()
        livesLost = 0
    }

    HangmanAppSmall(
        chosenWord = chosenWord,
        guessed = guessed,
        livesLost = 0,
        guessChar = guessChar
    )
}

@Composable
fun Letters(removed: Set<Char>, buttonWidth: Int, buttonHeight: Int, rowSize: Int, fontSize: Int, callback: (Char) -> Unit) {
    // can dynamically render the letters in different ways
    val letters = ('A'..'Z').toList()
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
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (removed.contains(c))
                                            Color.Gray.copy(alpha = 0.2f)
                                        else
                                            Color.Blue
                                    )
                                    .clickable {
                                        callback(c)
                                    },
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

@Composable
fun WordView(word: String, removed: Set<Char>) {
    val width = LocalConfiguration.current.screenWidthDp
    val columnWidth = ((width.toDouble() * 0.5)  / word.length).toInt()

    Row(modifier = Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.SpaceAround) {
        word.forEach { c ->
            Column(modifier = Modifier.width(columnWidth.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // the letter
                Text(
                    c.toString(),
                    color =
                    if (removed.contains(c))
                        Color.Blue// color it if it is guessed
                    else
                        Color.Transparent, // clear if not guessed yet
                    fontSize = 36.sp
                )
                Box(
                    modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color.Black)
                )
            }
        }
    }
}

@Composable
fun HangmanImage(livesLost: Int) {
    val image = when (livesLost) {
        0 -> R.drawable.hangman0
        1 -> R.drawable.hangman1
        2 -> R.drawable.hangman2
        3 -> R.drawable.hangman3
        4 -> R.drawable.hangman4
        5 -> R.drawable.hangman5
        6 -> R.drawable.hangman6
        else -> R.drawable.hangman5half
    }
    Image(
        painter = painterResource(image),
        contentDescription = "A picture of a hanging man"
    )
}

@Composable
fun HangmanAppSmall(chosenWord: String, guessed: Set<Char>, livesLost: Int, guessChar: (Char) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        HangmanImage(livesLost = livesLost)
        WordView(chosenWord, guessed)
        Text("Choose a Letter", fontSize = 40.sp)
        Letters(
            removed = guessed,
            buttonWidth = 70,
            buttonHeight = 60,
            rowSize = 4,
            fontSize = 40,
            callback = guessChar
        )
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun LetterMenuPreview() {
}