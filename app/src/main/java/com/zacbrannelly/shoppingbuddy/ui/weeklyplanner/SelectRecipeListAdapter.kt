package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.data.RecipeWithIngredients
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val TAG = "SelectRecipeListAdapter"

class SelectRecipeListAdapter(private val context: Context): RecyclerView.Adapter<SelectRecipeListAdapter.ViewHolder>() {

    var onItemSelected: ((RecipeWithIngredients) -> Unit)? = null

    private var items: List<SelectRecipeListItem> = emptyList()

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.list_item_image)
        private val heading: TextView = view.findViewById(R.id.list_item_heading)
        private val subHeading: TextView = view.findViewById(R.id.list_item_sub_heading)
        private val caption: TextView = view.findViewById(R.id.list_item_caption)
        private val checkBox: MaterialCheckBox = view.findViewById(R.id.list_item_checkbox)

        fun populate(listItem: SelectRecipeListItem) {
            // Load bitmap of recipe in coroutine.
            CoroutineScope(Dispatchers.Main).launch {
                val task = async(Dispatchers.IO) { listItem.recipe.recipe.loadBitmap(context) }
                val bitmap = task.await()
                image.setImageBitmap(bitmap)
            }

            heading.text = listItem.recipe.recipe.name
            subHeading.text = listItem.recipe.recipe.type
            caption.text = listItem.recipe.ingredientsWithQty.joinToString { it.ingredient.name }

            // Remove previous listener.
            checkBox.setOnCheckedChangeListener(null)

            // Check the box on click.
            view.setOnClickListener {
                checkBox.isChecked = !checkBox.isChecked
            }

            // Set the state of the check box.
            checkBox.isChecked = listItem.isSelected

            // Notify of the check state changing.
            checkBox.setOnCheckedChangeListener { _, _ ->
                onItemSelected?.invoke(listItem.recipe)
            }
        }
    }

    fun setItems(items: List<RecipeWithIngredients>) {
        this.items = items.map { SelectRecipeListItem(it) }
        notifyDataSetChanged()
    }

    fun setSelectedItems(newSelectedItems: List<RecipeWithIngredients>) {
        // Only notify the item's that were checked.
        items.forEachIndexed { i, item ->
            if (item.isSelected && !newSelectedItems.contains(item.recipe)) {
                // Has been deselected.
                item.isSelected = false
                notifyItemChanged(i)
            } else if (!item.isSelected && newSelectedItems.contains(item.recipe)) {
                // Has been selected.
                item.isSelected = true
                notifyItemChanged(i)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_tickable_recipe_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populate(items[position])
    }


}