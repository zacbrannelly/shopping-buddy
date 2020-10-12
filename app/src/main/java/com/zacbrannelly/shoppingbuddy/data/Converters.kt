package com.zacbrannelly.shoppingbuddy.data

import android.net.Uri
import androidx.room.TypeConverter
import java.util.*

object Converters {
    @TypeConverter
    @JvmStatic
    fun uuidToString(uuid: UUID) = uuid.toString()

    @TypeConverter
    @JvmStatic
    fun stringToUUID(string: String) = UUID.fromString(string)

    @TypeConverter
    @JvmStatic
    fun uriToString(uri: Uri) = uri.path

    @TypeConverter
    @JvmStatic
    fun stringToUri(string: String) = Uri.parse(string)
}