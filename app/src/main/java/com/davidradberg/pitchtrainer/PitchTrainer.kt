package com.davidradberg.pitchtrainer

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import com.davidradberg.pitchtrainer.databinding.PitchTrainerBinding
import kotlinx.coroutines.*
import kotlin.collections.ArrayDeque

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PitchTrainer : Fragment() {

    private var difficulty: Int = 0
    private var _binding: PitchTrainerBinding? = null
    private var phrase: List<Int> = emptyList()
    private var players: MutableList<MediaPlayer> = mutableListOf()
    private var phraseSize: Int = 2
    private val waitTimeMs: Long = 800
    private var state: AppStates = AppStates.BASELINE
    private var nCorrectGuesses: Int = 0
    private var phraseJob: Job? = null
    private var guessJobs: ArrayDeque<Job> = ArrayDeque()
    private var guessPlayers: ArrayDeque<MediaPlayer?> = ArrayDeque()
    private val maxGuessJobs: Int = 10
    private var settings: Settings = Settings()
    private val whiteKeys: List<Int> =
        listOf(0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21, 23, 24, 26, 28, 29, 31, 33)
    private val blackKeys: List<Int> =
        listOf(1, 3, 6, 8, 10, 13, 15, 18, 20, 22, 25, 27, 30, 32)
    private var streak: Int = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = PitchTrainerBinding.inflate(inflater, container, false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        difficulty = arguments?.getInt(ARG_DIFFICULTY) ?: 0
        settings = getSettings(difficulty)
        phraseSize = settings.phraseSize
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateNotes()
        binding.buttonPlayPhrase.setOnClickListener {
            if (state == AppStates.BASELINE) {
                toGuessState()
            }
            playAllNotes()
        }
        binding.buttonNewPhrase.setOnClickListener {
            generateNotes()
            binding.buttonPlayPhrase.callOnClick()
        }
        binding.textViewDifficulty.text = "Difficulty: $difficulty"
        binding.textViewStreak.text = "Streak: $streak"
    }

    private fun generateNotes() {
        releasePlayers()
        cancelGuess()
        phrase = generatePhrase(settings)

        for (note in phrase) {
            players.add(MediaPlayer.create(activity, getNote(note)))
        }
        if (state == AppStates.WAITING_FOR_GUESS) {
            streak = 0
            binding.textViewStreak.text = "Streak: $streak"
        }

        toBaselineState()
    }

    private fun playAllNotes() {
        cancelPhrase()
        phraseJob = GlobalScope.launch {
            for (player in players) {
                val playing = playNote(player)
                playing.await()
                delay(waitTimeMs)
            }
        }
    }

    private fun cancelPhrase() {
        if (phraseJob?.isActive == true) {
            phraseJob?.cancel()
        }
    }

    private fun cancelGuess() {
        while (guessPlayers.size > 0) {
            val player = guessPlayers.removeFirstOrNull()
            player?.stop()
            player?.release()
        }

        while (guessJobs.size > 0) {
            val job = guessJobs.removeFirstOrNull()
            job?.cancel()
        }
    }

    private fun toBaselineState() {
        state = AppStates.BASELINE

        for (i in notes.indices) {
            val button: android.widget.Button? = getButton(i)
            when (i) {
                in whiteKeys -> button?.setBackgroundColor(resources.getColor(R.color.White))
                in blackKeys -> button?.setBackgroundColor(resources.getColor(R.color.Black))
            }
        }
    }

    private fun toGuessState() {
        binding.buttonPlayPhrase.text = "Replay Phrase"
        state = AppStates.WAITING_FOR_GUESS
        nCorrectGuesses = 0
        setResultText(getGuessString(nCorrectGuesses + 2))

        for (i in notes.indices) {
            getButton(i)?.setOnClickListener() {
                if (state == AppStates.WAITING_FOR_GUESS) {
                     if (takeGuess(i) != GuessResult.CORRECT) {
                        playSingleNote(i)
                    }
                } else {
                    playSingleNote(i)
                }
            }
        }
        getButton(phrase[0])?.setBackgroundColor(resources.getColor(R.color.LightBlue))
    }

    private fun toCorrectState() {
        setResultText("Correct!")
        streak++
        binding.textViewStreak.text = "Streak: $streak"
        for (i in 0 until phraseSize) {
            getButton(phrase[i])?.setBackgroundColor(getPhraseColor(i))
        }
        state = AppStates.CORRECT_GUESS
    }

    private fun toIncorrectState(guess: Int) {
        streak = 0
        binding.textViewStreak.text = "Streak: $streak"
        setResultText("Incorrect!")
        getButton(guess)?.setBackgroundColor(resources.getColor(R.color.OrangeRed))

        for (i in 0..(nCorrectGuesses+1)) {
            getButton(phrase[i])?.setBackgroundColor(getPhraseColor(i))
        }
        state = AppStates.INCORRECT_GUESS
    }

    private fun releasePlayers() {
        cancelPhrase()
        for (player in players) {
            player.stop()
            player.release()
        }
        players = mutableListOf()
    }

    override fun onDestroy() {
        releasePlayers()
        cancelGuess()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun takeGuess(guess: Int): GuessResult {
        when (getGuessResult(guess, nCorrectGuesses+1)) {
            GuessResult.CORRECT -> {
                nCorrectGuesses++
                setResultText(getGuessString(nCorrectGuesses + 2))
                getButton(guess)?.setBackgroundColor(getPhraseColor(nCorrectGuesses))
                if (nCorrectGuesses >= phraseSize - 1) {
                    toCorrectState()
                }
                return GuessResult.CORRECT
            }
            GuessResult.SAME_NOTE -> {
                return GuessResult.SAME_NOTE
            }
            else -> {
                toIncorrectState(guess)
                return GuessResult.INCORRECT
            }
        }
    }

    private fun setResultText(text: String) {
        binding.ResultTextView.text = text
    }

    private fun getPhraseColor(idx: Int): Int {
        val ratio: Float = kotlin.math.min(idx * 1.0F / (phraseSize - 1.0F), 1.0F)
        return ColorUtils.blendARGB(resources.getColor(R.color.LightBlue), resources.getColor(R.color.Green), ratio)
    }

    private fun getGuessResult(guess: Int, noteIdx: Int, maxInterval: Int = 12): GuessResult {
        when (guess) {
            phrase[noteIdx] -> return GuessResult.CORRECT
            phrase[noteIdx - 1] -> return GuessResult.SAME_NOTE
        }
        if (kotlin.math.abs(guess - phrase[noteIdx]) > maxInterval) {
            return GuessResult.OUT_OF_RANGE
        }
        return GuessResult.INCORRECT
    }

    private fun playSingleNote(note: Int) {
        while (guessJobs.size > maxGuessJobs) {
            guessJobs.removeFirst().cancel()
        }

        val job: Job = GlobalScope.launch {
            var player: MediaPlayer? = null
            try {
                player = MediaPlayer.create(activity, getNote(note))
                guessPlayers.add(player)
                val playing = playNote(player)
                playing.await()
                delay(5000)
            } finally {
                player = guessPlayers.removeFirstOrNull()
                player?.stop()
                player?.release()
            }
        }
        guessJobs.add(job)
    }

    private fun getButton(note: Int): android.widget.Button? {
        when (note) {
            0 -> return binding.buttonC3
            1 -> return binding.buttonDb3
            2 -> return binding.buttonD3
            3 -> return binding.buttonEb3
            4 -> return binding.buttonE3
            5 -> return binding.buttonF3
            6 -> return binding.buttonGb3
            7 -> return binding.buttonG3
            8 -> return binding.buttonAb3
            9 -> return binding.buttonA3
            10 -> return binding.buttonBb3
            11 -> return binding.buttonB3
            12 -> return binding.buttonC4
            13 -> return binding.buttonDb4
            14 -> return binding.buttonD4
            15 -> return binding.buttonEb4
            16 -> return binding.buttonE4
            17 -> return binding.buttonF4
            18 -> return binding.buttonGb4
            19 -> return binding.buttonG4
            20 -> return binding.buttonAb4
            21 -> return binding.buttonA4
            22 -> return binding.buttonBb4
            23 -> return binding.buttonB4
            24 -> return binding.buttonC5
            25 -> return binding.buttonDb5
            26 -> return binding.buttonD5
            27 -> return binding.buttonEb5
            28 -> return binding.buttonE5
            29 -> return binding.buttonF5
            30 -> return binding.buttonGb5
            31 -> return binding.buttonG5
            32 -> return binding.buttonAb5
            33 -> return binding.buttonA5
        }
        return null
    }
}