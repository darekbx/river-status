package com.darekbx.riverstatus.waterlevel.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.riverstatus.commonui.ErrorBox
import com.darekbx.riverstatus.commonui.Progress
import com.darekbx.riverstatus.model.StationWrapper
import com.darekbx.riverstatus.model.WaterStateRecord
import com.darekbx.riverstatus.waterlevel.viewmodel.WaterLevelViewModel

@Composable
fun WaterlevelScreen(
    waterLevelViewModel: WaterLevelViewModel = hiltViewModel(),
    stationId: Long
) {
    val state by waterLevelViewModel.state

    if (state.hasError) {
        ErrorBox(state.errorMessage!!)
    } else {
        val stationWrapper by waterLevelViewModel.getStationInfo(stationId).observeAsState()
        stationWrapper
            ?.let { WaterLevel(it) }
            ?: run { Progress() }
    }
}

@Composable
private fun WaterLevel(station: StationWrapper) {
    Column {
        StationDescription(station = station)
        WaterLevelChart(
            modifier = Modifier.padding(8.dp).fillMaxSize(),
            waterStateRecords = station.waterStateRecords.reversed()
        )
    }
}

@Composable
private fun WaterLevelChart(
    modifier: Modifier = Modifier,
    waterStateRecords: List<WaterStateRecord>
) {
    Canvas(modifier = modifier, onDraw = {
        val leftOffset = 50.dp.toPx()
        val itemsToSkip = 1
        val circleRadius = 3F
        val circleStroke = 1F
        val yScale = 1.1F

        val width = size.width - leftOffset
        val chunkWidth = width / (waterStateRecords.size - itemsToSkip)
        val maximum = waterStateRecords.maxOf { it.value }
        val minimum = waterStateRecords.minOf { it.value }
        val chunkHeightScale = (size.height / maximum) * yScale

        var previousLevel = waterStateRecords.first().value
        var x = 0F

        val paint = Paint().asFrameworkPaint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 24F
        }

        translate(left = leftOffset, top = (size.height - maximum) / 2F) {

            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "${maximum}cm",
                    8F - leftOffset,
                    8F,
                    paint
                )
                it.nativeCanvas.drawText(
                    "${minimum}cm",
                    8F - leftOffset,
                    (maximum - minimum) * chunkHeightScale + 8F,
                    paint
                )
            }

            // Maximum line
            drawLine(
                Color.Gray,
                Offset(-leftOffset / 2F, 0F),
                Offset(size.width, 0F)
            )
            // Minimum line
            drawLine(
                Color.Gray,
                Offset(-leftOffset / 2F, (maximum - minimum) * chunkHeightScale),
                Offset(size.width, (maximum - minimum) * chunkHeightScale)
            )

            for (record in waterStateRecords.drop(itemsToSkip)) {
                val firstPoint = Offset(x, (maximum - previousLevel) * chunkHeightScale)
                val secondPoint =
                    Offset(x + chunkWidth, (maximum - record.value) * chunkHeightScale)

                drawLine(Color.Red, firstPoint, secondPoint)
                drawCircle(Color.White, circleRadius, firstPoint)
                drawCircle(Color.White, circleRadius, secondPoint)
                drawCircle(
                    Color.Black,
                    circleRadius,
                    firstPoint,
                    style = Stroke(width = circleStroke)
                )
                drawCircle(
                    Color.Black,
                    circleRadius,
                    secondPoint,
                    style = Stroke(width = circleStroke)
                )

                x += chunkWidth
                previousLevel = record.value
            }
        }
    })
}

@Composable
fun StationDescription(modifier: Modifier = Modifier, station: StationWrapper) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(text = station.name, style = MaterialTheme.typography.h5)
        Text(text = station.state, color = Color.DarkGray, style = MaterialTheme.typography.h6)
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun StationDescriptionPreview() {
    StationDescription(station = StationWrapper("WARSZAWA-BULWARY", "low", emptyList()))
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun ChartPreview() {
    val data = listOf(
        WaterStateRecord("", 34, ""),
        WaterStateRecord("", 32, ""),
        WaterStateRecord("", 31, ""),
        WaterStateRecord("", 31, ""),
        WaterStateRecord("", 30, ""),
        WaterStateRecord("", 30, ""),
        WaterStateRecord("", 34, ""),
        WaterStateRecord("", 35, ""),
        WaterStateRecord("", 34, "")
    )
    WaterLevelChart(Modifier.padding(8.dp).width(300.dp).height(200.dp), data)
}