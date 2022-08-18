package com.darekbx.riverstatus.waterlevel.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.darekbx.riverstatus.repository.local.WaterLevelDao
import com.darekbx.riverstatus.repository.local.dtos.WaterLevelDto
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
import java.lang.IllegalStateException

@OptIn(ExperimentalCoroutinesApi::class)
internal class RiverStatusViewModelTest {

    private class MockDao: WaterLevelDao {

        private val entries = mutableListOf<WaterLevelDto>()

        override fun fetch(): List<WaterLevelDto> = entries

        override fun fetchLast(): WaterLevelDto? = entries.lastOrNull()

        override fun insert(warterLevelDtos: List<WaterLevelDto>) {
            println("Insert")
            entries.addAll(warterLevelDtos)
        }
    }

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
    fun `Successfull station call`() = runTest {
        // Given
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"name":"WARSZAWA-BULWARY","state":"low","waterStateRecords":[{"state":"low","date":"2022-07-28T19:00:00Z","value":30.0},{"state":"low","date":"2022-07-28T18:00:00Z","value":30.0}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val testClient = HttpClient(mockEngine)
        val viewModel = WaterLevelViewModel(ImgwRepository(testClient, Gson()), MockDao())

        // When
        val result = viewModel.getStationInfo(152210170L)

        // Then
        assertEquals("WARSZAWA-BULWARY", result.name)
        assertEquals(2, result.waterStateRecords.size)
        assertEquals(30, result.waterStateRecords[0].value)
    }

    @Test
    fun `Append entries`() = runTest {
        // Given
        var counter = 0
        val mockEngine = MockEngine {
            when (counter++) {
                0 -> {
                    respond(
                        content = ByteReadChannel("""{"name":"WARSZAWA-BULWARY","state":"low","waterStateRecords":[{"state":"low","date":"2022-07-28T19:00:00Z","value":30.0},{"state":"low","date":"2022-07-28T18:00:00Z","value":10.0}]}"""),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                1 -> {
                    respond(
                        content = ByteReadChannel("""{"name":"WARSZAWA-BULWARY","state":"low","waterStateRecords":[{"state":"low","date":"2022-07-28T20:00:00Z","value":20.0}]}"""),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> throw IllegalStateException("Unknown request!")
            }
        }
        val testClient = HttpClient(mockEngine)
        val viewModel = WaterLevelViewModel(ImgwRepository(testClient, Gson()), MockDao())

        // First insert
        with(viewModel.getStationInfo(152210170L)) {
            assertEquals("WARSZAWA-BULWARY", name)
            assertEquals(2, waterStateRecords.size)
            assertEquals(30, waterStateRecords.first().value)
        }

        // When
        with (viewModel.getStationInfo(152210170L)) {
            // Then
            assertEquals("WARSZAWA-BULWARY", name)
            assertEquals(3, waterStateRecords.size)
            assertEquals(20, waterStateRecords.last().value)
        }
    }
}