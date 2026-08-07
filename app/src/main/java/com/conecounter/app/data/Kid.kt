package com.conecounter.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kids")
data class Kid(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val dailyGoal: Int = 3,
    val sortOrder: Long = System.currentTimeMillis()
)

val KID_AVATAR_EMOJIS = listOf("🧒", "👦", "👧", "🧑", "👶", "🐵", "🐶", "🦄", "🐱", "🦊")

val KID_AVATAR_COLORS = listOf(
    "#FFB74D", "#4FC3F7", "#81C784", "#F06292",
    "#BA68C8", "#FFD54F", "#4DB6AC", "#E57373"
)
