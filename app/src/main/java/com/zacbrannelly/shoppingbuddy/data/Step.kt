package com.zacbrannelly.shoppingbuddy.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*

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
    val description: String
)