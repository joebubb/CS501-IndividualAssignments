package com.jbubb.hangman.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import kotlin.reflect.KProperty

class HangmanModel {
    private var word = ""
    private var guesses: Set<Char> = setOf()
    private var livesLost = 0
    private var hintsGiven = 0
    private val maxLives = 6
    private var displayHintMessage = false

    private val words = listOf(
        "APPLE", "BANANA", "CHERRY", "DRAGONFRUIT", "ELDERBERRY",
        "FIG", "GRAPE", "HONEYDEW", "KIWI", "LEMON",
        "MANGO", "NECTARINE", "ORANGE", "PAPAYA", "QUINCE",
        "RASPBERRY", "STRAWBERRY", "TANGERINE", "UGLI", "VANILLA",
        "WATERMELON", "XIGUA", "YAM", "ZUCCHINI", "APRICOT",
        "BLUEBERRY", "CANTALOUPE", "DATE", "GRAPEFRUIT", "PEAR"
    )

    fun hintMessage(): String {
        return when (word) {
            "APPLE" -> "Red"
            "BANANA" -> "Yellow"
            "CHERRY" -> "Pairs"
            "DRAGONFRUIT" -> "Exotic"
            "ELDERBERRY" -> "Berry"
            "FIG" -> "Sweet"
            "GRAPE" -> "Vine"
            "HONEYDEW" -> "Melon"
            "KIWI" -> "Fuzzy"
            "LEMON" -> "Sour"
            "MANGO" -> "Tropical"
            "NECTARINE" -> "Smooth"
            "ORANGE" -> "Citrus"
            "PAPAYA" -> "Nasty"
            "QUINCE" -> "Hard"
            "RASPBERRY" -> "Red"
            "STRAWBERRY" -> "Juicy"
            "TANGERINE" -> "Peel"
            "UGLI" -> "Jamaica"
            "VANILLA" -> "Ice Cream"
            "WATERMELON" -> "Green"
            "XIGUA" -> "China"
            "YAM" -> "Root"
            "ZUCCHINI" -> "Squash"
            "APRICOT" -> "Orange"
            "BLUEBERRY" -> "Blue"
            "CANTALOUPE" -> "Heavy"
            "DATE" -> "Dry"
            "GRAPEFRUIT" -> "Bitter"
            "PEAR" -> "Weird Shape"
            else -> "Unknown"
        }
    }

    private fun createNewModel(word: String, guesses: Set<Char>, livesLost: Int, hintsGiven: Int): HangmanModel {
        val result = HangmanModel()
        result.word = word
        result.guesses = guesses
        result.livesLost = livesLost
        result.hintsGiven = hintsGiven
        return result
    }

    fun guess(char: Char): HangmanModel {
        if (char !in word && char !in guesses) { // lose a life for a wrong guess
            livesLost += 1
        }
        guesses += setOf(char) // add to the set
        return createNewModel(word, guesses, livesLost, hintsGiven)
    }

    fun reset(): HangmanModel {
        val result = HangmanModel()
        result.word = words.random()
        return result
    }

    fun gameIsLost(): Boolean {
        return livesLost >= maxLives
    }

    fun getWord(): String {
        return word
    }

    fun getGuesses(): Set<Char> {
        return guesses
    }

    fun getLivesLost(): Int {
        return livesLost
    }

    fun canGiveHint(): Boolean {
        val hintCost = when (hintsGiven) {
            0 -> 0
            else -> 1
        }

        return hintCost + livesLost < maxLives
    }

    fun giveHint(): HangmanModel {
        val hintCost = when (hintsGiven) {
            0 -> 0
            else -> 1
        }

        // assume a hint can be given
        when (hintsGiven) {
            0 -> { // display the hint message
                val result = createNewModel(word, guesses, livesLost, 1)
                result.displayHintMessage = true
                return result
            }
            1 -> { // disable half of the letters not in the word
                var letters = ('A'..'Z').toList()
                // remove each character in the word from letters
                word.forEach { c ->
                    letters = letters.filter { c2 ->
                        c2 != c
                    }
                }

                // remove already guessed
                guesses.forEach { c ->
                    letters = letters.filter { c2 ->
                        c != c2
                    }
                }

                val total = letters.size
                val numToRemove = total / 2
                for (i in 0..<numToRemove) { // add guesses randomly
                    val char = letters.random()
                    guesses += setOf(char) // add guess
                    letters = letters.filter { c -> // remove
                        c != char
                    }
                }

                val result = createNewModel(word, guesses, livesLost+1, 2)
                result.displayHintMessage = true
                return result
            }
            2 -> { // disable all vowels
                val vowels = listOf('A', 'E', 'I', 'O', 'U')
                vowels.forEach {
                    guesses += setOf(it) // guess all vowels for the player
                }

                val result = createNewModel(word, guesses, livesLost+1, 3)
                result.displayHintMessage = true
                return result
            }
            else -> {
                return  createNewModel(word, guesses, livesLost, hintsGiven) // should never happen
            }
        }
    }
    operator fun getValue(thisRef: Any?, property: KProperty<*>): HangmanModel {
        return this
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: HangmanModel) {
        word = value.word
        guesses = value.guesses
        livesLost = value.livesLost
        hintsGiven = value.hintsGiven
        displayHintMessage = value.displayHintMessage
    }

    companion object {
        val Saver: Saver<MutableState<HangmanModel>, List<Any>> = Saver(
            save = { state ->
                val model = state.value
                listOf(
                    model.word,
                    model.guesses.toList(),
                    model.livesLost,
                    model.hintsGiven,
                    model.displayHintMessage
                )
            },
            restore = { data ->
                mutableStateOf(
                    HangmanModel().apply {
                        word = data[0] as String
                        guesses = (data[1] as List<Char>).toSet()
                        livesLost = data[2] as Int
                        hintsGiven = data[3] as Int
                        displayHintMessage = data[4] as Boolean
                    }
                )
            }
        )
    }
}