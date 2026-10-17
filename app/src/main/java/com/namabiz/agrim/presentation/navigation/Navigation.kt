package com.namabiz.agrim.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.namabiz.agrim.presentation.theme.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Discover : Screen("discover")
    object Rfq : Screen("rfq")
    object Compare : Screen("compare")
    object Account : Screen("account")
    object SupplierDetail : Screen("supplier/{slug}") {
        fun createRoute(slug: String) = "supplier/$slug"
    }
    object Scanner : Screen("scanner")
    object PdfViewer : Screen("pdf_viewer/{docId}") {
        fun createRoute(docId: Int) = "pdf_viewer/$docId"
    }
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Screen.Home.route, Icons.Filled.Storefront, Icons.Outlined.Storefront),
    BottomNavItem("Discover", Screen.Discover.route, Icons.Filled.Explore, Icons.Outlined.Explore),
    BottomNavItem("RFQ", Screen.Rfq.route, Icons.Filled.RequestQuote, Icons.Outlined.RequestQuote),
    BottomNavItem("Compare", Screen.Compare.route, Icons.AutoMirrored.Filled.CompareArrows, Icons.AutoMirrored.Outlined.CompareArrows),
    BottomNavItem("Account", Screen.Account.route, Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
)

@Composable
fun MainScaffold(
    navController: NavHostController,
    isOffline: Boolean = false,
    content: @Composable (Modifier) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = SurfaceCard,
                    tonalElevation = 4.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandGreen,
                                selectedTextColor = BrandGreen,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Reactive Offline Alert Banner
            AnimatedVisibility(
                visible = isOffline,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Surface(
                    color = AmberBadgeBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = AmberBadgeText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Offline Mode · Serving from Room SQLite Cache",
                            color = AmberBadgeText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            content(Modifier.fillMaxSize())
        }
    }
}
