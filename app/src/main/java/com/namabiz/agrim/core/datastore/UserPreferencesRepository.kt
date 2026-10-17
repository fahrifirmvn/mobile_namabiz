package com.namabiz.agrim.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class UserSession(
    val token: String,
    val id: Int,
    val name: String,
    val email: String,
    val tier: String
)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "agrim_user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = intPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val SUBSCRIPTION_TIER = stringPreferencesKey("subscription_tier")
        val PREMIUM_EXPIRES_AT = stringPreferencesKey("premium_expires_at")
        val AVATAR_URL = stringPreferencesKey("avatar_url")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { it[Keys.AUTH_TOKEN] }
    val subscriptionTier: Flow<String> = context.dataStore.data.map { it[Keys.SUBSCRIPTION_TIER] ?: "free" }
    val userName: Flow<String> = context.dataStore.data.map { it[Keys.USER_NAME] ?: "John Doe" }
    val userEmail: Flow<String> = context.dataStore.data.map { it[Keys.USER_EMAIL] ?: "john.doe@example.com" }

    val userSessionFlow: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        val token = prefs[Keys.AUTH_TOKEN]
        if (token.isNullOrBlank()) null
        else UserSession(
            token = token,
            id = prefs[Keys.USER_ID] ?: 1,
            name = prefs[Keys.USER_NAME] ?: "Buyer",
            email = prefs[Keys.USER_EMAIL] ?: "buyer@agrim.com",
            tier = prefs[Keys.SUBSCRIPTION_TIER] ?: "free"
        )
    }

    suspend fun saveAuthSession(
        token: String,
        id: Int,
        name: String,
        email: String,
        tier: String,
        expiresAt: String? = null,
        avatar: String? = null
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AUTH_TOKEN] = token
            prefs[Keys.USER_ID] = id
            prefs[Keys.USER_NAME] = name
            prefs[Keys.USER_EMAIL] = email
            prefs[Keys.SUBSCRIPTION_TIER] = tier
            if (expiresAt != null) prefs[Keys.PREMIUM_EXPIRES_AT] = expiresAt
            if (avatar != null) prefs[Keys.AVATAR_URL] = avatar
        }
    }

    suspend fun updateSubscriptionTier(tier: String, expiresAt: String? = null) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SUBSCRIPTION_TIER] = tier
            if (expiresAt != null) prefs[Keys.PREMIUM_EXPIRES_AT] = expiresAt
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    suspend fun clearAuthSession() {
        clearSession()
    }
}

