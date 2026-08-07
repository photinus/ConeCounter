package com.conecounter.app.data

import kotlinx.coroutines.flow.Flow

class CounterRepository(
    private val kidDao: KidDao,
    private val scoopDao: ScoopDao,
    private val tripPreferences: TripPreferences
) {
    val kids: Flow<List<Kid>> = kidDao.getAllKids()
    val scoops: Flow<List<Scoop>> = scoopDao.getAllScoops()
    val tripInfo: Flow<TripInfo> = tripPreferences.tripInfo

    suspend fun addKid(name: String, emoji: String, colorHex: String, dailyGoal: Int): Long =
        kidDao.insert(Kid(name = name, emoji = emoji, colorHex = colorHex, dailyGoal = dailyGoal))

    suspend fun updateKid(kid: Kid) = kidDao.update(kid)

    suspend fun deleteKid(kid: Kid) = kidDao.delete(kid)

    suspend fun logScoop(kidId: Long, flavor: String): Scoop {
        val scoop = Scoop(kidId = kidId, flavor = flavor)
        val id = scoopDao.insert(scoop)
        return scoop.copy(id = id)
    }

    suspend fun quickLogScoop(kidId: Long): Scoop {
        val lastFlavor = scoopDao.getLastScoopForKid(kidId)?.flavor ?: COMMON_FLAVORS.first()
        return logScoop(kidId, lastFlavor)
    }

    suspend fun getKid(kidId: Long): Kid? = kidDao.getKidById(kidId)

    suspend fun deleteScoop(scoop: Scoop) = scoopDao.delete(scoop)

    suspend fun setTripName(name: String) = tripPreferences.setTripName(name)

    suspend fun setStartDate(epochDay: Long) = tripPreferences.setStartDate(epochDay)

    suspend fun setFamilyGoalOverride(goal: Int?) = tripPreferences.setFamilyGoalOverride(goal)

    suspend fun ensureStartDateInitialized() = tripPreferences.ensureStartDateInitialized()
}
