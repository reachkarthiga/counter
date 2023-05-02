package com.example.counter.savedCounters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.counter.R
import com.example.counter.databinding.FragmentSavedCountersBinding

class SavedCounters : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding = FragmentSavedCountersBinding.inflate(layoutInflater)

        return binding.root

    }

}