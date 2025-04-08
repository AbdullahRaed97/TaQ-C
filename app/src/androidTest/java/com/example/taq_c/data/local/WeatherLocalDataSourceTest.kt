package com.example.taq_c.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.taq_c.data.db.AlertDao
import com.example.taq_c.data.db.ResponsesDao
import com.example.taq_c.data.db.WeatherDao
import com.example.taq_c.data.db.WeatherDatabase
import com.example.taq_c.data.model.Alert
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
class WeatherLocalDataSourceTest {
    private lateinit var database: WeatherDatabase
    private lateinit var alertDao: AlertDao
    private lateinit var weatherDao: WeatherDao
    private lateinit var responsesDao: ResponsesDao
    private lateinit var localDataSource: WeatherLocalDataSource

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        alertDao = database.getAlertDao()
        weatherDao = database.getWeatherDao()
        responsesDao = database.getResponsesDao()
        localDataSource = WeatherLocalDataSource.getInstance(weatherDao, alertDao,responsesDao)
    }

    @Test
    fun insertAlert_retrieveAlert() = runTest {
        val alert = Alert(
            requestCode = "1",
            city = City(
                name = "Cairo",
                coord = Coordinates(
                    lat = 0.0,
                    lon = 0.0
                )
            ),
            timeStamp = 10
        )
        localDataSource.insertAlert(alert)
        val result = localDataSource.getAllAlerts().first().get(0)
        assertThat(result.city, `is`(alert.city))
        assertThat(result.requestCode, `is`(alert.requestCode))
        assertThat(result.timeStamp, `is`(alert.timeStamp))
    }

    @Test
    fun deleteAlert_retrieveAlerts() = runTest {
        val alert = Alert(

            requestCode = "1",
            city = City(
                name = "Cairo",
                coord = Coordinates(
                    lat = 0.0,
                    lon = 0.0
                )
            ),
            timeStamp = 10
        )
        val alertList: MutableList<Alert> = mutableListOf()
        alertList.add(alert)
        localDataSource.insertAlert(alert)
        val result = localDataSource.deleteAlert(alert)
        val alertListResult = localDataSource.getAllAlerts().first()
        assertThat(alertListResult ,`is`(listOf()))
    }

    @After
    fun finalize() {
        database.close()
    }
}