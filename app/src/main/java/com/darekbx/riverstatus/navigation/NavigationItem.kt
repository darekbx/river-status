package com.darekbx.riverstatus.navigation

sealed class NavigationItem(
    var route: String
) {
    object Stations: NavigationItem("stations")
    object WaterLevel: NavigationItem("water_level")
    object IntroScreen: NavigationItem("intro_screen")

    companion object {
        val stationIdArg = "station_id"
    }
}
