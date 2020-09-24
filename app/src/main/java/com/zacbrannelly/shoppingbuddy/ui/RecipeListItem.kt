package com.zacbrannelly.shoppingbuddy.ui

import com.zacbrannelly.shoppingbuddy.entity.Recipe

class RecipeListItem {
    var recipe: Recipe? = null
    var heading: String? = null
    var isDraggable: Boolean = false
    var viewType: Int

    constructor(recipe: Recipe, isDraggable: Boolean = false) {
        this.recipe = recipe
        this.viewType = VIEW_TYPE_ITEM
        this.isDraggable = isDraggable
    }
    constructor(heading: String) {
        this.heading = heading
        this.viewType = VIEW_TYPE_HEADER
    }

    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_ITEM = 2
    }
}