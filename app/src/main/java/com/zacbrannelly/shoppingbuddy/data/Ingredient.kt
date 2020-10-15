package com.zacbrannelly.shoppingbuddy.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey val id: UUID,
    val name: String,
    val units: String
)