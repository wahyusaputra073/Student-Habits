package com.wahyusembiring.data.model.entity

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.DeadlineTime
import com.wahyusembiring.data.model.Time
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import java.util.UUID


@Entity(tableName = "reminder")
data class Reminder(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),

    val title: String,

    val date: LocalDate,

    val time: LocalTime,

    val times: DeadlineTime,

    val color: Color,

    val completed: Boolean = false,

    val attachments: List<Attachment>,

    val description: String,
)