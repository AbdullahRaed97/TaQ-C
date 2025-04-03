package com.example.taq_c.favourite.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taq_c.data.model.City
import com.example.taq_c.data.repository.IWeatherRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavouriteViewModelTest {
    lateinit var viewModel: FavouriteViewModel
    lateinit var repo: IWeatherRepository
    @Before
    fun setup(){
        repo= DummyRepo()
        viewModel= FavouriteViewModel(repo)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertFavCity_City_string()= runTest {

        val city = City(
            name = "Rome"
        )

        val messages = mutableListOf<String?>()

        val job = launch {
            viewModel.message.collect { messages.add(it) }
        }

        delay(1000)
        viewModel.insertFavCity(city)

        advanceUntilIdle()
        job.cancel()

        assertEquals("Insertion Success", messages.firstOrNull())
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteFavCity_string()=runTest {
        val city = City(
            name = "Rome"
        )
        viewModel.insertFavCity(city)

        val messages = mutableListOf<String?>()

        val job = launch {
            viewModel.message.collect { messages.add(it) }
        }

        delay(1000)
        viewModel.deleteFavCity(city)

        advanceUntilIdle()
        job.cancel()

        assertEquals("Deletion Success", messages.first())
    }
}