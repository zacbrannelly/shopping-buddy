package com.zacbrannelly.shoppingbuddy.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FullRecipe(
    @Embedded
    val recipe: Recipe,

    @Relation(parentColumn = "id", entityColumn = "recipe_id", entity = RecipeIngredient::class)
    val ingredients: List<IngredientWithQty>,

    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    val steps: List<Step>
) : Parcelable