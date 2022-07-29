package com.darekbx.riverstatus.stations.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.riverstatus.commonui.ErrorBox
import com.darekbx.riverstatus.commonui.Progress
import com.darekbx.riverstatus.model.Station
import com.darekbx.riverstatus.stations.viewmodel.StationsViewModel

@Composable
fun StationsScreen(
    stationsViewModel: StationsViewModel = hiltViewModel(),
    openStationClick: (stationId: Long) -> Unit
) {
    val state by stationsViewModel.state

    if (state.hasError) {
        ErrorBox(state.errorMessage!!)
    } else {
        val stationsWrapper by stationsViewModel.listStations().observeAsState()
        stationsWrapper
            ?.let { StationsList(stations = it.byStations) }
            ?: run { Progress() }
    }

    Column() {
        Text("StationsScreen")
        Button(onClick = { openStationClick(152210170) }) {
            Text("Open station")
        }
    }

}

@Composable
private fun StationsList(modifier: Modifier = Modifier, stations: List<Station>) {
    LazyColumn(modifier) {
        items(stations) { station ->
            StationItem(station = station)
        }
    }
}

@Preview
@Composable
private fun StationItem(
    modifier: Modifier = Modifier,
    station: Station = Station(1, "WARSZAWA-BULWARY", 0.0, 0.0, "p")
) {
    Text(text = station.name)
}