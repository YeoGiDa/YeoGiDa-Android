package com.starters.yeogida

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreModule(private val context: Context) {
    val Context.userDataStore by preferencesDataStore(name = "user_prefs")

    private val USER_EMAIL_KEY = stringPreferencesKey("USER_EMAIL")
    private val USER_ACCESS_TOKEN_KEY = stringPreferencesKey("USER_ACCESS_TOKEN")
    private val USER_REFRESH_TOKEN_KEY = stringPreferencesKey("USER_REFRESH_TOKEN")
    private val USER_IS_LOGIN_KEY = booleanPreferencesKey("USER_IS_LOGIN")

    val userEmail: Flow<String> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { userPrefs ->
            userPrefs[USER_EMAIL_KEY] ?: ""
        }

    val userAccessToken: Flow<String> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { userPrefs ->
            userPrefs[USER_ACCESS_TOKEN_KEY] ?: ""
        }

    val userRefreshToken: Flow<String> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { userPrefs ->
            userPrefs[USER_REFRESH_TOKEN_KEY] ?: ""
        }

    val userIsLogin : Flow<Boolean> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { userPrefs ->
            userPrefs[USER_IS_LOGIN_KEY] ?: false
        }

    suspend fun saveUserEmail( userEmail : String ) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_EMAIL_KEY] = userEmail
        }
    }

    suspend fun saveAccessToken(accessToken: String) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_ACCESS_TOKEN_KEY] = accessToken
        }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun saveIsLogin(userIsLogin: Boolean) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_IS_LOGIN_KEY] = userIsLogin
        }
    }
}