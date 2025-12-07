package com.sumeyyaterzi.babymoments.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sumeyyaterzi.babymoments.MomentsViewModel
import com.sumeyyaterzi.babymoments.navigation.BottomNavItem
import com.sumeyyaterzi.babymoments.navigation.bottomNavItems

@Composable
fun MainScreen(
    viewModel: MomentsViewModel,
    parentNavController: NavHostController  // ← Parent NavController ekle
) {
    val navController = rememberNavController()
    val customPrimary = Color(0xFFF1A3A7)
    val customLight = Color(0xFFFDDDDD)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                customPrimary = customPrimary,
                customLight = customLight
            )
        }
    ) { paddingValues ->
        BottomNavGraph(
            navController = navController,
            parentNavController = parentNavController,  // ← Geçir
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    customPrimary: Color,
    customLight: Color
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Color.White,
        contentColor = customPrimary
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = customPrimary,
                    selectedTextColor = customPrimary,
                    indicatorColor = customLight,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    parentNavController: NavHostController,  // ← Parent NavController ekle
    viewModel: MomentsViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) {
            TimelineScreen(
                navController = parentNavController,  // ← Parent kullan
                viewModel = viewModel
            )
        }

        composable(BottomNavItem.Growth.route) {
            GrowthScreen(
                navController = parentNavController,  // ← Parent kullan
                viewModel = viewModel
            )
        }

        composable(BottomNavItem.Baby.route) {
            BabyProfileScreen(navController = parentNavController)  // ← Parent kullan
        }
    }
}