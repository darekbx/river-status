package com.darekbx.riverstatus.repository

import com.darekbx.riverstatus.model.StationWrapper
import com.darekbx.riverstatus.model.StationsWrapper
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class ImgwRepository(
    private val httpClient: HttpClient,
    private val gson: Gson
    ): BaseRiverStatusRepository {

    override suspend fun fetchStationInfo(stationId: Long): StationWrapper {
        val response: HttpResponse = httpClient.get(STATION_ENDPOINT) {
            url {
                parameters.append("id", "$stationId")
            }
        }
        val body = response.readText()
        return gson.fromJson(body, StationWrapper::class.java)
    }

    override suspend fun listStations(): StationsWrapper {
        val response: HttpResponse = httpClient.get(STATIONS_ENDPOINT)
        val body = response.readText()
        return gson.fromJson(body, StationsWrapper::class.java)
    }

    companion object {
        private const val BASE_ENDPOINT = "https://hydro.imgw.pl/api"
        private const val STATIONS_ENDPOINT = "$BASE_ENDPOINT/map/stationsPrecip"
        private const val STATION_ENDPOINT = "$BASE_ENDPOINT/station/hydro/"
    }
}