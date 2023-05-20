package com.example.counter.savedCounters

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.R
import com.example.counter.counterTag.CounterPage
import com.example.counter.counterTag.CounterViewModel
import com.example.counter.dataBase.Database
import com.example.counter.databinding.FragmentSavedCountersBinding
import com.example.counter.databinding.SaveCounterDialogBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SavedCounters : Fragment() {

    val viewModel: CounterViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding = FragmentSavedCountersBinding.inflate(layoutInflater)

        val adapter = CounterAdapter(CounterClickListener {
            viewModel.setCounterValues(it)
            replaceFragment(CounterPage())
        })

        viewModel.counterList.observe(viewLifecycleOwner, Observer {

            if (it.isEmpty()) {
                binding.noCounterData.visibility = VISIBLE
            } else {
                binding.noCounterData.visibility = INVISIBLE
            }

            adapter.submitList(it)

        })

        binding.addNew.setOnClickListener {
            showSaveCountDialog()
        }

        binding.recyclerViewCounters.adapter = adapter

        binding.recyclerViewCounters.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return binding.root

    }

    private fun showSaveCountDialog() {

        val builder: AlertDialog = AlertDialog.Builder(context).create()
        val view = SaveCounterDialogBinding.inflate(layoutInflater)

        builder.setView(view.root)
        builder.setCancelable(true)
        builder.show()

        view.saveButton.setOnClickListener {
            if (view.counterName.text.toString().trim().isNotEmpty()) {
                viewModel.saveCounter(view.counterName.text.toString())
                builder.dismiss()
                replaceFragment(CounterPage())
                viewModel.updateNewCounterName(view.counterName.text.toString().trim())
            } else {
                Toast.makeText(context, "Counter Name is Mandatory", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment , fragment)
        fragmentTransaction.commit()

    }

}