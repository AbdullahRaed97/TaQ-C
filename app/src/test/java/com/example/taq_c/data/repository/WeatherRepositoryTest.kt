package com.example.taq_c.data.repository

import com.example.taq_c.data.local.FakeWeatherLocalDataSource
import com.example.taq_c.data.model.City
import com.example.taq_c.data.remote.FakeWeatherRemoteDataSource
import org.junit.Before


class WeatherRepositoryTest {

    private lateinit var fakeWeatherLocalDataSource: FakeWeatherLocalDataSource
    private lateinit var fakeWeatherRemoteDataSource: FakeWeatherRemoteDataSource
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


    @Before
    fun setup(){
        fakeWeatherLocalDataSource = FakeWeatherLocalDataSource(localData)
        fakeWeatherRemoteDataSource = FakeWeatherRemoteDataSource()
       // weatherRepository = WeatherRepository(fakeWeatherLocalDataSource,fakeWeatherRemoteDataSource)
    }

}