package com.namabiz.agrim.core.network

import android.content.Context
import com.namabiz.agrim.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

object SessionEventBus {
    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()

    fun emitSessionExpired() {
        _sessionExpiredEvent.tryEmit(Unit)
    }
}

class TokenAuthenticator(
    private val context: Context,
    private val prefsRepository: UserPreferencesRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loops if authentication itself failed on login
        if (response.request.url.encodedPath.contains("/login")) {
            return null
        }

        // If we received 401 Unauthorized, session token has expired
        if (response.code == 401) {
            runBlocking<Unit> {
                prefsRepository.clearAuthSession()
            }
            SessionEventBus.emitSessionExpired()
        }

        return null
    }
}
