package com.conecounter.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.conecounter.app.ConeCounterApp
import com.conecounter.app.data.COMMON_FLAVORS
import com.conecounter.app.data.CounterRepository
import com.conecounter.app.data.Kid
import com.conecounter.app.data.Scoop
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

data class ScoopWithKid(val scoop: Scoop, val kid: Kid)

data class KidStats(
    val kid: Kid,
    val scoopsToday: Int,
    val scoopsTotal: Int,
    val favoriteFlavor: String?
)

data class DayCount(val label: String, val count: Int, val isToday: Boolean)

data class AppUiState(
    val loading: Boolean = true,
    val tripName: String = "Our Cruise Adventure",
    val cruiseDay: Int = 1,
    val kidStats: List<KidStats> = emptyList(),
    val familyScoopsToday: Int = 0,
    val familyGoal: Int = 0,
    val totalScoopsAllTime: Int = 0,
    val topFlavorAllTime: String? = null,
    val weeklyCounts: List<DayCount> = emptyList(),
    val recentScoops: List<ScoopWithKid> = emptyList(),
    val familyGoalOverride: Int? = null
)

class CounterViewModel(private val repository: CounterRepository) : ViewModel() {

    init {
        viewModelScope.launch { repository.ensureStartDateInitialized() }
    }

    val uiState: StateFlow<AppUiState> = combine(
        repository.kids,
        repository.scoops,
        repository.tripInfo
    ) { kids, scoops, trip ->
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startOfToday = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val startOfTomorrow = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val scoopsByKid = scoops.groupBy { it.kidId }

        val kidStats = kids.map { kid ->
            val kidScoops = scoopsByKid[kid.id].orEmpty()
            val todayCount = kidScoops.count { it.timestamp in startOfToday until startOfTomorrow }
            val favorite = kidScoops.groupingBy { it.flavor }.eachCount().maxByOrNull { it.value }?.key
            KidStats(kid, todayCount, kidScoops.size, favorite)
        }

        val familyGoal = trip.familyGoalOverride ?: kids.sumOf { it.dailyGoal }
        val familyScoopsToday = scoops.count { it.timestamp in startOfToday until startOfTomorrow }
        val topFlavor = scoops.groupingBy { it.flavor }.eachCount().maxByOrNull { it.value }?.key
        val cruiseDay = (today.toEpochDay() - trip.startEpochDay).toInt() + 1

        val kidsById = kids.associateBy { it.id }
        val weeklyCounts = (6 downTo 0).map { offset ->
            val day = today.minusDays(offset.toLong())
            val dayStart = day.atStartOfDay(zone).toInstant().toEpochMilli()
            val dayEnd = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            val count = scoops.count { it.timestamp in dayStart until dayEnd }
            val label = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            DayCount(label, count, day == today)
        }
        val recentScoops = scoops.sortedByDescending { it.timestamp }
            .take(30)
            .mapNotNull { s -> kidsById[s.kidId]?.let { ScoopWithKid(s, it) } }

        AppUiState(
            loading = false,
            tripName = trip.tripName,
            cruiseDay = maxOf(cruiseDay, 1),
            kidStats = kidStats,
            familyScoopsToday = familyScoopsToday,
            familyGoal = familyGoal,
            totalScoopsAllTime = scoops.size,
            topFlavorAllTime = topFlavor,
            weeklyCounts = weeklyCounts,
            recentScoops = recentScoops,
            familyGoalOverride = trip.familyGoalOverride
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUiState())

    fun addKid(name: String, emoji: String, colorHex: String, dailyGoal: Int) {
        viewModelScope.launch { repository.addKid(name, emoji, colorHex, dailyGoal) }
    }

    fun updateKid(kid: Kid) {
        viewModelScope.launch { repository.updateKid(kid) }
    }

    fun deleteKid(kid: Kid) {
        viewModelScope.launch { repository.deleteKid(kid) }
    }

    fun logScoop(kidId: Long, flavor: String) {
        viewModelScope.launch { repository.logScoop(kidId, flavor.ifBlank { COMMON_FLAVORS.first() }) }
    }

    fun deleteScoop(scoop: Scoop) {
        viewModelScope.launch { repository.deleteScoop(scoop) }
    }

    fun setTripName(name: String) {
        viewModelScope.launch { repository.setTripName(name) }
    }

    fun setStartDate(date: LocalDate) {
        viewModelScope.launch { repository.setStartDate(date.toEpochDay()) }
    }

    fun setFamilyGoalOverride(goal: Int?) {
        viewModelScope.launch { repository.setFamilyGoalOverride(goal) }
    }

    /** Logs a scoop instantly for a home-screen shortcut tap. Returns the kid name + flavor logged, or null if the kid no longer exists. */
    suspend fun quickLog(kidId: Long): Pair<String, String>? {
        val kid = repository.getKid(kidId) ?: return null
        val scoop = repository.quickLogScoop(kidId)
        return kid.name to scoop.flavor
    }

    companion object {
        fun factory(app: ConeCounterApp): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CounterViewModel(app.repository) as T
                }
            }
    }
}
