package com.example.taq_c.utilities

import androidx.room.TypeConverter
import com.example.taq_c.data.model.Coordinates

class TypeConverter {

    @TypeConverter
    fun fromCoordinates(coordinates: Coordinates?): String? {
        return "${coordinates?.lat},${coordinates?.lon}"
    }

    @TypeConverter
    fun toCoordinates(str: String?): Coordinates? {
        if (str == null)
            return null
        val coordinates = str.split(",")
        if (coordinates.size != 2) {
            throw IllegalArgumentException("Invalid Coordinates String values : $coordinates")
        }
        val lat = coordinates.get(0).toDoubleOrNull()
        val lon = coordinates.get(1).toDoubleOrNull()
        if (lat == null || lon == null) {
            throw IllegalArgumentException("Invalid Coordinates values : $coordinates")
        }
        return Coordinates(lat = lat, lon = lon)
    }
}