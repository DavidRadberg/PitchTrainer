package com.example.pitchtrainer

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pitchtrainer.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private var phrase: List<Int> = emptyList()
    private var players: MutableList<MediaPlayer> = mutableListOf()
    private var phraseSize: Int = 4
    private val waitTimeMs: Long = 800
    private var nPrepared: Int = 0

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
        binding.buttonSecond.setOnClickListener {
            playAllNotes()
        }

    }

    fun generateNotes() {
        phrase = generatePhrase(phraseSize)

        for (note in phrase) {
            players.add(MediaPlayer.create(activity, getNote(note)))
        }
    }

    fun onPrepared() {
        nPrepared = 1
    }

    fun playAllNotes() {
        for (player in players) {
            playNote(player, waitTimeMs)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}