package com.example.storyapp.data.local.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.storyapp.data.contract.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl private constructor(
    private val dataStore: DataStore<Preferences>
): UserPreferencesRepository {

    override fun getLoginSession(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_SESSION] ?: false
        }
    }

    override suspend fun saveLoginSession(loginSession: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOGIN_SESSION] = loginSession
        }
    }

    override fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }


    override suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    override fun getName(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[NAME] ?: ""
        }
    }


    override suspend fun saveName(name: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = name
        }
    }

    override suspend fun clearDataLogin() {
        dataStore.edit { preferences ->
            preferences.remove(LOGIN_SESSION)
            preferences.remove(TOKEN)
            preferences.remove(NAME)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferencesRepositoryImpl? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferencesRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferencesRepositoryImpl(dataStore)
                INSTANCE = instance
                instance
            }
        }

        private val LOGIN_SESSION = booleanPreferencesKey("login_session")
        private val TOKEN = stringPreferencesKey("token")
        private val NAME = stringPreferencesKey("name")

    }
}