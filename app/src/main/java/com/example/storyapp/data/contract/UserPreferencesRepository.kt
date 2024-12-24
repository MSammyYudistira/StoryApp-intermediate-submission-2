package com.example.storyapp.data.contract

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getLoginSession(): Flow<Boolean>
    suspend fun saveLoginSession(loginSession: Boolean)

    fun getToken(): Flow<String>
    suspend fun saveToken(token: String)

    fun getName(): Flow<String>
    suspend fun saveName(name: String)

    suspend fun clearDataLogin()
}
