package com.example.counter.savedCounters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.databinding.CounterListItemViewBinding
import com.example.counter.models.Counter

class CounterAdapter(val clickListener: CounterClickListener) :ListAdapter<Counter, RecyclerView.ViewHolder>(diffUtilCallBack()){

    class ViewHolder(val binding : CounterListItemViewBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(CounterListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.binding.viewModel = getItem(position)
            holder.binding.clickListener = clickListener
            holder.binding.executePendingBindings()
        }
    }

    fun getCounter(bindingAdapterPosition: Int) :Counter {
        return getItem(bindingAdapterPosition)
    }

}

class diffUtilCallBack() :DiffUtil.ItemCallback<Counter>() {
    override fun areItemsTheSame(oldItem: Counter, newItem: Counter): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Counter, newItem: Counter): Boolean {
        return oldItem == newItem
    }

}

class CounterClickListener(val clickListener: (counterName :String) -> Unit) {
    fun onClick(counter: Counter) {
        clickListener(counter.name)
    }
}