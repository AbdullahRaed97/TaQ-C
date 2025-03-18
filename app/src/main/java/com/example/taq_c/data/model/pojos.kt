package com.example.taq_c.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.taq_c.utilities.TypeConverter
import com.google.gson.annotations.SerializedName


data class Clouds(var all: Int = 0)

//This class is for the coordinates
data class Coord(var lon: Double = 0.0,
            var lat: Double = 0.0)


//This class is used to get the fully detailed weather characteristics
data class Main(var temp: Double = 0.0,
           var feels_like: Double = 0.0,
           var temp_min: Double = 0.0,
           var temp_max: Double = 0.0,
           var pressure: Int = 0,
           var humidity: Int = 0,
           var sea_level: Int? = 0,
           var grnd_level: Int? = 0,
           var temp_kf : Double? =0.0)

//This is the returned weather response from the API
data class WeatherResponse(var coord: Coord? = null,
           var weather: List<Weather>? = null,
           var base: String? = null,
           var main: Main? = null,
           var visibility: Int = 0,
           var wind: Wind? = null,
           var clouds: Clouds? = null,
           var dt: Long= 0,//time when data is calculated
           var sys: Sys? = null,
           var timezone: Int = 0,//time zone offset in seconds
           var id: Int = 0,//unique identifier for the city
           @SerializedName("name")
           var cityName: String? = null,//city name
           var cod: Int = 0,//HTTP response ode (200 = success)
           @SerializedName("list")
           var weatherForecastList :List<Forecast>?=null,
           var city:City?=null )


//This class is used to determine the system-related information
class Sys( var type: Int = 0,
           var id: Int = 0,
           var country: String? = null,
           var sunrise: Long = 0,
           var sunset: Long = 0,
           var pod :String?=null)//Part Of the Day ("d" for day, "n" for night)

//This class is used to describe the weather
data class Weather(var id: Int = 0,
                   var main: String? = null,//weather description
                   var description: String? = null,//full weather description
                   var icon: String? = null)//icon that show the weather condition (snow , sunny , clear...)


//This class is used to describe the wind speed and degree
data class Wind(var speed: Double = 0.0,
                var deg: Int = 0,
                var gust:Double=0.0)
data class Rain( @SerializedName("3h")
                 var _3h: Double = 0.0)

//This class represents the weather forecast for a specific 3-hour interval
data class Forecast( var dt: Long = 0,//TimeStamp
                 var main: Main? = null,
                 var weather: List<Weather>? = null,
                 var clouds: Clouds? = null,
                 var wind: Wind? = null,
                 var visibility: Int = 0,
                 var pop: Double = 0.0,
                 var sys: Sys? = null,
                 var dt_txt: String? = null,//Date and time in text format
                 var rain: Rain? = null)
@Entity(tableName = "Cities")
data class City (  @PrimaryKey
                   var id: Int =0,
                  var name: String?=null,
                  @TypeConverters(TypeConverter::class)
                  var coord: Coord? = null,
                  var country: String? = null,
                  @Ignore
                  var population: Int = 0,
                  var timezone: Int = 0,
                  @Ignore
                  var sunrise: Long = 0,
                  @Ignore
                  var sunset: Long = 0)






