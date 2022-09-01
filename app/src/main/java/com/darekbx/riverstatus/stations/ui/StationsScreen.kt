package com.darekbx.riverstatus.stations.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.riverstatus.commonui.ErrorBox
import com.darekbx.riverstatus.commonui.Progress
import com.darekbx.riverstatus.model.Station
import com.darekbx.riverstatus.stations.viewmodel.StationsViewModel
import kotlinx.coroutines.delay

@Composable
fun StationsScreen(
    stationsViewModel: StationsViewModel = hiltViewModel(),
    openStationClick: (stationId: Long) -> Unit,
    openIntroClick: () -> Unit
) {
    val state by stationsViewModel.state

    if (state.hasError) {
        ErrorBox(state.errorMessage!!)
    } else {
        val filterState = remember { mutableStateOf("") }
        val stations by stationsViewModel.listStations().observeAsState()
        stations?.let {
            Box {
                var target by remember {
                    mutableStateOf<LayoutCoordinates?>(null)
                }

                Column {
                    Row {
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = { openStationClick(152210170) }) {
                            Text("Open WARSZAWA-BULWARY")
                        }
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = { openIntroClick() }) {
                            Text("Demo button")
                        }
                    }

                    FilterBox(Modifier.fillMaxWidth()) { query ->
                        filterState.value = query
                    }
                    StationsList(stations = it, filter = filterState) {
                        openStationClick(it.id)
                    }
                }

                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp)
                        .onGloballyPositioned { target = it },
                    elevation = FloatingActionButtonDefaults.elevation(6.dp),
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        Icons.Filled.Email,
                        contentDescription = "Email"
                    )
                }
                target?.let {
                    IntroShowCase(it)
                }
            }
        } ?: run { Progress() }
    }
}
@Composable
fun IntroShowCase(targetCords: LayoutCoordinates) {
    val targetRect = targetCords.boundsInRoot()
    val targetRadius = targetRect.maxDimension / 2f

    val animationSpec = infiniteRepeatable<Float>(
        animation = tween(3000, easing = FastOutLinearInEasing),
        repeatMode = RepeatMode.Restart,
    )
    val animatables = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    animatables.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * 1000L)
            animatable.animateTo(
                targetValue = 1f, animationSpec = animationSpec
            )
        }
    }

    val dys = animatables.map { it.value }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = 0.99f)
    ) {
        drawCircle(
            color = Color.Black,
            center = targetRect.center,
            radius = targetRadius * 3,
        )
        dys.forEach { dy ->
            drawCircle(
                color = Color.White,
                radius = targetRect.maxDimension * dy * 1.5f,
                center = targetRect.center,
                alpha = 1 - dy
            )
        }
        drawCircle(
            color = Color.White,
            radius = targetRadius,
            center = targetRect.center,
            blendMode = BlendMode.Clear
        )
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
    filter: MutableState<String>,
    onClick: (Station) -> Unit
) {
    val stationsFiltered = stations.filter { it.name.lowercase().contains(filter.value) }
    LazyColumn(modifier) {
        items(stationsFiltered) { station ->
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
    modifier: Modifier = Modifier.fillMaxWidth(),
    station: Station = Station(1, "WARSZAWA-BULWARY", 52.213141, 21.099211, "p")
) {
    Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                text = station.name
            )
            Text(
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                text = "%.4f, %.4f".format(station.latitude, station.longitude),
                style = TextStyle(fontSize = 10.sp),
                color = Color.White.copy(alpha = 0.65F)
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                modifier = Modifier.padding(end = 8.dp, top = 8.dp),
                text = "#${station.id}",
                color = Color.White.copy(alpha = 0.5F),
                style = TextStyle(fontSize = 9.sp)
            )
            Text(
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                text = "${station.a}",
                color = Color.White.copy(alpha = 0.65F),
                style = TextStyle(fontSize = 9.sp)
            )
        }
    }
}
