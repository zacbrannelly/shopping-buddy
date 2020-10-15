package com.zacbrannelly.shoppingbuddy.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*

@Entity(
    tableName = "recipe_ingredients",
    primaryKeys = ["recipe_id", "ingredient_id"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecipeIngredient(
    @ColumnInfo(name = "recipe_id") val recipeId: UUID,
    @ColumnInfo(name = "ingredient_id") val ingredientId: UUID,
    val qty: Double
)