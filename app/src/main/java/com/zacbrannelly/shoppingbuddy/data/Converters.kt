package com.zacbrannelly.shoppingbuddy.data

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
    fun dayToString(day: Day) = day.toString()

    @TypeConverter
    @JvmStatic
    fun stringToDay(string: String) = Day.valueOf(string)
}