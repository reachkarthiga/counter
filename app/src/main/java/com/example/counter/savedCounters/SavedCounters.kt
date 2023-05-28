package com.example.counter.savedCounters

import android.app.AlertDialog
import android.graphics.Canvas
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.R
import com.example.counter.counterTag.CounterPage
import com.example.counter.counterTag.CounterViewModel
import com.example.counter.databinding.FragmentSavedCountersBinding
import com.example.counter.databinding.HeadingEdittextButtonDialogBinding
import com.example.counter.models.Counter
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import org.koin.android.ext.android.inject

class SavedCounters : Fragment() {

    val viewModel: CounterViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding = FragmentSavedCountersBinding.inflate(layoutInflater)

        val adapter = CounterAdapter(CounterClickListener {
            viewModel.setCounterValuesOnScreen(it)
            replaceFragment(CounterPage())
        })

        binding.progressCircular.visibility = VISIBLE
        binding.noCounterData.visibility = INVISIBLE
        binding.recyclerViewCounters.visibility = INVISIBLE

        viewModel.counterList.observe(viewLifecycleOwner, Observer {

            binding.progressCircular.visibility = INVISIBLE

            if (it.isEmpty()) {
                binding.noCounterData.visibility = VISIBLE
            } else {
                binding.noCounterData.visibility = INVISIBLE
                binding.recyclerViewCounters.visibility = VISIBLE

            }

            adapter.submitList(it)

        })

        binding.addNew.setOnClickListener {
            showSaveCountDialog()
        }

        val callBack = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {


                when (direction) {

                    ItemTouchHelper.LEFT -> {
                        viewModel.deleteCounter(adapter.getCounter(viewHolder.bindingAdapterPosition))
                    }

                    ItemTouchHelper.RIGHT -> {

                        val list = adapter.currentList.toMutableList()
                        list.removeAt(viewHolder.bindingAdapterPosition)
                        adapter.submitList(
                            list
                        )

                        showEditDialog(adapter.getCounter(viewHolder.bindingAdapterPosition))

                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                    .addSwipeRightBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        )
                    )
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .create().decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

            }

        }

        val itemTouchHelper = ItemTouchHelper(callBack)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewCounters)

        binding.recyclerViewCounters.adapter = adapter

        binding.recyclerViewCounters.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return binding.root

    }

    private fun showEditDialog(counter: Counter) {

        val builder: AlertDialog = AlertDialog.Builder(context).create()
        val view = HeadingEdittextButtonDialogBinding.inflate(layoutInflater)

        view.heading.text = "Edit Counter"
        view.edittextBox.hint = "Counter Name"
        view.edittext.setText(counter.name)
        view.button.text = "Save"
        view.edittext.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        builder.setView(view.root)
        builder.setCancelable(false)
        builder.show()

        view.button.setOnClickListener {

            if (view.edittext.text.toString().trim().isEmpty()) {
                sendMessage("Counter Name is Mandatory")
                return@setOnClickListener
            }

            if (!viewModel.checkCounterAvailability(view.edittext.text.toString().trim())) {
                sendMessage("There is another counter with this name! Pls change the name!")
                return@setOnClickListener
            }

            viewModel.updateCounter(counter,
                Counter(
                    view.edittext.text.toString(),
                    counter.count,
                    counter.tagsCount,
                    counter.id
                )
            )
            builder.dismiss()

        }


    }

    private fun sendMessage(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun showSaveCountDialog() {

        val builder: AlertDialog = AlertDialog.Builder(context).create()
        val view = HeadingEdittextButtonDialogBinding.inflate(layoutInflater)

        view.heading.text = "Add Counter"
        view.edittextBox.hint = "Counter Name"
        view.button.text = "Save"
        view.edittext.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        builder.setView(view.root)
        builder.setCancelable(true)
        builder.show()

        view.button.setOnClickListener {

            if (view.edittext.text.toString().trim().isEmpty()) {
                sendMessage("Counter Name is Mandatory")
                return@setOnClickListener
            }

            if (!viewModel.checkCounterAvailability(view.edittext.text.toString().trim())) {
                sendMessage("There is another counter with this name! Pls change the name!")
                return@setOnClickListener
            }

            viewModel.saveCounter(view.edittext.text.toString())
            builder.dismiss()
            replaceFragment(CounterPage())
            viewModel.setNewCounterOnScreen(view.edittext.text.toString().trim())

        }

    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, fragment)
        fragmentTransaction.commit()

    }

}