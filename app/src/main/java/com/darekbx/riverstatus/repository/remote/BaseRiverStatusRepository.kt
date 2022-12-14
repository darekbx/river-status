package com.darekbx.riverstatus.repository.remote

import com.darekbx.riverstatus.model.StationWrapper
import com.darekbx.riverstatus.model.StationsWrapper

interface BaseRiverStatusRepository {

    suspend fun fetchStationInfo(stationId: Long): StationWrapper

    suspend fun listStations(): StationsWrapper
}