package com.namabiz.agrim.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.namabiz.agrim.data.repository.AuthRepository
import com.namabiz.agrim.data.repository.RfqRepository
import com.namabiz.agrim.data.repository.SupplierRepository
import com.namabiz.agrim.presentation.account.AccountScreen
import com.namabiz.agrim.presentation.auth.LoginScreen
import com.namabiz.agrim.presentation.auth.LoginViewModel
import com.namabiz.agrim.presentation.compare.CompareScreen
import com.namabiz.agrim.presentation.detail.SupplierDetailScreen
import com.namabiz.agrim.presentation.discover.DiscoverScreen
import com.namabiz.agrim.presentation.home.HomeScreen
import com.namabiz.agrim.presentation.pdf.PdfViewerScreen
import com.namabiz.agrim.presentation.rfq.RfqScreen
import com.namabiz.agrim.presentation.rfq.RfqViewModel
import com.namabiz.agrim.presentation.scanner.QrScannerScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AgrimNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    supplierRepository: SupplierRepository,
    rfqRepository: RfqRepository,
    authRepository: AuthRepository,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val rfqViewModel: RfqViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = koinViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToScanner = {
                    navController.navigate(Screen.Scanner.route)
                },
                onNavigateToSupplier = { slug ->
                    navController.navigate(Screen.SupplierDetail.createRoute(slug))
                },
                onNavigateToRfq = {
                    navController.navigate(Screen.Rfq.route)
                },
                onNavigateToCompare = {
                    navController.navigate(Screen.Compare.route)
                },
                onNavigateToAccount = {
                    navController.navigate(Screen.Account.route)
                }
            )
        }

        composable(Screen.Discover.route) {
            DiscoverScreen(
                onNavigateToDetail = { slug ->
                    navController.navigate(Screen.SupplierDetail.createRoute(slug))
                },
                onNavigateToCompare = {
                    navController.navigate(Screen.Compare.route)
                }
            )
        }

        composable(Screen.Rfq.route) {
            RfqScreen(
                viewModel = rfqViewModel,
                onNavigateToSupplier = { slug ->
                    navController.navigate(Screen.SupplierDetail.createRoute(slug))
                }
            )
        }

        composable(Screen.Compare.route) {
            CompareScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRequestQuoteBoth = {
                    navController.navigate(Screen.Rfq.route)
                }
            )
        }

        composable(Screen.Account.route) {
            AccountScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignOut = {
                    coroutineScope.launch {
                        authRepository.logout()
                    }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.SupplierDetail.route,
            arguments = listOf(
                navArgument("slug") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val slug = backStackEntry.arguments?.getString("slug") ?: "agrim-pte-ltd"
            SupplierDetailScreen(
                slug = slug,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRequestQuote = {
                    navController.navigate(Screen.Rfq.route)
                }
            )
        }

        composable(Screen.Scanner.route) {
            QrScannerScreen(
                supplierRepository = supplierRepository,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSupplier = { slug ->
                    navController.navigate(Screen.SupplierDetail.createRoute(slug)) {
                        popUpTo(Screen.Scanner.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.PdfViewer.route,
            arguments = listOf(
                navArgument("docId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getInt("docId") ?: 1
            PdfViewerScreen(
                docId = docId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
