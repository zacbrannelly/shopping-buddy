package com.zacbrannelly.shoppingbuddy.ui.shoppinglist

import com.zacbrannelly.shoppingbuddy.data.Ingredient

data class ShoppingListItem(
    val ingredient: Ingredient,
    val qty: Double,
    var isChecked: Boolean = false
)