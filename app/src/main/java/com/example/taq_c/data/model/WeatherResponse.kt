package com.example.taq_c.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(var coord: Coordinates? = null,
                           var weather: List<Weather>? = null,
                           @SerializedName("main")
                           var weatherDetails: WeatherDetails? = null,
                           var wind: Wind? = null,
                           var clouds: Clouds? = null,
                           var dt: Long= 0, //time when data is calculated
                           var sys: Sys?=null,
                           var timezone: Int = 0, //time zone offset in seconds
                           @SerializedName("name")
                           var cityName: String? = null)