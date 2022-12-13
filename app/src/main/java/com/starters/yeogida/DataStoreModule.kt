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

    private val USER_ACCESS_TOKEN_KEY = stringPreferencesKey("USER_ACCESS_TOKEN")
    private val USER_REFRESH_TOKEN_KEY = stringPreferencesKey("USER_REFRESH_TOKEN")
    private val USER_BEARER_TOKEN_KEY = stringPreferencesKey("USER_BEARER_TOKEN")
    private val USER_IS_LOGIN_KEY = booleanPreferencesKey("USER_IS_LOGIN")
    private val IMAGE_PERMISSION_IS_REJECTED_KEY =
        booleanPreferencesKey("IS_IMAGE_PERMISSION_REJECTED")

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

    val userBearerToken: Flow<String> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { userPrefs ->
            userPrefs[USER_BEARER_TOKEN_KEY] ?: ""
        }

    val userIsLogin: Flow<Boolean> = context.userDataStore.data
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

    val imagePermissionIsRejected: Flow<Boolean> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { userPrefs ->
            userPrefs[IMAGE_PERMISSION_IS_REJECTED_KEY] ?: false
        }

    private suspend fun saveAccessToken(accessToken: String) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_ACCESS_TOKEN_KEY] = accessToken
        }
    }

    private suspend fun removeAccessToken() {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_ACCESS_TOKEN_KEY] = ""
        }
    }

    private suspend fun saveRefreshToken(refreshToken: String) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    private suspend fun removeRefreshToken() {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_REFRESH_TOKEN_KEY] = ""
        }
    }

    private suspend fun saveBearerToken(accessToken: String) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_BEARER_TOKEN_KEY] = "Bearer $accessToken"
        }
    }

    private suspend fun removeBearerToken() {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_BEARER_TOKEN_KEY] = ""
        }
    }

    suspend fun saveIsLogin(userIsLogin: Boolean) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_IS_LOGIN_KEY] = userIsLogin
        }
    }

    suspend fun saveIsImgPermissionRejected(isRejected: Boolean) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[IMAGE_PERMISSION_IS_REJECTED_KEY] = isRejected
        }
    }

    suspend fun saveUserToken(accessToken: String?, refreshToken: String?) {
        saveAccessToken(accessToken ?: "")
        saveBearerToken(accessToken ?: "")
        saveRefreshToken(refreshToken ?: "")
    }

    suspend fun removeUserToken() {
        removeAccessToken()
        removeBearerToken()
        removeRefreshToken()
    }
}