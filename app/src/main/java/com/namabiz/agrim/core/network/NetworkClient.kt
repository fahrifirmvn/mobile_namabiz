package com.namabiz.agrim.core.network

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.namabiz.agrim.core.datastore.UserPreferencesRepository
import com.namabiz.agrim.core.datastore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking {
            val key = stringPreferencesKey("auth_token")
            context.dataStore.data.first()[key]
        }

        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/json")

        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}

object NetworkClient {
    // 10.0.2.2 points to host machine from Android Emulator.
    // For local physical device testing, replace with machine LAN IP.
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    private var apiService: AgrimApiService? = null

    fun getService(context: Context): AgrimApiService {
        return apiService ?: synchronized(this) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val prefs = UserPreferencesRepository(context.applicationContext)
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context.applicationContext))
                .authenticator(TokenAuthenticator(context.applicationContext, prefs))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit.create(AgrimApiService::class.java).also { apiService = it }
        }
    }
}
