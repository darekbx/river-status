package com.darekbx.riverstatus.demo

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

class Item(val label: String, val isChecked: Boolean)

@Composable
fun IntroScreen() {
    var sliderValue by remember { mutableStateOf(0f) }
    val isBelowRange by remember {
      derivedStateOf { sliderValue < 20 }
    }
    
    Column(verticalArrangement = Arrangement.Center) {
        Slider(value = sliderValue, onValueChange = { sliderValue = it }, valueRange = 0f..100f)
        Text(
            text = "Slider at ${sliderValue.roundToInt()}",
            color = if (isBelowRange) Color.Red else Color.Black
        )
        Log.v("------------", "recompose")
    }
}
