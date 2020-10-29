package com.zacbrannelly.shoppingbuddy.ui.shoppinglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.data.Ingredient
import com.zacbrannelly.shoppingbuddy.data.ShoppingListItem

class ShoppingListAdapter: RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>(){

    var onItemSelected: ((Ingredient, Boolean) -> Unit)? = null

    private var items: List<ShoppingListItem> = emptyList()

    open inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        private val heading: TextView = view.findViewById(R.id.list_item_heading)
        private val caption: TextView = view.findViewById(R.id.list_item_caption)
        private val checkBox: MaterialCheckBox = view.findViewById(R.id.list_item_checkbox)

        fun populate(item: ShoppingListItem) {
            heading.text = item.ingredient.name
            caption.text = "${item.qty} ${item.ingredient.units}"

            // Remove previous listener
            checkBox.setOnCheckedChangeListener(null)

            // Check the box if the entire list item is selected.
            view.setOnClickListener {
                checkBox.isChecked = !checkBox.isChecked
            }

            // Set the checkBox to the state of the item.
            checkBox.isChecked = item.isChecked

            // Listen for changes, invoke lambda and set state to current data instance.
            checkBox.setOnCheckedChangeListener { _, checked ->
                onItemSelected?.invoke(item.ingredient, checked)
                item.isChecked = checked
            }
        }
    }

    fun setItems(items: List<ShoppingListItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_tickable_ingredient_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populate(items[position])
    }
}