package com.example.taq_c.data.repository

import com.example.taq_c.data.local.FakeWeatherLocalDataSource
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.Coordinates
import com.example.taq_c.data.model.WeatherResponse
import com.example.taq_c.data.remote.StubWeatherRemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test


class WeatherRepositoryTest {

    private lateinit var fakeWeatherLocalDataSource: FakeWeatherLocalDataSource
    private lateinit var fakeWeatherRemoteDataSource: StubWeatherRemoteDataSource
    private lateinit var weatherRepository : WeatherRepository

    val localData: MutableList<City> = mutableListOf(
        City(
           name = "Cairo"
        ), City(
            name = "Paris"
        ) , City(
            name = "London"
        ) , City(
            name = "Milano"
        )
    )
    val remoteData : WeatherResponse = WeatherResponse(
        coord = Coordinates(lon = 31.0, lat = 30.0),
        cityName = "Cairo"
    )

    @Before
    fun setup(){
        fakeWeatherLocalDataSource = FakeWeatherLocalDataSource(localData)
        fakeWeatherRemoteDataSource = StubWeatherRemoteDataSource(remoteData)
        weatherRepository = WeatherRepository.getInstance(fakeWeatherLocalDataSource,
            fakeWeatherRemoteDataSource)
    }

    @Test
    fun getCurrentWeather_weatherResponse_weatherResponse()= runTest{
        val result = weatherRepository.getCurrentWeatherData(0.0,0.0,"","").first()
        assertThat(remoteData,`is` (result))
    }

    @Test
    fun getAllFavCity_cityList_cityList()=runTest{
        val result = weatherRepository.getAllFavCities().first()
        assertThat(localData as List<City>,`is`(result))
    }

    @Test
    fun insertFavCity_City_one()=runTest{

        val city = City(
            name = "Rome"
        )

        localData.add(city)

        val result = weatherRepository.insertFavCity(city)
        val cityList = weatherRepository.getAllFavCities().first()

        assertThat(result,`is`(1))
        assertThat(cityList,`is`(localData))
    }
}