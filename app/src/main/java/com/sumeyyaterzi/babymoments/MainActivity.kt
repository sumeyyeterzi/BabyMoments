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
import com.sumeyyaterzi.babymoments.screens.EditBabyProfileScreen
import com.sumeyyaterzi.babymoments.screens.EditMomentScreen
import com.sumeyyaterzi.babymoments.screens.MainScreen
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
        startDestination = "main"
    ) {
        // Ana Ekran (Bottom Navigation ile)
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                parentNavController = navController  // ← Parent NavController'ı geç
            )
        }

        // Yeni Anı Ekle (Full screen - Bottom Nav yok)
        composable("add") {
            AddMomentScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Anı Düzenle (Full screen - Bottom Nav yok)
        composable(
            route = "edit/{momentId}",
            arguments = listOf(
                navArgument("momentId") {
                    type = NavType.LongType
                }
            )
        )

        { backStackEntry ->
            val momentId = backStackEntry.arguments?.getLong("momentId")
            if (momentId != null) {
                EditMomentScreen(
                    navController = navController,
                    viewModel = viewModel,
                    momentId = momentId
                )
            }
        }
        composable("edit_profile") {
            EditBabyProfileScreen(navController = navController)
        }
    }
}