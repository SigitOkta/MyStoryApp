package com.dwarf.mystoryapp.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){
    private val token = stringPreferencesKey("token")

    fun getToken(): Flow<String> {
        return dataStore.data.map{
            it[token] ?: ""
        }
    }

    suspend fun saveToken(token: String){
        dataStore.edit {
            it[this.token] = token
        }
    }

    suspend fun deleteToken() {
        dataStore.edit {
            it[token] = ""
        }
    }

    companion object {
        @Volatile
        private var instance: UserPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences =
            instance ?: synchronized(this) {
                instance ?: UserPreferences(dataStore)
            }.also { instance = it }
    }
}