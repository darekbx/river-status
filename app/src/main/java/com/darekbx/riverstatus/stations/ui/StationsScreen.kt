package com.darekbx.riverstatus.stations.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        val stations by stationsViewModel.listStations().observeAsState()
        stations
            ?.let {
                var stations by remember { mutableStateOf(it) }
                Column {
                    Button(modifier = Modifier.padding(8.dp), onClick = { openStationClick(152210170) }) {
                        Text("Open WARSZAWA-BULWARY")
                    }

                    FilterBox(Modifier.fillMaxWidth()) { query ->
                        stations = stations.filter { it.name.lowercase().contains(query) }
                    }
                    StationsList(stations = stations) {
                        openStationClick(it.id)
                    }
                }
            }
            ?: run { Progress() }
    }
}

@Preview
@Composable
private fun FilterBox(
    modifier: Modifier = Modifier,
    onFilter: (query: String) -> Unit = { }
) {
    var text by remember { mutableStateOf("") }
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = {
            text = it.lowercase()
            onFilter(text)
        },
        label = { Text("Filter...") }
    )
}

@Composable
private fun StationsList(
    modifier: Modifier = Modifier,
    stations: List<Station>,
    onClick: (Station) -> Unit
) {
    LazyColumn(modifier) {
        items(stations) { station ->
            StationItem(
                modifier = Modifier
                    .padding(top = 8.dp, start = 18.dp, end = 8.dp)
                    .fillMaxWidth()
                    .clickable { onClick(station) },
                station = station
            )
        }
    }
}

@Preview
@Composable
private fun StationItem(
    modifier: Modifier = Modifier,
    station: Station = Station(1, "WARSZAWA-BULWARY", 0.0, 0.0, "p")
) {
    Row(modifier) {
        Text(text = station.name)
    }
}