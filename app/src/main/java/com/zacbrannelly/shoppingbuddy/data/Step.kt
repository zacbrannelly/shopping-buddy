package com.zacbrannelly.shoppingbuddy.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(
    tableName = "steps",
    primaryKeys = ["recipe_id", "step"],
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
data class Step(
    @ColumnInfo(name = "recipe_id") val recipeId: UUID,
    val step: Int,
    var description: String
): Parcelable