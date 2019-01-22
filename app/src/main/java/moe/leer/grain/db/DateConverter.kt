package moe.leer.grain.db

import androidx.room.TypeConverter
import java.util.*

/**
 *
 * Created by leer on 1/22/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class DateConverter {
    val TAG = "DateConverter"
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}