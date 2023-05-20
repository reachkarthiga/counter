package com.example.counter.counterTag

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.models.Tags
import com.example.counter.R
import com.example.counter.dataBase.Database
import com.example.counter.databinding.FragmentCounterPageBinding
import com.example.counter.databinding.SaveCounterDialogBinding
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

        val menuProvider = object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.counter_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                if (menuItem.itemId == R.id.saveCounter && viewModel.counterName.value == TEMPORARY_COUNTER) {
                    showSaveCounterDialog()
                }

                return true

            }

        }

        binding.toolbar.addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)

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
                Toast.makeText(this.context, "Count value already Tagged", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveTag(
                    binding.tagName.text.toString(),
                    binding.countValue.text.toString().toInt(),
                    binding.counterName.text.toString()
                )
            }
        }

        return binding.root

    }

    private fun showSaveCounterDialog() {

        val builder: AlertDialog = AlertDialog.Builder(context).create()
        val view = SaveCounterDialogBinding.inflate(layoutInflater)

        builder.setView(view.root)
        builder.setCancelable(true)
        builder.show()

        view.saveButton.setOnClickListener {
            if (view.counterName.text.toString().trim().isNotEmpty()) {
                viewModel.saveCounter(view.counterName.text.toString())
                builder.dismiss()
            } else {
                Toast.makeText(context, "Counter Name is Mandatory", Toast.LENGTH_SHORT).show()
            }
        }

    }

}