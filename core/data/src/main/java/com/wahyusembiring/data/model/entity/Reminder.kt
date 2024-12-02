package com.wahyusembiring.data.model.entity

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wahyusembiring.data.model.Attachment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import java.util.UUID


@Entity(tableName = "reminder")
data class Reminder(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),

    val title: String,

    @ColumnInfo("reminder_dates")
    val reminderDates: List<LocalDateTime>,

    val notes: String,
)