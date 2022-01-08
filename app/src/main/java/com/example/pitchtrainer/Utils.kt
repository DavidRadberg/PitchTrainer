package com.example.pitchtrainer

import android.media.MediaPlayer
import kotlin.math.min
import kotlin.math.max


fun playNote(mp: MediaPlayer?, duration: Long = 0) {
    if (mp?.isPlaying == true) {
        mp?.stop()
        mp?.prepare()
    }
    mp?.start()
    Thread.sleep(duration)
}

fun getNote(n: Int): Int {
    if (n < 0 || n >= notes.size) {
        return 0
    }
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
    R.raw.c4
)

data class NotePair(var base: Int, var interval: Int) {
    fun getFirstNote(): Int {
        return getNote(base)
    }
    fun getSecondNote(): Int {
        return getNote(base + interval)
    }
}

fun generateInterval(size: Int, maxInterval: Int = 12) : NotePair {
    val interval : Int = (1..maxInterval).random()
    var max : Int = size - interval - 1
    val baseNote : Int = (0..max).random()
    return NotePair(baseNote, interval)
}

fun generatePhrase(size: Int, maxInterval: Int=12): List<Int> {
    val maxNote: Int = notes.size - 1
    val minNote: Int = 0
    val startNote: Int = (1..maxNote).random()

    var phrase: MutableList<Int> = mutableListOf(startNote)
    var lastNote: Int = startNote

    while (phrase.size < size) {
        val maxInPhrase = phrase.maxOrNull() ?: 0
        val minInPhrase = phrase.minOrNull() ?: 0

        val max = min(minInPhrase + maxInterval, maxNote)
        val min = max(minNote, maxInPhrase - maxInterval)
        var note: Int = (min..max).random()
        while (note == lastNote) {
            note = (min..max).random()
        }

        lastNote = note
        phrase.add(note)
    }

    return phrase
}

enum class app_states {
    BASELINE, WAITING_FOR_GUESS, CORRECT_GUESS, INCORRECT_GUESS
}

enum class guess_result {
    CORRECT, INCORRECT, OUT_OF_RANGE, SAME_NOTE
}