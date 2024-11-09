package com.firman.dicodingevent.ui.ui.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val THEME_KEY = booleanPreferencesKey("theme_setting")
    private val NOTIFICATION_KEY = booleanPreferencesKey("notification_setting")

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        CoroutineScope(coroutineContext).launch {
            dataStore.edit { preferences ->
                preferences[THEME_KEY] = isDarkModeActive
            }
        }
    }

    fun getNotificationSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[NOTIFICATION_KEY] ?: false
        }
    }

    fun saveNotificationSetting(isNotificationActive: Boolean) {
        CoroutineScope(coroutineContext).launch {
            dataStore.edit { preferences ->
                preferences[NOTIFICATION_KEY] = isNotificationActive
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}

