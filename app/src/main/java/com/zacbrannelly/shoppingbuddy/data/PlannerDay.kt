package com.zacbrannelly.shoppingbuddy.data

data class PlannerDay(
    val day: Day,
    val recipes: List<RecipeWithIngredients>
)