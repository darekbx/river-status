package com.darekbx.riverstatus.stations.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.darekbx.riverstatus.model.Station
import com.darekbx.riverstatus.repository.BaseRiverStatusRepository
import com.darekbx.riverstatus.waterlevel.UIEvent
import com.darekbx.riverstatus.waterlevel.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StationsViewModel @Inject constructor(
    private val riverStatusRepository: BaseRiverStatusRepository
): ViewModel() {

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

    fun listStations(): LiveData<List<Station>> =
        liveData(context = viewModelScope.coroutineContext) {
            try {
                val data = riverStatusRepository
                    .listStations()
                    .byStations
                    .sortedBy { it.name }
                emit(data)
            } catch (e: Exception) {
                onEvent(UIEvent.Error(e.message ?: "Unknown error"))
            }
        }
}
