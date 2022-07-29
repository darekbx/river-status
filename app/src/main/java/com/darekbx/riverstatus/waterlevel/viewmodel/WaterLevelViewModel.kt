package com.darekbx.riverstatus.waterlevel.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.darekbx.riverstatus.model.StationWrapper
import com.darekbx.riverstatus.repository.BaseRiverStatusRepository
import com.darekbx.riverstatus.waterlevel.UIEvent
import com.darekbx.riverstatus.waterlevel.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WaterLevelViewModel @Inject constructor(
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

    fun getStationInfo(stationId: Long): LiveData<StationWrapper> =
        liveData(context = viewModelScope.coroutineContext) {
            try {
                val data = riverStatusRepository.fetchStationInfo(stationId)
                emit(data)
            } catch (e: Exception) {
                onEvent(UIEvent.Error(e.message ?: "Unknown error"))
            }
        }
}
