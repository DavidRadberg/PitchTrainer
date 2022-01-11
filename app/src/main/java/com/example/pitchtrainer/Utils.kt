package com.example.pitchtrainer

import android.media.MediaPlayer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.math.min
import kotlin.math.max

val diatonic: List<Int> = listOf(0, 2, 4, 5, 7, 9, 11)
val pentatonic: List<Int> = listOf(0, 2, 4, 7, 9)
val major_chord: List<Int> = listOf(0, 4, 7)

const val ARG_DIFFICULTY = "difficulty"
const val octave: Int = 12

fun inScale(note: Int, key: Int, scale: List<Int>): Boolean {
    return scale.contains((note - key) % octave)
}

data class Settings(
    var scale: List<Int> = diatonic,
    var startNote: Int? = null,
    var key: Int? = null,
    var canDecline: Boolean = true,
    var phraseSize: Int = 2
)

fun getSettings(difficulty: Int): Settings {
    val settings = Settings()
    when (difficulty) {
        1 -> {
            settings.scale = major_chord
        }
        2 -> {
            settings.scale = pentatonic
        }
        else -> {
            settings.scale = diatonic
        }
    }

    if (difficulty < 4) {
        settings.canDecline = false
    }

    if (difficulty < 5) {
        settings.startNote = 12
    }

    if (difficulty < 6) {
        settings.key = 0
    }

    if (difficulty > 6) {
        settings.phraseSize = difficulty - 4
    }

    return settings
}

fun playNote(mp: MediaPlayer?) = GlobalScope.async {
    if (mp?.isPlaying == true) {
        mp?.stop()
        mp?.prepare()
    }
    mp?.start()
}

val suffixes: List<String> = listOf("th", "st", "nd", "rd", "th")


fun getGuessString(idx: Int): String {
    val suffix: String = suffixes[min(idx, 3)]
    return "Guess the $idx$suffix note"
}

fun getNote(n: Int): Int {
    return notes[n]
}

val notes: List<Int> = listOf(
    R.raw.c3,
    R.raw.db3,
    R.raw.d3,
    R.raw.eb3,
    R.raw.e3,
    R.raw.f3,
    R.raw.gb3,
    R.raw.g3,
    R.raw.ab3,
    R.raw.a3,
    R.raw.bb3,
    R.raw.b3,
    R.raw.c4,
    R.raw.db4,
    R.raw.d4,
    R.raw.eb4,
    R.raw.e4,
    R.raw.f4,
    R.raw.gb4,
    R.raw.g4,
    R.raw.ab4,
    R.raw.a4,
    R.raw.bb4,
    R.raw.b4,
    R.raw.c5,
    R.raw.db5,
    R.raw.d5,
    R.raw.eb5,
    R.raw.e5,
    R.raw.f5,
    R.raw.gb5,
    R.raw.g5,
    R.raw.ab5,
    R.raw.a5,
)

fun generatePhrase(settings: Settings, maxInterval: Int = octave): List<Int> {
    val scale = settings.scale

    val size: Int = settings.phraseSize
    val maxNote: Int = notes.size - 1
    val minNote: Int = 0

    val key: Int = settings.key ?: (0 until octave).random()
    var startNote: Int = settings.startNote ?: (0..maxNote).random()

    var noteOk: Boolean = inScale(startNote, key, scale)
    while (!noteOk) {
        startNote = (0..maxNote).random()
        noteOk = inScale(startNote, key, scale)
    }

    var phrase: MutableList<Int> = mutableListOf(startNote)
    var lastNote: Int = startNote

    while (phrase.size < size) {
        val maxInPhrase = phrase.maxOrNull() ?: 0
        val minInPhrase = phrase.minOrNull() ?: 0

        val max = min(minInPhrase + maxInterval, maxNote)
        var min = max(minNote, maxInPhrase - maxInterval)

        if (!settings.canDecline) {
            min = startNote
        }

        var note: Int = (min..max).random()
        noteOk = inScale(note, key, scale) && (note != lastNote)
        while (!noteOk) {
            note = (min..max).random()
            noteOk = inScale(note, key, scale) && (note != lastNote)
        }

        lastNote = note
        phrase.add(note)
    }

    return phrase
}

enum class AppStates {
    BASELINE, WAITING_FOR_GUESS, CORRECT_GUESS, INCORRECT_GUESS
}

enum class GuessResult {
    CORRECT, INCORRECT, OUT_OF_RANGE, SAME_NOTE
}