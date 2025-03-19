package com.example.simplenotas.data.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title: String,
    var content: String,
    var category: String = "",
    var isFormatted: Boolean = false,
    var createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var reminderDate: Date? = null,
    var isProtected: Boolean = false,
    var backgroundColor: Long? = null
)