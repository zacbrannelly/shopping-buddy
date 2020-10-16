package com.zacbrannelly.shoppingbuddy.data

import androidx.room.Embedded
import androidx.room.Relation

data class PlannerWithRecipe (
    @Embedded
    val planner: Planner,

    @Relation(parentColumn = "recipe_id", entityColumn = "id", entity = Recipe::class)
    val recipe: RecipeWithIngredients
)