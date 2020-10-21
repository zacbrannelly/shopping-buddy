package com.zacbrannelly.shoppingbuddy.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileInputStream
import java.util.*

@Entity(tableName = "recipes")
@Parcelize
data class Recipe(
    @PrimaryKey @ColumnInfo(name = "id") val id: UUID,
    var name: String,
    var type: String,
    var image: String,
    @ColumnInfo(name = "is_image_asset") var isImageAsset: Boolean
) : Parcelable {
    suspend fun loadBitmap(context: Context): Bitmap? = coroutineScope {
        var loadedImage: Bitmap? = null

        if (isImageAsset) {
            // Load the image from the assets folder
            context.assets.open(image).use { inputStream ->
                loadedImage = BitmapFactory.decodeStream(inputStream)
            }
        } else {
            // Load the image from the app's files directory
            FileInputStream(File(context.filesDir, image)).also {
                loadedImage = BitmapFactory.decodeStream(it)
            }.close()
        }

        Log.i("Recipe", "Successfully loaded image from path: $image")

        return@coroutineScope loadedImage
    }
}