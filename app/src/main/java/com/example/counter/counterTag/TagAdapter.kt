package com.example.counter.counterTag

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.models.Tags
import com.example.counter.databinding.TagListItemViewBinding
import com.example.counter.models.Counter


class TagAdapter() :
    androidx.recyclerview.widget.ListAdapter<Tags, RecyclerView.ViewHolder>(diffUtilCallback()) {

    private  lateinit var mRecyclerView :RecyclerView

    class TagView( val binding: TagListItemViewBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TagView(TagListItemViewBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TagView) {
            holder.binding.item = getItem(position)
            holder.binding.executePendingBindings()
        }
    }

    fun getTag(bindingAdapterPosition: Int) : Tags {
        return getItem(bindingAdapterPosition)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }
    override fun submitList(list: MutableList<Tags>?) {
        super.submitList(list?.let { ArrayList(it).sortedByDescending {
            it.count
        }})

        mRecyclerView.layoutManager?.smoothScrollToPosition(mRecyclerView, RecyclerView.State(), 0)
    }

}


class diffUtilCallback :DiffUtil.ItemCallback<Tags>() {
    override fun areItemsTheSame(oldItem: Tags, newItem: Tags): Boolean {
     return   oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Tags, newItem: Tags): Boolean {
        return oldItem == newItem
    }
}