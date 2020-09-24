package com.zacbrannelly.shoppingbuddy.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.zacbrannelly.shoppingbuddy.R

class RecipeListAdapter: RecyclerView.Adapter<RecipeListAdapter.ViewHolder>() {

    private var items = emptyList<RecipeListItem>()

    abstract inner class ViewHolder(protected val view: View): RecyclerView.ViewHolder(view) {
        abstract fun populate(item: RecipeListItem)
    }

    inner class HeadingViewHolder(view: View) : ViewHolder(view) {
        private val heading = view.findViewById<TextView>(R.id.list_header_text)

        override fun populate(item: RecipeListItem) {
            heading.text = item.heading
        }
    }

    inner class RecipeViewHolder(view: View) : ViewHolder(view) {
        private val image = view.findViewById<ShapeableImageView>(R.id.list_item_image)
        private val heading = view.findViewById<TextView>(R.id.list_item_heading)
        private val subHeading = view.findViewById<TextView>(R.id.list_item_sub_heading)
        private val caption = view.findViewById<TextView>(R.id.list_item_caption)
        private val dragHandle = view.findViewById<ImageView>(R.id.list_item_drag_handle)

        init {
            // Disable corner rounding on top and bottom right corners.
            image.shapeAppearanceModel = image.shapeAppearanceModel
                .toBuilder()
                .setTopRightCorner(CornerFamily.CUT, 0.0f)
                .setBottomRightCorner(CornerFamily.CUT, 0.0f)
                .build()
        }

        override fun populate(item: RecipeListItem) {
            dragHandle.visibility =
                if (item.isDraggable) ImageView.VISIBLE else ImageView.INVISIBLE
        }
    }

    fun setItems(data: List<RecipeListItem>) {
        items = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var holder: ViewHolder? = null

        // Inflate the layout corresponding to the view type.
        when (viewType) {
            RecipeListItem.VIEW_TYPE_HEADER -> holder = HeadingViewHolder(
                inflater.inflate(R.layout.list_header_item, parent, false))
            RecipeListItem.VIEW_TYPE_ITEM   -> holder = RecipeViewHolder(
                inflater.inflate(R.layout.list_recipe_item, parent, false))
        }

        return holder!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populate(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    override fun getItemCount() = items.size
}

