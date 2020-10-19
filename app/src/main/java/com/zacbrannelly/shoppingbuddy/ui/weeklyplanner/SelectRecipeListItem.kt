package com.zacbrannelly.shoppingbuddy.ui.weeklyplanner

import com.zacbrannelly.shoppingbuddy.data.RecipeWithIngredients

data class SelectRecipeListItem(
    val recipe: RecipeWithIngredients,
    var isSelected: Boolean = false
)