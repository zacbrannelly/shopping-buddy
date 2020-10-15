package com.zacbrannelly.shoppingbuddy.data

import androidx.room.Embedded
import androidx.room.Relation

data class IngredientWithQty(
    @Embedded
    val metadata: RecipeIngredient,

    @Relation(parentColumn = "ingredient_id", entityColumn = "id")
    val ingredient: Ingredient
)