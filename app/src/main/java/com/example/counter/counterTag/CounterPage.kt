package com.example.counter.counterTag

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.media.MediaPlayer
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.R
import com.example.counter.databinding.FragmentCounterPageBinding
import com.example.counter.databinding.HeadingEdittextButtonDialogBinding
import com.example.counter.models.Tags
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import org.koin.android.ext.android.inject


class CounterPage : Fragment() {

    private val viewModel: CounterViewModel by inject()
    private lateinit var mediaPlayer: MediaPlayer
    private val adapter: TagAdapter = TagAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mediaPlayer = MediaPlayer.create(this@CounterPage.requireContext(), R.raw.sound)

        val inputManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val binding = FragmentCounterPageBinding.inflate(layoutInflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        binding.saveButton.setOnClickListener {
            if (viewModel.counterNameSelected.value == TEMPORARY_COUNTER) {
                showSaveCounterDialog()
            }
        }

        binding.resetButton.setOnClickListener {
            showConfirmationDialogForReset()
        }

        binding.gotoButton.setOnClickListener {
            showGoToDialog()
        }

        if (viewModel.tapToIncrease) {
            binding.counterPage.setOnClickListener {
                viewModel.increaseCountValue()
                if (viewModel.playSound) {
                    mediaPlayer.start()
                }
            }
        }

        binding.increment.setOnClickListener {
            viewModel.increaseCountValue()
            if (viewModel.playSound) {
                mediaPlayer.start()
            }
        }

        binding.decrement.setOnClickListener {
            viewModel.decreaseCountValue()
            if (viewModel.playSound) {
                mediaPlayer.start()
            }
        }

        val layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        binding.tagList.adapter = adapter

        viewModel.tagList.observe(viewLifecycleOwner, Observer {
            if (viewModel.counterNameSelected.value != TEMPORARY_COUNTER) {
                adapter.submitList(it.toMutableList())
            }
        })

        if (viewModel.tempTagList.isNotEmpty() && viewModel.counterNameSelected.value == TEMPORARY_COUNTER) {
            adapter.submitList(viewModel.tempTagList)
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

                val tag = adapter.getTag(viewHolder.bindingAdapterPosition)

                when (direction) {

                    ItemTouchHelper.LEFT -> {

                        if (tag.counterName == TEMPORARY_COUNTER) {
                            viewModel.tempTagList.remove(tag)
                            adapter.submitList(viewModel.tempTagList)
                        } else {
                            viewModel.deleteTag(tag)
                        }

                    }

                    ItemTouchHelper.RIGHT -> {

                        if (tag.counterName == TEMPORARY_COUNTER) {
                            viewModel.tempTagList.removeAt(viewHolder.bindingAdapterPosition)
                            showEditTagDialog(tag)
                            adapter.submitList(viewModel.tempTagList)
                        } else {
                            showEditTagDialog(tag)
                        }

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
        itemTouchHelper.attachToRecyclerView(binding.tagList)

        binding.tagList.layoutManager = layoutManager


        binding.addTag.setOnClickListener {

            if (binding.tagName.text.toString().trim().isEmpty()) {
                sendMessage("Tag Name cant be empty")
                binding.tagName.text?.clear()
                inputManager.hideSoftInputFromWindow(view?.windowToken, 0)
                return@setOnClickListener
            }

            if (adapter.currentList.toMutableList().any { it.count == viewModel.count.value }) {
                sendMessage("Count value already Tagged")
                binding.tagName.text?.clear()
                inputManager.hideSoftInputFromWindow(view?.windowToken, 0)
                return@setOnClickListener
            }

            when (viewModel.counterNameSelected.value) {

                TEMPORARY_COUNTER -> {

                    viewModel.tempTagList.add(
                        Tags(
                            binding.tagName.text.toString(),
                            binding.countValue.text.toString().toInt(),
                            binding.counterName.text.toString(),
                            System.currentTimeMillis()
                        )
                    )

                    adapter.submitList(viewModel.tempTagList)
                }

                else -> {

                    viewModel.saveTag(
                        binding.tagName.text.toString(),
                        binding.countValue.text.toString().toInt(),
                        binding.counterName.text.toString()
                    )

                    viewModel.increaseTagCount(binding.counterName.text.toString())

                }

            }

            binding.tagName.text?.clear()
            inputManager.hideSoftInputFromWindow(view?.windowToken, 0)
            binding.tagName.clearFocus()


        }

        viewModel.showToastOnMaxiCount.observe(viewLifecycleOwner, Observer {
            if (it) {
                sendMessage("Count Maximum Value reached. Create a new counter to continue Counting!")
            }
        })

        return binding.root

    }

    private fun sendMessage(msg: String) {
        Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun showEditTagDialog(tag: Tags) {

        val builder: AlertDialog = AlertDialog.Builder(context).create()
        val view = HeadingEdittextButtonDialogBinding.inflate(layoutInflater)

        view.heading.text = "Edit Tag"
        view.edittextBox.hint = "Tag Name"
        view.edittext.setText(tag.tagName)
        view.button.text = "Save"
        view.edittext.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        builder.setView(view.root)
        builder.setCancelable(false)
        builder.show()

        view.button.setOnClickListener {

            if (view.edittext.text.toString().trim().isEmpty()) {
                sendMessage("Tag Name is Mandatory")
                return@setOnClickListener
            }

            if (tag.counterName == TEMPORARY_COUNTER) {

                viewModel.tempTagList.add(
                    Tags(
                        view.edittext.text.toString().trim(),
                        tag.count,
                        tag.counterName,
                        tag.id
                    )
                )

                adapter.submitList(viewModel.tempTagList)

            } else {
                viewModel.updateTag(
                    Tags(
                        view.edittext.text.toString(),
                        tag.count,
                        tag.counterName,
                        tag.id
                    )
                )
            }

            builder.dismiss()

        }

    }

    private fun showConfirmationDialogForReset() {

        val builder = AlertDialog.Builder(context)

        builder.setTitle("Are You sure ?")
        builder.setMessage("This will delete the tags and counter. Are you sure you want to continue ?" + "\n\nClick Tags Alone option to delete only tags.")

        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->

            if (viewModel.counterNameSelected.value != TEMPORARY_COUNTER) {
                viewModel.setCounterCount(0)
                viewModel.clearTags()
            } else {
                viewModel.count.value = 0
                viewModel.tempTagList.clear()
                adapter.submitList(viewModel.tempTagList)
            }

        })

        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })

        builder.setNeutralButton("Tags alone", DialogInterface.OnClickListener { dialog, which ->
            if (viewModel.counterNameSelected.value != TEMPORARY_COUNTER) {
                viewModel.clearTags()
            } else {
                viewModel.tempTagList.clear()
                adapter.submitList(viewModel.tempTagList)
            }
        })

        builder.create().show()

    }

