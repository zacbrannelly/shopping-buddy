package com.zacbrannelly.shoppingbuddy.data

import androidx.room.Embedded
import com.zacbrannelly.shoppingbuddy.data.Ingredient

data class ShoppingListItem(
    @Embedded
    val ingredient: Ingredient,
    val qty: Double,
    var isChecked: Boolean = false
)