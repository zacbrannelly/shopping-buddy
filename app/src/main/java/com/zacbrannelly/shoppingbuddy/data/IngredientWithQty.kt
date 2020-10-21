package com.zacbrannelly.shoppingbuddy.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IngredientWithQty(
    @Embedded
    val metadata: RecipeIngredient,

    @Relation(parentColumn = "ingredient_id", entityColumn = "id")
    val ingredient: Ingredient
): Parcelable