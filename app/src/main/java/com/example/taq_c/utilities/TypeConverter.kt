package com.example.taq_c.utilities

import androidx.room.TypeConverter
import com.example.taq_c.data.model.Coord

class TypeConverter {

    @TypeConverter
    fun fromCoordinates(coordinates: Coord?):String?{
        return "${coordinates?.lat},${coordinates?.lon}"
    }
    @TypeConverter
    fun toCoordinates(str:String?): Coord? {
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
        return Coord(lat = lat, lon = lon)
    }
}