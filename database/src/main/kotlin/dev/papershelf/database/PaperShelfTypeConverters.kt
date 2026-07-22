package dev.papershelf.database

import androidx.room.TypeConverter
import dev.papershelf.database.entity.BookFormatEntity

class PaperShelfTypeConverters {
    @TypeConverter
    fun bookFormatToString(value: BookFormatEntity): String = value.name

    @TypeConverter
    fun stringToBookFormat(value: String): BookFormatEntity = BookFormatEntity.valueOf(value)
}
