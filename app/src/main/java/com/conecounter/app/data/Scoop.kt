package com.conecounter.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scoops",
    foreignKeys = [
        ForeignKey(
            entity = Kid::class,
            parentColumns = ["id"],
            childColumns = ["kidId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("kidId")]
)
data class Scoop(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val kidId: Long,
    val flavor: String,
    val timestamp: Long = System.currentTimeMillis()
)

val COMMON_FLAVORS = listOf(
    "Vanilla", "Chocolate", "Strawberry", "Mint Chip",
    "Cookie Dough", "Rocky Road", "Cookies & Cream", "Butter Pecan"
)
