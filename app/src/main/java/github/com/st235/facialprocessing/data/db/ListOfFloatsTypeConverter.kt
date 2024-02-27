package github.com.st235.facialprocessing.data.db

import androidx.room.TypeConverter

class ListOfFloatsTypeConverter {

    @TypeConverter
    fun toListOfFloats(rawVector: String): List<Float> {
        return rawVector.split(",").map { it.trim().toFloat() }
    }

    @TypeConverter
    fun toString(vector: List<Float>): String {
        return vector.joinToString(separator = ",")
    }

}