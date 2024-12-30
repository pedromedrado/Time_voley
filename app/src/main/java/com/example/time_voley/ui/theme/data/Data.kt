package com.example.time_voley.ui.theme.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore("teams_prefs")

// Chave para salvar os times
private val TEAMS_KEY = stringPreferencesKey("sorted_teams")

// Função para salvar os times
suspend fun saveTeams(context: Context, teams: List<List<String>>) {
    val teamsJson = Gson().toJson(teams)
    context.dataStore.edit { preferences ->
        preferences[TEAMS_KEY] = teamsJson
    }
}

// Função para recuperar os times
fun getSavedTeams(context: Context): Flow<List<List<String>>> {
    return context.dataStore.data.map { preferences ->
        val teamsJson = preferences[TEAMS_KEY] ?: "[]"
        Gson().fromJson(teamsJson, object : TypeToken<List<List<String>>>() {}.type)
    }
}