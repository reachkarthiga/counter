package com.example.counter.counterTag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.models.Tags
import com.example.counter.R
import com.example.counter.databinding.FragmentCounterPageBinding

class CounterPage : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding =  FragmentCounterPageBinding.inflate(layoutInflater)

        val viewModel = CounterViewModel()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.tagList.adapter = TagAdapter(listOf(Tags("tagName", 1, "counterName")))

        binding.tagList.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        binding.addTag.setOnClickListener {
            viewModel.saveTag(binding.tagName.text.toString(), binding.countValue.text.toString().toInt(), binding.counterName.text.toString())
        }

        return binding.root

    }

}