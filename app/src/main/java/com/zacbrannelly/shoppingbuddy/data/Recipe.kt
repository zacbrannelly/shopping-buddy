package com.zacbrannelly.shoppingbuddy.data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val id: UUID,
    val name: String,
    val type: String,
    val image: String,
    @ColumnInfo(name = "is_image_asset") val isImageAsset: Boolean
)