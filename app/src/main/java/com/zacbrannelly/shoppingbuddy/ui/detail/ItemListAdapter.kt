package com.zacbrannelly.shoppingbuddy.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zacbrannelly.shoppingbuddy.R

class ItemListAdapter: RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {
    private var items: List<Pair<String, String>> = emptyList()

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        private val heading: TextView = view.findViewById(R.id.list_item_heading)
        private val caption: TextView = view.findViewById(R.id.list_item_caption)

        fun populate(item: Pair<String, String>) {
            heading.text = item.first
            caption.text = item.second
        }
    }

    fun setItems(items: List<Pair<String, String>>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populate(items[position])
    }
}