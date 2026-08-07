package com.conecounter.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.tripDataStore by preferencesDataStore(name = "trip_prefs")

data class TripInfo(
    val tripName: String,
    val startEpochDay: Long,
    val familyGoalOverride: Int?
)

class TripPreferences(private val context: Context) {

    private val nameKey = stringPreferencesKey("trip_name")
    private val startDayKey = longPreferencesKey("trip_start_epoch_day")
    private val familyGoalKey = intPreferencesKey("family_goal_override")

    val tripInfo = context.tripDataStore.data.map { prefs ->
        TripInfo(
            tripName = prefs[nameKey] ?: "Our Cruise Adventure",
            startEpochDay = prefs[startDayKey] ?: LocalDate.now().toEpochDay(),
            familyGoalOverride = prefs[familyGoalKey]
        )
    }

    suspend fun setTripName(name: String) {
        context.tripDataStore.edit { it[nameKey] = name }
    }

    suspend fun setStartDate(epochDay: Long) {
        context.tripDataStore.edit { it[startDayKey] = epochDay }
    }

    suspend fun setFamilyGoalOverride(goal: Int?) {
        context.tripDataStore.edit {
            if (goal == null) it.remove(familyGoalKey) else it[familyGoalKey] = goal
        }
    }

    suspend fun ensureStartDateInitialized() {
        context.tripDataStore.edit {
            if (it[startDayKey] == null) {
                it[startDayKey] = LocalDate.now().toEpochDay()
            }
        }
    }
}
