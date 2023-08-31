package com.pavel.scopictask.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pavel.scopictask.data.network.model.ListItem
import com.pavel.scopictask.databinding.ListItemViewBinding

class ListItemAdapter(private val listener: ListItemFragment):
    ListAdapter<ListItem, ListItemAdapter.ListItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ListItemViewHolder(private val binding: ListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        private var currItem: ListItem? = null

        init{
            binding.apply{
                root.rootView.setOnLongClickListener {
                    currItem?.let { item ->
                        listener.callItemDialog(item)
                    }
                    true
                }
            }
        }

        fun bind(item: ListItem){
            currItem = item
            binding.apply {
                listItem.text = item.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val binding = ListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val currItem = getItem(position)
        holder.bind(currItem)
    }
}
interface ItemEvents{
    fun onItemUpdate(item: ListItem)
    fun callItemDialog(item: ListItem)
}