package com.example.taq_c.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.taq_c.utilities.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    var cloudPercentage: Int = 0)

data class Wind(
    @SerializedName("speed")
    var windSpeed: Double = 0.0,
    var deg: Int = 0)

data class Rain( @SerializedName("3h")
                 var rainInLast3Hours: Double = 0.0)

data class Forecast(
    var dt: Long = 0,//TimeStamp
    @SerializedName("main")
    var weatherDetails: WeatherDetails? = null,
    var weather: List<Weather>? = null,
    var clouds: Clouds? = null,
    var wind: Wind? = null,
    var visibility: Int = 0,
    var pop: Double = 0.0,
    var sys: Sys? = null,
    var dt_txt: String? = null,//Date and time in text format
    var rain: Rain? = null)

data class Coordinates(var lon: Double = 0.0,
                       var lat: Double = 0.0)


@Entity(tableName = "Cities")
data class City (  @PrimaryKey
                   var id: Int =0,
                   var name: String? =null,
                   @TypeConverters(TypeConverter::class)
                   var coord: Coordinates? = null,
                   var country: String = "",
                   @Ignore
                   var population: Int = 0,
                   var timezone: Int = 0,
                   @Ignore
                   var sunrise: Long = 0,
                   @Ignore
                   var sunset: Long = 0)


//This class is used to determine the system-related information
class Sys(
           var country: String? = null,
           var sunrise: Long = 0,
           var sunset: Long = 0,
           var pod :String?=null)//Part Of the Day ("d" for day, "n" for night)

//This is the returned Forecast response from the API
data class ForecastResponse(
    @SerializedName("list")
    val weatherForecastList: List<Forecast>?= null,
    val city: City? = null,
)

//This is the returned weather response from the API

data class Weather(
    @SerializedName("main")
    var shortWeatherDesc: String? = null,//Short Weather description
    @SerializedName("description")
    var fullWeatherDesc: String? = null,//full weather description
    @SerializedName("icon")
    var weatherIcon: String? = null)//icon that show the weather condition (snow , sunny , clear...)


data class WeatherDetails(
    var temp: Double = 0.0,
    var feels_like: Double = 0.0,
    var temp_min: Double = 0.0,
    var temp_max: Double = 0.0,
    var pressure: Int = 0,
    var humidity: Int = 0,
    var sea_level: Int? = 0,
    var grnd_level: Int? = 0,
    var temp_kf : Double? =0.0)

@Entity(tableName="Alert")
data class Alert(
    @PrimaryKey val requestCode: String ="",
    @Embedded("alert") val city:City,
    val timeStamp: Long? = 0
)
