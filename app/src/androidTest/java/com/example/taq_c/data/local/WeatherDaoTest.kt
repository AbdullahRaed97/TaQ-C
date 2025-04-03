package com.example.taq_c.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.taq_c.data.db.AlertDao
import com.example.taq_c.data.db.WeatherDao
import com.example.taq_c.data.db.WeatherDatabase
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.Coordinates
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@SmallTest
@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var weatherDao: WeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        weatherDao = database.getWeatherDao()
    }

    @Test
    fun insertFavCity_retrieveFavCity()= runTest {
        val city = City(
                name = "Cairo",
                coord = Coordinates(
                    lat = 0.0,
                    lon = 0.0
                )
        )
        weatherDao.insertFavCity(city)
        val result = weatherDao.getAllFavCities().first().get(0)
        assertThat(result,`is`(city))
    }

    @Test
    fun getAllFavCity_cityList()=runTest {
        val cityList : MutableList<City> = mutableListOf()
        val city1 =City(
            id = 1,
            name = "Cairo",
            coord = Coordinates(
                lat = 0.0,
                lon = 0.0
            )
        )

        val city2 =City(
            id = 2,
        name = "Paris",
            coord = Coordinates(
                lat = 0.0,
                lon = 0.0
            )
        )

        val city3= City(
            id = 3,
        name = "London",
            coord = Coordinates(
                lat = 0.0,
                lon = 0.0
            )
        )

        cityList.add(city1)
        cityList.add(city2)
        cityList.add(city3)

        weatherDao.insertFavCity(city1)
        weatherDao.insertFavCity(city2)
        weatherDao.insertFavCity(city3)

        val result = weatherDao.getAllFavCities().first()
        println(result)
        println(cityList)
        assertThat(result as MutableList<City> , `is`(cityList))
    }

    @After
    fun finalize(){
        database.close()
    }
}