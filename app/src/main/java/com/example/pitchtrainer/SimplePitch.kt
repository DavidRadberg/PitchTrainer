package com.example.pitchtrainer

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pitchtrainer.databinding.FragmentFirstBinding
import com.example.pitchtrainer.databinding.FragmentSecondBinding
import com.example.pitchtrainer.databinding.FragmentSimplePitchBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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

/**
 * A simple [Fragment] subclass.
 * Use the [SimplePitch.newInstance] factory method to
 * create an instance of this fragment.
 */
class SimplePitch : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mp1: MediaPlayer? = null
    private var mp2: MediaPlayer? = null
    private var notePair: NotePair = NotePair(0, 0)

    private var _binding: FragmentSimplePitchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        notePair = generateInterval(notes.size)
        mp1 = MediaPlayer.create(activity, notePair.getFirstNote())
        mp2 = MediaPlayer.create(activity, notePair.getSecondNote())
    }

    override fun onStop() {
        mp1?.stop()
        mp2?.stop()
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSimplePitchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.interval1.setOnClickListener {
            playNote(mp1)
            Thread.sleep(1_000)
            playNote(mp2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SimplePitch.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SimplePitch().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}