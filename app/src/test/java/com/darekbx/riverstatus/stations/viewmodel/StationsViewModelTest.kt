package com.darekbx.riverstatus.stations.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.darekbx.riverstatus.getOrAwaitValue
import com.darekbx.riverstatus.repository.remote.ImgwRepository
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class StationsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Successull stations call`() = runTest {
        // Given
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{
    "byStations": [{
        "p": 0.0,
        "a": "b",
        "i": "250230070",
        "n": "TOMASZÓW LUBELSKI",
        "r": null,
        "lo": 23.3989,
        "la": 50.4581
    }, {
        "p": 0.0,
        "a": "b",
        "i": "250240010",
        "n": "STRZYŻÓW",
        "r": null,
        "lo": 24.0356,
        "la": 50.84
    }]
}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val testClient = HttpClient(mockEngine)
        val viewModel = StationsViewModel(ImgwRepository(testClient, Gson()))

        // When
        val result = viewModel.listStations().getOrAwaitValue()

        // Then
        assertEquals(2, result.byStations.size)
        with (result.byStations[0]) {
            assertEquals(250230070, id)
            assertEquals(23.3989, longitude, 0.01)
            assertEquals("TOMASZÓW LUBELSKI", name)
        }
    }
}