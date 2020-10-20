package com.zacbrannelly.shoppingbuddy.ui.form

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.zacbrannelly.shoppingbuddy.R
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TextInputListAdapter(private val textInputLayout: Int, private val fields: List<Int>): RecyclerView.Adapter<TextInputListAdapter.ViewHolder>() {
    private var inputs = ArrayList<ArrayList<String>>()

    open inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        // Retrieve all fields registered in the fields property.
        private val inputFields: List<TextInputEditText> = fields.map { view.findViewById(it) as TextInputEditText }
        private var textWatchers = ArrayList<TextWatcher>()

        private fun updateState(emptyCheck: Boolean) {
            val index = adapterPosition
            val hasValue = index < inputs.size

            // Check if each field is empty.
            if (emptyCheck && inputFields.all { it.text.isNullOrBlank() }) {
                // If all fields empty, then remove it from state.
                if (hasValue) {
                    inputFields.forEach { it.clearFocus() }
                    inputs.removeAt(index)
                    notifyItemRemoved(index)
                    return
                }
            }

            // If it was a non-empty change there was no value for this field in state, create one.
            // If it was a non-empty change and there was a value, update the value.
            if (!emptyCheck) {
                val newData = ArrayList<String>()
                newData.addAll(inputFields.map { it.text.toString() })

                if (hasValue) {
                    inputs[index] = newData
                } else {
                    inputs.add(newData)
                    notifyItemInserted(index + 1)
                }
            }
        }

        fun populate(index: Int, value: ArrayList<String>?) {
            // Remove previous watchers.
            textWatchers.forEachIndexed { i, w -> inputFields[i].removeTextChangedListener(w) }
            textWatchers.clear()

            // Set the fields in the view holder
            inputFields.forEachIndexed { i, field ->
                if (!value.isNullOrEmpty()) {
                    field.setText(value[i])
                } else {
                    field.text = null
                }
            }

            // Watch each field for input.
            inputFields.forEach {
                // Create new watcher.
                val textWatcher = object : TextWatcher {
                    override fun afterTextChanged(text: Editable?) {
                        updateState(text.isNullOrEmpty())
                    }

                    override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                }

                // Start watching field.
                it.addTextChangedListener(textWatcher)
                textWatchers.add(textWatcher)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(textInputLayout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = inputs.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populate(position, if (position < inputs.size) inputs[position] else null)
    }
}