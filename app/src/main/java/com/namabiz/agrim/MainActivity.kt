package com.namabiz.agrim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.namabiz.agrim.core.datastore.UserPreferencesRepository
import com.namabiz.agrim.core.network.NetworkConnectivityObserver
import com.namabiz.agrim.core.network.NetworkStatus
import com.namabiz.agrim.core.network.SessionEventBus
import com.namabiz.agrim.core.worker.CatalogSyncWorker
import com.namabiz.agrim.data.repository.AuthRepository
import com.namabiz.agrim.data.repository.RfqRepository
import com.namabiz.agrim.data.repository.SupplierRepository
import com.namabiz.agrim.presentation.navigation.AgrimNavHost
import com.namabiz.agrim.presentation.navigation.MainScaffold
import com.namabiz.agrim.presentation.navigation.Screen
import com.namabiz.agrim.presentation.theme.AgrimTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val supplierRepository: SupplierRepository by inject()
    private val rfqRepository: RfqRepository by inject()
    private val authRepository: AuthRepository by inject()
    private val prefsRepository: UserPreferencesRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule periodic sync via WorkManager
        CatalogSyncWorker.schedulePeriodicSync(applicationContext)

        val connectivityObserver = NetworkConnectivityObserver(applicationContext)

        setContent {
            AgrimTheme {
                val navController = rememberNavController()

                // Observe Network Connectivity
                val networkStatus by connectivityObserver.observe()
                    .collectAsState(initial = NetworkStatus.Available)
                val isOffline = networkStatus != NetworkStatus.Available

                // Check initial session
                val userSession by prefsRepository.userSessionFlow.collectAsState(initial = null)
                val startDestination = if (userSession?.token.isNullOrBlank()) {
                    Screen.Login.route
                } else {
                    Screen.Home.route
                }

                // Listen for 401 Session Expired events
                LaunchedEffect(Unit) {
                    SessionEventBus.sessionExpiredEvent.collectLatest {
                        Toast.makeText(applicationContext, "Session expired. Please sign in again.", Toast.LENGTH_LONG).show()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                // Deep Link Handler (e.g. agrim://supplier/{slug})
                LaunchedEffect(intent) {
                    handleIncomingIntent(intent) { slug ->
                        navController.navigate(Screen.SupplierDetail.createRoute(slug))
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScaffold(
                        navController = navController,
                        isOffline = isOffline
                    ) { innerModifier ->
                        AgrimNavHost(
                            navController = navController,
                            startDestination = startDestination,
                            supplierRepository = supplierRepository,
                            rfqRepository = rfqRepository,
                            authRepository = authRepository,
                            modifier = innerModifier
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?, onDeepLinkSupplier: (String) -> Unit) {
        val uri = intent?.data ?: return
        if (uri.scheme == "agrim" && uri.host == "supplier") {
            val slug = uri.lastPathSegment
            if (!slug.isNullOrBlank()) {
                onDeepLinkSupplier(slug)
            }
        }
    }
}
