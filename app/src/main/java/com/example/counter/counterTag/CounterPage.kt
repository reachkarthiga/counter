package com.example.counter.counterTag

import android.app.AlertDialog
import android.content.Context
import android.hardware.input.InputManager
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.databinding.FragmentCounterPageBinding
import com.example.counter.databinding.HeadingEdittextButtonDialogBinding
import org.koin.android.ext.android.inject


class CounterPage : Fragment() {

    val viewModel: CounterViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentCounterPageBinding.inflate(layoutInflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        binding.saveButton.setOnClickListener {
            if (viewModel.counterName.value == TEMPORARY_COUNTER) {
                showSaveCounterDialog()
            }
        }

        binding.resetButton.setOnClickListener {
            viewModel.clearCounterCount()
        }

        binding.gotoButton.setOnClickListener {
            showGoToDialog()
        }


        val adapter = TagAdapter()

        binding.tagList.adapter = adapter

        viewModel.tagList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        binding.tagList.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        binding.addTag.setOnClickListener {

            if (binding.tagName.text.toString().trim().isEmpty()) {
                Toast.makeText(this.context, "Tag Name cant be empty", Toast.LENGTH_SHORT).show()
            } else if (viewModel.checkTagAvailability()) {
                Toast.makeText(this.context, "Count value already Tagged", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.saveTag(
                    binding.tagName.text.toString(),
                    binding.countValue.text.toString().toInt(),
                    binding.counterName.text.toString()
                )

                binding.tagName.text?.clear()

                val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(view?.windowToken, 0)

            }
        }

        return binding.root

    }

    private fun showGoToDialog() {

        val builder: AlertDialog = AlertDialog.Builder(context).create()
        val view =
            HeadingEdittextButtonDialogBinding.inflate(layoutInflater)

        view.heading.text = "Go To"
        view.edittext.hint = "Counter Value"
        view.edittext.inputType = InputType.TYPE_CLASS_NUMBER

        builder.setView(view.root)
        builder.setCancelable(true)
        builder.show()

        view.button.setOnClickListener {
            if (view.edittext.text.toString().toInt() > 0) {
                viewModel.setCounterCount(view.edittext.text.toString().toInt())
                builder.dismiss()
            } else {
                Toast.makeText(context, "Counter Value is Mandatory", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showSaveCounterDialog() {

        val builder: AlertDialog = AlertDialog.Builder(context).create()
        val view = HeadingEdittextButtonDialogBinding.inflate(layoutInflater)

        view.heading.text = "Add Counter"
        view.edittext.hint = "Counter Name"
        view.edittext.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        builder.setView(view.root)
        builder.setCancelable(true)
        builder.show()

        view.button.setOnClickListener {
            if (view.edittext.text.toString().trim().isNotEmpty()) {
                viewModel.saveCounter(view.edittext.text.toString())
                builder.dismiss()
            } else {
                Toast.makeText(context, "Counter Name is Mandatory", Toast.LENGTH_SHORT).show()
            }
        }

    }

}