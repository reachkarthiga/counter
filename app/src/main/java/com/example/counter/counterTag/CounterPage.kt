package com.example.counter.counterTag

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.hardware.input.InputManager
import android.media.MediaPlayer
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.R
import com.example.counter.databinding.FragmentCounterPageBinding
import com.example.counter.databinding.HeadingEdittextButtonDialogBinding
import com.example.counter.models.Counter
import com.example.counter.models.Tags
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import org.koin.android.ext.android.inject


class CounterPage : Fragment() {

    val viewModel: CounterViewModel by inject()
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mediaPlayer = MediaPlayer.create(this@CounterPage.requireContext(), R.raw.sound)

        val binding = FragmentCounterPageBinding.inflate(layoutInflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        binding.saveButton.setOnClickListener {
            if (viewModel.counterName.value == TEMPORARY_COUNTER) {
                showSaveCounterDialog()
            }
        }

        binding.resetButton.setOnClickListener {
            showConfirmationDialog()
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

        val adapter = TagAdapter()

        binding.tagList.adapter = adapter

        viewModel.tagList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        val callBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {


                when (direction) {

                    ItemTouchHelper.LEFT -> {
                        viewModel.deleteTag(adapter.getCounter(viewHolder.bindingAdapterPosition))
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

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .create().decorate()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            }

        }

        val itemTouchHelper = ItemTouchHelper(callBack)
        itemTouchHelper.attachToRecyclerView(binding.tagList)


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

                val inputManager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(view?.windowToken, 0)

            }
        }

        return binding.root

    }

    private fun showEditDialog(tag: Tags) {

        val builder: AlertDialog = AlertDialog.Builder(context).create()
        val view = HeadingEdittextButtonDialogBinding.inflate(layoutInflater)

        view.heading.text = "Edit Tag"
        view.edittext.setText(tag.tagName)
        view.button.text = "Save"
        view.edittext.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        builder.setView(view.root)
        builder.setCancelable(true)
        builder.show()

        view.button.setOnClickListener {
            if (view.edittext.text.toString().trim().isNotEmpty()) {
                viewModel.updateTag(Tags(view.edittext.text.toString(), tag.count, tag.counterName, tag.id))
                builder.dismiss()
            } else {
                Toast.makeText(context, "Counter Name is Mandatory", Toast.LENGTH_SHORT).show()
            }
        }




    }

    private fun showConfirmationDialog() {

        val builder = AlertDialog.Builder(context)

        builder.setTitle("Are You sure ?")
        builder.setMessage("This will delete the tags and counter. Are you sure you want to continue ?" + "\n\nClick Tags Alone option to delete only tags." )

        builder.setPositiveButton("Yes" , DialogInterface.OnClickListener { dialog, which ->
            viewModel.setCounterCount(0)
            viewModel.clearTags()
        })

        builder.setNegativeButton( "No", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })

        builder.setNeutralButton("Tags alone" , DialogInterface.OnClickListener { dialog, which ->
            viewModel.clearTags()
        })

        builder.create().show()

    }

    private fun showGoToDialog() {

        val builder: AlertDialog = AlertDialog.Builder(context).create()
        val view =
            HeadingEdittextButtonDialogBinding.inflate(layoutInflater)

        view.heading.text = "Go To"
        view.edittext.hint = "Counter Value"
        view.button.text = "Done"
        view.edittext.inputType = InputType.TYPE_CLASS_NUMBER

        builder.setView(view.root)
        builder.setCancelable(true)
        builder.show()

        view.button.setOnClickListener {
            if (view.edittext.text.toString().isNotEmpty()) {
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
        view.button.text = "Save"
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

    override fun onDestroyView() {
        mediaPlayer.release()
        super.onDestroyView()

    }


}