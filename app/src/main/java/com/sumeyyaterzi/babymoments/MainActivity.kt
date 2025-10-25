package com.sumeyyaterzi.babymoments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sumeyyaterzi.babymoments.screens.AddMomentScreen
import com.sumeyyaterzi.babymoments.screens.EditMomentScreen
import com.sumeyyaterzi.babymoments.screens.TimelineScreen
import com.sumeyyaterzi.babymoments.ui.theme.BabyMomentsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MomentsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabyMomentsTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MomentsViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "timeline"
    ) {
        // Timeline (Ana Ekran)
        composable("timeline") {
            TimelineScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Yeni Anı Ekle
        composable("add") {
            AddMomentScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Anı Düzenle (ID parametreli)
        composable(
            route = "edit/{momentId}",
            arguments = listOf(
                navArgument("momentId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val momentId = backStackEntry.arguments?.getInt("momentId")
            if (momentId != null) {
                EditMomentScreen(
                    navController = navController,
                    viewModel = viewModel,
                    momentId = momentId
                )
            }
        }
    }
}