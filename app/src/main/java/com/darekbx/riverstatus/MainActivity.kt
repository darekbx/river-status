package com.darekbx.riverstatus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.darekbx.riverstatus.navigation.NavigationItem
import com.darekbx.riverstatus.commonui.theme.RiverStatusTheme
import com.darekbx.riverstatus.demo.IntroScreen
import com.darekbx.riverstatus.stations.ui.StationsScreen
import com.darekbx.riverstatus.waterlevel.ui.WaterlevelScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RiverStatusTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation(navController)
                }
            }
        }
    }

    @Composable
    fun Navigation(navController: NavHostController) {
        NavHost(navController, startDestination = NavigationItem.Stations.route) {
            composable(NavigationItem.Stations.route) {
                StationsScreen(
                    openStationClick = { stationId ->
                        val route = "${NavigationItem.WaterLevel.route}/$stationId"
                        navController.navigate(route)
                    },
                    openIntroClick = {
                        navController.navigate(NavigationItem.IntroScreen.route)
                    }
                )
            }
            composable(NavigationItem.IntroScreen.route) {
                IntroScreen()
            }
            composable(
                "${NavigationItem.WaterLevel.route}/{${NavigationItem.stationIdArg}}",
                listOf(navArgument(NavigationItem.stationIdArg) { type = NavType.LongType })
            ) {
                it.arguments?.getLong(NavigationItem.stationIdArg)?.let { stationId ->
                    WaterlevelScreen(stationId = stationId)
                }
            }
        }
    }
}
