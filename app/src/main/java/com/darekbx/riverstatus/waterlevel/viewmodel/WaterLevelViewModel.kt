package com.darekbx.riverstatus.waterlevel.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.darekbx.riverstatus.model.StationWrapper
import com.darekbx.riverstatus.model.WaterStateRecord
import com.darekbx.riverstatus.repository.local.WaterLevelDao
import com.darekbx.riverstatus.repository.local.dtos.WaterLevelDto
import com.darekbx.riverstatus.repository.remote.BaseRiverStatusRepository
import com.darekbx.riverstatus.waterlevel.UIEvent
import com.darekbx.riverstatus.waterlevel.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class WaterLevelViewModel @Inject constructor(
    private val riverStatusRepository: BaseRiverStatusRepository,
    private val waterLevelDao: WaterLevelDao
) : ViewModel() {

    private val _state = mutableStateOf(UIState())
    val state: State<UIState> = _state

    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.Error ->
                _state.value = state.value.copy(
                    hasError = true,
                    errorMessage = event.message
                )
            is UIEvent.ClearErrors ->
                _state.value = state.value.copy(
                    hasError = false,
                    errorMessage = null
                )
        }
    }

    suspend fun getStationInfo(stationId: Long): StationWrapper {
        try {
            val data = riverStatusRepository.fetchStationInfo(stationId)
            Log.v(TAG, "Received ${data.waterStateRecords.size} records")
            val newRows = addNewEntries(data, stationId)
            val entries = loadEntries()
            return data.copy(waterStateRecords = entries)
                .apply { this.newRows = newRows }
        } catch (e: Exception) {
            onEvent(UIEvent.Error(e.message ?: "Unknown error"))
            throw e
        }
    }

    private fun loadEntries(): List<WaterStateRecord> {
        val entries = waterLevelDao
            .fetch()
            .map { WaterStateRecord("", it.value, it.date) }
        return entries
    }

    /**
     * @return How many rows were addded
     */
    private fun addNewEntries(
        data: StationWrapper,
        stationId: Long
    ): Int {
        val lastEntry = waterLevelDao.fetchLast()
        Log.v(TAG, "Last entry from ${lastEntry?.date}")
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

        val records = when (lastEntry) {
            null -> data.waterStateRecords
            else -> {
                val latestDate = LocalDateTime.parse(lastEntry.date, formatter)
                Log.v(TAG, "Append entries")
                data.waterStateRecords.filter {
                    val entryDate = LocalDateTime.parse(it.date, formatter)
                    entryDate > latestDate
                }
            }
        }
        if (records.isNotEmpty()) {
            Log.v(TAG, "Adding ${records.size} new records")
            val entries = records.map {
                WaterLevelDto(null, it.value, it.date, stationId)
            }
            waterLevelDao.insert(entries)
        } else {
            Log.v(TAG, "No new records, skip")
        }

        return records.size
    }

    companion object {
        private const val TAG = "WaterLevelViewModel"
    }
}
