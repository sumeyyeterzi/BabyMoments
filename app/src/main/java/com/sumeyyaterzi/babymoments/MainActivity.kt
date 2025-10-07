package com.sumeyyaterzi.babymoments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sumeyyaterzi.babymoments.screens.AddMomentScreen
import com.sumeyyaterzi.babymoments.screens.TimelineScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val vm: MomentsViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application))

            NavHost(navController = navController, startDestination = "timeline") {
                composable("timeline") { TimelineScreen(navController, vm) }
                composable("add") { AddMomentScreen(navController, vm) }
            }
        }
    }
}
