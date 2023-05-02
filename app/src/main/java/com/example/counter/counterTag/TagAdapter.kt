package com.example.counter.counterTag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.models.Tags
import com.example.counter.databinding.TagListItemViewBinding


class TagAdapter(val list: List<Tags>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class TagView( val binding: TagListItemViewBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TagView(TagListItemViewBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TagView) {
            holder.binding.item = list[position]
            holder.binding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}