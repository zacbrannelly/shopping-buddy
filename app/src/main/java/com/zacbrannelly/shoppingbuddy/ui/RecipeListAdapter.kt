package com.zacbrannelly.shoppingbuddy.ui

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.entity.Recipe
import java.util.*

private const val TAG = "RecipeListAdapter"

class RecipeListAdapter(private val itemClickListener: (Recipe, ImageView) -> Unit,
                        private val itemOrderChangedListener: ((Int, Int, List<RecipeListItem>) -> Unit)? = null): RecyclerView.Adapter<RecipeListAdapter.ViewHolder>() {

    private var touchHelper: ItemTouchHelper? = null
    private var touchHelperListener = ItemMoveListener()
    private var items = emptyList<RecipeListItem>()

    // ItemTouchHelper listener that allows dragging items.
    inner class ItemMoveListener : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            // Allow up and down movements only.
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // Notify the adapter of the change.
            onMoveItem(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // STUB
        }

    }

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
            // Disable corner rounding on top and bottom right corners of the image.
            image.shapeAppearanceModel = image.shapeAppearanceModel
                .toBuilder()
                .setTopRightCorner(CornerFamily.CUT, 0.0f)
                .setBottomRightCorner(CornerFamily.CUT, 0.0f)
                .build()
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun populate(item: RecipeListItem) {
            if (item.viewType == RecipeListItem.VIEW_TYPE_ITEM)
                view.setOnClickListener { itemClickListener(item.recipe!!, image) }

            // Hide the drag handle if not draggable.
            dragHandle.visibility =
                if (item.isDraggable) ImageView.VISIBLE else ImageView.INVISIBLE

            // Allow reordering by drag if draggable.
            if (item.isDraggable) {
                dragHandle.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        touchHelper?.startDrag(this)
                    }
                    return@setOnTouchListener true
                }
            }
        }
    }

    fun onMoveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        Log.i(TAG, "Item reordering occurred from $fromPosition to $toPosition")

        itemOrderChangedListener?.invoke(fromPosition, toPosition, items)
    }

    fun setItems(data: List<RecipeListItem>) {
        items = data
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        // Create touch helper and attach to recycler view.
        touchHelper = ItemTouchHelper(touchHelperListener)
        touchHelper?.attachToRecyclerView(recyclerView)
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

