package com.example.taq_c.favourite.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.repository.WeatherRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavouriteViewModelTest {
    lateinit var viewModel: FavouriteViewModel
    lateinit var repo: WeatherRepository
    val city = City(name = "Rome")

    @Before
    fun setup() {
        repo = mockk()
        viewModel = FavouriteViewModel(repo)
    }

    @Test
    fun insertCity_city_sameCity() = runTest {
        viewModel.insertFavCity(city)
        val result = viewModel.favCitiesResponse.value
        if (result is Response.Success) {
            assertThat(result.data, `is`(city))
        }
        coVerify { repo.insertFavCity(city) }
    }

    @Test
    fun get5D_3HForeCastData_latLon_response() {
        viewModel.get5D_3HForeCastData(0.0, 0.0, "metric", "en")
        val result = viewModel.forecastResponse.value
        if (result is Response.Success) {
            assertThat(result.data, not(nullValue()))
        }
        coVerify { repo.get5D_3HForeCastData(0.0, 0.0, "metric", "en") }
    }
}