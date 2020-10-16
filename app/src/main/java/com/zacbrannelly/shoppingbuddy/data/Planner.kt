package com.zacbrannelly.shoppingbuddy.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "planners",
    primaryKeys = ["day", "priority"],
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
data class Planner(
    val day: Day,
    @ColumnInfo(name = "recipe_id") val recipeId: UUID,
    val priority: Int
)