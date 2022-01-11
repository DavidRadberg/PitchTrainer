package com.example.pitchtrainer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.navigation.fragment.findNavController
import com.example.pitchtrainer.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

val difficultyDescriptions: List<String> = listOf(
    "0 does not exist",
    "Start note is c3. Second note is one of [e3, g3, c4].",
    "Start note is c3. Second note is random note from pentatonic C [d3, e3, g3, a3, c4].",
    "Start note is C3. Second note is random note from C scale ascending (all white keys d3-c4).",
    "Start note is C3. Second note is random note from C scale ascending or descending (all white keys c2-c4).",
    "Start and second note are random from C scale, within one octave apart.",
    "Start and second note are random from random scale, within one octave apart.",
    "Phrase consists of 3 random notes from a random scale, within one octave apart.",
    "Phrase consists of 4 random notes from a random scale, within one octave apart.",
    "Phrase consists of 5 random notes from a random scale, within one octave apart.",
)

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var difficulty: Int = 1

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.textviewFirst.text = "Difficulty: $difficulty"
        binding.textviewDecleration.text = difficultyDescriptions[difficulty]
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(ARG_DIFFICULTY, difficulty)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
        }

        binding.seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                difficulty = i + 1
                binding.textviewFirst.text = "Difficulty: $difficulty"
                binding.textviewDecleration.text = difficultyDescriptions[difficulty]
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}