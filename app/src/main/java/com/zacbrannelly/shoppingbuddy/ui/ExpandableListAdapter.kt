package com.zacbrannelly.shoppingbuddy.ui

import android.content.Context
import android.view.*
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.ui.detail.ItemListAdapter

class ExpandableListAdapter(private val context: Context, private var items: List<ExpandableListItem>): RecyclerView.Adapter<ExpandableListAdapter.ViewHolder>() {
    inner class ViewHolder(val view: View, private val adapter: ItemListAdapter): RecyclerView.ViewHolder(view) {
        private val iconImageView = view.findViewById<ImageView>(R.id.expandable_list_icon)
        private val heading = view.findViewById<TextView>(R.id.expandable_list_heading)
        private val expandIcon = view.findViewById<ImageView>(R.id.expand_icon)
        private val subList = view.findViewById<RecyclerView>(R.id.item_list)
        private val divider = view.findViewById<View>(R.id.expandable_list_divider)

        fun populate(item: ExpandableListItem) {
            iconImageView.setImageDrawable(context.getDrawable(item.icon))
            heading.text = item.header

            // Give adapter sub list items (ingredient or instruction).
            adapter.setItems(item.items)

            view.setOnClickListener {
                item.expanded = !item.expanded
                if (!item.expanded) {
                    collapse()
                } else {
                    expand()
                }
            }

            if (item.expanded) {
                expand()
            } else {
                collapse()
            }
        }

        private fun expand() {
            subList.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            val actualHeight = subList.measuredHeight

            subList.layoutParams.height = 0
            subList.visibility = View.VISIBLE

            // Create animation for expanding the sub list
            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    subList.apply {
                        layoutParams.height =
                            if (interpolatedTime == 1.0f) WindowManager.LayoutParams.WRAP_CONTENT
                            else (actualHeight * interpolatedTime).toInt()

                        requestLayout()
                    }
                }
            }
            animation.duration = (actualHeight / subList.context.resources.displayMetrics.density).toLong()
            subList.startAnimation(animation)

            // Animate the arrow
            expandIcon.animate().setDuration(200).rotation(180.0f)
            divider.visibility = View.VISIBLE
        }

        private fun collapse() {
            val actualHeight = subList.measuredHeight

            // Create animation for collapsing the sub list
            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    subList.apply {
                        if (interpolatedTime == 1.0f) {
                            visibility = View.GONE
                        } else {
                            layoutParams.height =
                                (actualHeight - actualHeight * interpolatedTime).toInt()
                            requestLayout()
                        }
                    }
                }
            }
            animation.duration = (actualHeight / subList.context.resources.displayMetrics.density).toLong()
            subList.startAnimation(animation)

            expandIcon.animate().setDuration(200).rotation(0.0f)
            divider.visibility = View.INVISIBLE
        }
    }

    fun setItems(items: List<ExpandableListItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_expandable_item, parent, false)

        val listAdapter = ItemListAdapter()

        view.findViewById<RecyclerView>(R.id.item_list).apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(parent.context)
            adapter = listAdapter
        }

        return ViewHolder(view, listAdapter)
    }

    override fun getItemCount() = items.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populate(items[position])
    }
}