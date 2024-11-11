package com.wahyusembiring.data.model.entity

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.DeadlineTime
import com.wahyusembiring.data.model.Time
import java.util.Date


@Entity(tableName = "reminder")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val date: Date,

    val time: Time,

    val times: DeadlineTime,

    val color: Color,

    val completed: Boolean = false,

    val attachments: List<Attachment>,

    val description: String,
)