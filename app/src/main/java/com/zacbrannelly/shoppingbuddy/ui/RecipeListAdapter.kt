package com.zacbrannelly.shoppingbuddy.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.zacbrannelly.shoppingbuddy.R
import com.zacbrannelly.shoppingbuddy.data.Recipe
import kotlinx.coroutines.*
import java.lang.StringBuilder
import java.util.*

private const val TAG = "RecipeListAdapter"

class RecipeListAdapter(val context: Context,
                        private val itemClickListener: (Recipe, ImageView) -> Unit,
                        private val itemOrderChangedListener: (() -> Unit)? = null): RecyclerView.Adapter<RecipeListAdapter.ViewHolder>() {

    private var touchHelper: ItemTouchHelper? = null
    private var touchHelperListener = ItemMoveListener(context)
    private var items = emptyList<RecipeListItem>()

    var onItemRemoved: ((Recipe) -> Unit)? = null

    // ItemTouchHelper listener that allows dragging items.
    inner class ItemMoveListener(private val context: Context) : ItemTouchHelper.Callback() {
        private val background: ColorDrawable = ColorDrawable()
        private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete)

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            // Don't allow movement on headings.
            if (viewHolder is HeadingViewHolder) {
                return 0
            }

            // Allow up or down movement (reordering) and swiping to the left (to delete).
            var moveFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START

            // Disable dragging on no-draggable list items.
            val recipeViewHolder = viewHolder as RecipeViewHolder
            if (!recipeViewHolder.item!!.isDraggable) {
                moveFlags = 0
            }

            return makeMovementFlags(moveFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // Notify the adapter of the change.
            onMoveItem(viewHolder.adapterPosition, target.adapterPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // Notify the adapter of the removal.
            onRemoveItem(viewHolder.adapterPosition)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView

            background.bounds = Rect(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.color = Color.RED
            background.draw(c)

            // Calculate position of delete icon
            val itemHeight = itemView.bottom - itemView.top
            val intrinsicWidth = (deleteIcon!!.intrinsicWidth * 1.5).toInt()
            val intrinsicHeight = (deleteIcon!!.intrinsicHeight * 1.5).toInt()
            val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
            val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
            val deleteIconRight = itemView.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + intrinsicHeight

            // Draw the delete icon
            deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            deleteIcon.draw(c)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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
        var item: RecipeListItem? = null

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

        private fun loadImageFromAssets(recipe: Recipe) = CoroutineScope(Dispatchers.Main).launch {
            // Load the image in an IO thread.
            val task = async(Dispatchers.IO) {
                return@async recipe.loadBitmap(context)
            }

            // Set the bitmap in the UI thread.
            val bitmap = task.await()
            image.setImageBitmap(bitmap)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun populate(item: RecipeListItem) {
            this.item = item

            val recipe = item.recipeWithIngredients!!.recipe
            val ingredients = item.recipeWithIngredients!!.ingredientsWithQty.map { i -> i.ingredient.name }

            // Set text area's
            heading.text = recipe.name
            subHeading.text = recipe.type
            caption.text = ingredients.joinToString()

            // Load the image of the recipe
            loadImageFromAssets(recipe)

            if (item.viewType == RecipeListItem.VIEW_TYPE_ITEM)
                view.setOnClickListener { itemClickListener(recipe, image) }

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
        Log.i(TAG, "Item reordering occurred from $fromPosition to $toPosition")

        // Don't allow an item move to above the first header.
        if (toPosition == 0 && items[fromPosition].viewType == RecipeListItem.VIEW_TYPE_ITEM)
            return

        // Swap the data
        Collections.swap(items, fromPosition, toPosition)

        // Notify the system so the animation occurs.
        notifyItemMoved(fromPosition, toPosition)

        itemOrderChangedListener?.invoke()
    }

    fun onRemoveItem(position: Int) {
        // Remove from the list
        val newList = items.toMutableList()
        val item = newList.removeAt(position)

        // Update the list
        items = newList
        notifyItemRemoved(position)

        // Notify others of the removal
        onItemRemoved?.invoke(item.recipeWithIngredients!!.recipe)
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
                inflater.inflate(R.layout.list_card_recipe_item, parent, false))
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

