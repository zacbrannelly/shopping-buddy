package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.zacbrannelly.shoppingbuddy.R

private const val TAG = "SelectRecipeListAdapter"

class SelectRecipeListAdapter: RecyclerView.Adapter<SelectRecipeListAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        private val checkBox: MaterialCheckBox = view.findViewById(R.id.list_item_checkbox)

        fun populate() {
            view.setOnClickListener {
                checkBox.isChecked = !checkBox.isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_tickable_recipe_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populate()
    }


}