    private fun showGoToDialog() {

        val builder: AlertDialog = AlertDialog.Builder(context).create()
        val view =
            HeadingEdittextButtonDialogBinding.inflate(layoutInflater)

        view.heading.text = "Go To"
        view.edittextBox.hint = "Counter Value"
        view.button.text = "Done"
        view.edittext.inputType = InputType.TYPE_CLASS_NUMBER

        builder.setView(view.root)
        builder.setCancelable(true)
        builder.show()

        view.button.setOnClickListener {

            if (view.edittext.text.toString().isEmpty()) {
                sendMessage("Counter Value is Mandatory")
                return@setOnClickListener
            }

            if (view.edittext.text.toString().toInt() >= 9999) {
                sendMessage("Maximum Counter value is 9999. Can't increase more!")
                return@setOnClickListener
            }

            viewModel.setCounterCount(view.edittext.text.toString().toInt())
            builder.dismiss()

        }

    }

    private fun showSaveCounterDialog() {

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

            viewModel.tempTagList.forEach {
                viewModel.saveTag(it.tagName, it.count, view.edittext.text.toString().trim())
            }

            viewModel.tempTagList.clear()
            adapter.submitList(viewModel.tempTagList)

            viewModel.saveCounter(view.edittext.text.toString())
            builder.dismiss()

        }

    }

    override fun onDestroyView() {
        mediaPlayer.release()
        super.onDestroyView()

    }


}