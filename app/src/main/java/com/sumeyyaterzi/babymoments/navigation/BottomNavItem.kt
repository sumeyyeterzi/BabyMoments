package com.sumeyyaterzi.babymoments.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Bottom Navigation Item'ları tanımla
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Ana Sayfa",
        icon = Icons.Default.Home
    )

    object Growth : BottomNavItem(
        route = "growth",
        title = "Gelişim",
        icon = Icons.Default.ThumbUp
    )

    object Baby : BottomNavItem(
        route = "baby",
        title = "Bebek",
        icon = Icons.Default.Person
    )
}

// Tüm bottom nav item'ları
val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Growth,
    BottomNavItem.Baby
)