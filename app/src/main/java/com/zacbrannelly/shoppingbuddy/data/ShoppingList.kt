package com.zacbrannelly.shoppingbuddy.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "shopping_list")
data class ShoppingList(
    @PrimaryKey
    val ingredientId: UUID,
    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean
)