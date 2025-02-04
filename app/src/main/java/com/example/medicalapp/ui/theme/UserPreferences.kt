package com.example.medicalapp.ui.theme

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore instance
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val EMAIL_KEY = stringPreferencesKey("email")
    private val PHONE_KEY = stringPreferencesKey("phone")

    // Save user data
    suspend fun saveUserData(username: String, email: String, phone: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
            preferences[EMAIL_KEY] = email
            preferences[PHONE_KEY] = phone

            Log.d("UserPreferences", "Data saved: Username=$username, Email=$email, Phone=$phone")

        }
    }

    // Read user data
    val userData: Flow<UserData> = context.dataStore.data.map { preferences ->
        val username = preferences[USERNAME_KEY] ?: ""
        val email = preferences[EMAIL_KEY] ?: ""
        val phone = preferences[PHONE_KEY] ?: ""

        Log.d("UserPreferences", "Data loaded: Username=$username, Email=$email, Phone=$phone")

        UserData(username, email, phone)
    }

}

// Data class for User
data class UserData(val username: String, val email: String, val phone: String)
