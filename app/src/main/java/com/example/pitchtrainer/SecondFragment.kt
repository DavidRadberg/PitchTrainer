package com.example.pitchtrainer

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pitchtrainer.databinding.FragmentSecondBinding
import java.lang.Math.abs

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private var phrase: List<Int> = emptyList()
    private var players: MutableList<MediaPlayer> = mutableListOf()
    private var singlePlayer: MediaPlayer? = null
    private var phraseSize: Int = 2
    private val waitTimeMs: Long = 800
    private var state: app_states = app_states.BASELINE
    private val whiteKeys: List<Int> =
        listOf(0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21, 23, 24, 26, 28, 29, 31, 33)
    private val blackKeys: List<Int> =
        listOf(1, 3, 6, 8, 10, 13, 15, 18, 20, 22, 25, 27, 30, 32)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateNotes()
        binding.buttonPlayPhrase.setOnClickListener {
            if (state != app_states.WAITING_FOR_GUESS) {
                toGuessState()
            }
            playAllNotes()
        }
        binding.buttonNewPhrase.setOnClickListener {
            generateNotes()
            binding.buttonPlayPhrase.callOnClick()
        }
    }

    private fun generateNotes() {
        releasePlayers()
        phrase = generatePhrase(phraseSize)

        for (note in phrase) {
            players.add(MediaPlayer.create(activity, getNote(note)))
        }
        toGuessState()
    }

    private fun playAllNotes() {
        for (player in players) {
            playNote(player, waitTimeMs)
        }
    }

    private fun toGuessState() {
        state = app_states.WAITING_FOR_GUESS

        for (i in 0..notes.size) {
            val button: android.widget.Button = getButton(i)
            when (i) {
                in whiteKeys -> button.setBackgroundColor(resources.getColor(R.color.White))
                in blackKeys -> button.setBackgroundColor(resources.getColor(R.color.Black))
            }
            getButton(i).setOnClickListener() {
                if (takeGuess(i) != guess_result.CORRECT) {
                    playSingleNote(i)
                }
            }
        }
        getButton(phrase[0]).setBackgroundColor(resources.getColor(R.color.Green))
    }

    private fun toCorrectState() {
        for (note in phrase) {
            getButton(note).setBackgroundColor(resources.getColor(R.color.Green))
        }
        state = app_states.CORRECT_GUESS
    }

    private fun toIncorrectState(guess: Int) {
        getButton(guess).setBackgroundColor(resources.getColor(R.color.Red))
        state = app_states.INCORRECT_GUESS
    }

    private fun releasePlayers() {
        for (player in players) {
            player.stop()
            player.release()
        }
        players = mutableListOf()
        singlePlayer?.stop()
        singlePlayer?.release()
    }

    override fun onDestroy() {
        releasePlayers()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun takeGuess(guess: Int): guess_result {
        val guessResult: guess_result = getGuessResult(guess, 1)
        when (guessResult) {
            guess_result.CORRECT -> toCorrectState()
            else -> {
                toIncorrectState(guess)
            }
        }
        return guessResult
    }

    private fun getGuessResult(guess: Int, noteIdx: Int, maxInterval: Int = 12): guess_result {
        when (guess) {
            phrase[noteIdx] -> return guess_result.CORRECT
            phrase[noteIdx - 1] -> return guess_result.SAME_NOTE
        }
        if (abs(guess - phrase[noteIdx]) > maxInterval) {
            return guess_result.OUT_OF_RANGE
        }
        return guess_result.INCORRECT
    }

    private fun playSingleNote(note: Int) {
        singlePlayer?.release()
        singlePlayer = MediaPlayer.create(activity, getNote(note))
        playNote(singlePlayer)
    }


    //0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21, 23, 24, 26, 28, 29, 31, 33
    private fun getButton(note: Int): android.widget.Button {
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
            else -> {
                return binding.buttonC3
            }
        }
    }
}