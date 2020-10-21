package com.zacbrannelly.shoppingbuddy.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey val id: UUID,
    val name: String,
    val units: String
): Parcelable