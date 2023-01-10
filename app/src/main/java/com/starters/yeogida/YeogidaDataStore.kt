package com.starters.yeogida

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class YeogidaDataStore(private val context: Context) {
    val Context.userDataStore by preferencesDataStore(name = "user_prefs")

    private val USER_ACCESS_TOKEN_KEY = stringPreferencesKey("USER_ACCESS_TOKEN")
    private val USER_REFRESH_TOKEN_KEY = stringPreferencesKey("USER_REFRESH_TOKEN")
    private val USER_BEARER_TOKEN_KEY = stringPreferencesKey("USER_BEARER_TOKEN")
    private val USER_LOGIN_TYPE_KEY = stringPreferencesKey("USER_LOGIN_TYPE")

    private val USER_IS_LOGIN_KEY = booleanPreferencesKey("USER_IS_LOGIN")
    private val IMAGE_PERMISSION_IS_REJECTED_KEY =
        booleanPreferencesKey("IS_IMAGE_PERMISSION_REJECTED")

    private val MEMBER_ID = longPreferencesKey("MEMBER_ID")

    // 알림
    private val NOTIFICATION_IS_LIKE = booleanPreferencesKey("NOTIFICATION_IS_LIKE")
    private val NOTIFICATION_IS_FOLLOW = booleanPreferencesKey("NOTIFICATION_IS_FOLLOW")
    private val NOTIFICATION_IS_COMMENT = booleanPreferencesKey("NOTIFICATION_IS_COMMENT")

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

    val userLoginType: Flow<String> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { userPrefs ->
            userPrefs[USER_LOGIN_TYPE_KEY] ?: ""
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

    val memberId: Flow<Long> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { userPrefs ->
            userPrefs[MEMBER_ID] ?: 0
        }

    val notificationLikeIsAllow: Flow<Boolean> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { userPrefs ->
            userPrefs[NOTIFICATION_IS_LIKE] ?: true
        }

    val notificationFollowIsAllow: Flow<Boolean> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { userPrefs ->
            userPrefs[NOTIFICATION_IS_FOLLOW] ?: true
        }

    val notificationCommentIsAllow: Flow<Boolean> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { userPrefs ->
            userPrefs[NOTIFICATION_IS_COMMENT] ?: true
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

    suspend fun saveLoginType(loginType: String) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_LOGIN_TYPE_KEY] = loginType
        }
    }

    suspend fun removeLoginType() {
        context.userDataStore.edit { userPrefs ->
            userPrefs[USER_LOGIN_TYPE_KEY] = ""
        }
    }


    suspend fun saveMemberId(memberId: Long) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[MEMBER_ID] = memberId
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

    suspend fun saveNotificationLikeIsAllow(isAllow: Boolean) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[NOTIFICATION_IS_LIKE] = isAllow
        }
    }

    suspend fun saveNotificationFollowIsAllow(isAllow: Boolean) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[NOTIFICATION_IS_FOLLOW] = isAllow
        }
    }

    suspend fun saveNotificationCommentIsAllow(isAllow: Boolean) {
        context.userDataStore.edit { userPrefs ->
            userPrefs[NOTIFICATION_IS_COMMENT] = isAllow
        }
    }

    suspend fun saveUserToken(accessToken: String?, refreshToken: String?) {
        saveAccessToken(accessToken ?: "")
        saveBearerToken(accessToken ?: "")
        saveRefreshToken(refreshToken ?: "")
    }

    suspend fun saveMemberId(memberId: Long?) {
        saveMemberId(memberId ?: 0)
    }

    suspend fun removeMemberId() {
        saveMemberId(0)
    }

    suspend fun removeUserToken() {
        removeAccessToken()
        removeBearerToken()
        removeRefreshToken()
    }
}
