package com.zacbrannelly.shoppingbuddy.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithIngredients(
    @Embedded
    val recipe: Recipe,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipe_id",
        entity = RecipeIngredient::class
    )
    val ingredientsWithQty: List<IngredientWithQty>
)