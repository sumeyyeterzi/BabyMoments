package com.sumeyyaterzi.babymoments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sumeyyaterzi.babymoments.screens.AddMomentScreen
import com.sumeyyaterzi.babymoments.screens.TimelineScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val vm: MomentsViewModel = hiltViewModel()

            NavHost(navController = navController, startDestination = "timeline") {
                composable("timeline") { TimelineScreen(navController, vm) }
                composable("add") { AddMomentScreen(navController, vm) }
            }
        }
    }
}