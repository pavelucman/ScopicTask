package com.pavel.scopictask.data.network.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pavel.scopictask.data.network.model.Auth
import kotlinx.coroutines.flow.map

class DataStoreManager(val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "AUTH")

    companion object {

        val PASSWORD = stringPreferencesKey("PASSWORD")
        val AUTH_EMAIL = booleanPreferencesKey("AUTH_EMAIL")

    }

    suspend fun savetoDataStore(password: String, authEmail: Boolean) {
        context.dataStore.edit {
            it[PASSWORD] = password
            it[AUTH_EMAIL] = authEmail
        }
    }

    val authPreference = context.dataStore.data.map {
        Auth(
            it[PASSWORD] ?: "", it[AUTH_EMAIL] ?: false
        )
    }
}