package com.example.pitchtrainer

import android.media.MediaPlayer


fun playNote(mp: MediaPlayer?) {
    if (mp?.isPlaying == true) {
        mp?.stop()
        mp?.prepare()
    }
    mp?.start()
}

fun getNote(n: Int): Int {
    return notes[n]
}

val notes: List<Int> = listOf(
    R.raw.c3,
    R.raw.db3,
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

enum class APP_STATES {
    WAITING_FOR_GUESS, CORRECT_GUESS, INCORRECT_GUESS
}