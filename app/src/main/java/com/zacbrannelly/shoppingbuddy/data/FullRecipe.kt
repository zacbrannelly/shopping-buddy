package com.zacbrannelly.shoppingbuddy.data

import androidx.room.Embedded
import androidx.room.Relation

data class FullRecipe(
    @Embedded
    val recipe: Recipe,

    @Relation(parentColumn = "id", entityColumn = "recipe_id", entity = RecipeIngredient::class)
    val ingredients: List<IngredientWithQty>,

    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    val steps: List<Step>
)