package com.conecounter.app

import android.app.Application
import com.conecounter.app.data.CounterRepository
import com.conecounter.app.data.ConeCounterDatabase
import com.conecounter.app.data.TripPreferences

class ConeCounterApp : Application() {

    val repository: CounterRepository by lazy {
        val db = ConeCounterDatabase.getInstance(this)
        CounterRepository(db.kidDao(), db.scoopDao(), TripPreferences(this))
    }
}
