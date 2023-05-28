package com.example.counter.settings

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.counter.counterTag.CounterViewModel
import com.example.counter.databinding.FragmentSettingsBinding
import org.koin.android.ext.android.inject

class Settings : Fragment() {

    val viewModel:SettingsViewModel by inject()
    val counterViewModel: CounterViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentSettingsBinding.inflate(layoutInflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.resetCounters.setOnClickListener {
            showConfirmationDialogToDeleteAll()
        }

        binding.stepPlus.setOnClickListener {
            viewModel.increaseStepValue()
            counterViewModel.steps = viewModel.step_value.value ?: 1
        }

        binding.stepMinus.setOnClickListener {
            viewModel.decreaseStepValue()
            counterViewModel.steps = viewModel.step_value.value ?: 1
        }

        binding.switchTap.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.setTap(true)
                counterViewModel.tapToIncrease = true
            } else {
                viewModel.setTap(false)
                counterViewModel.tapToIncrease = false
            }
        }

        binding.switchSound.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.setPlayingSound(true)
                counterViewModel.playSound = true
            } else {
                viewModel.setPlayingSound(false)
                counterViewModel.playSound = false
            }
        }

        binding.appInfo3.movementMethod = LinkMovementMethod.getInstance()

        return binding.root
    }

    private fun showConfirmationDialogToDeleteAll() {

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Are You Sure ?")
        builder.setMessage("This will clear all the counters and tags. Are you sure to delete all counters ?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            viewModel.clearAll()
            counterViewModel.count.value = 0
            counterViewModel.counterNameSelected.value = ""
            dialog.dismiss()
        })

        builder.setNegativeButton("No" , DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })

        builder.show()

    }

}