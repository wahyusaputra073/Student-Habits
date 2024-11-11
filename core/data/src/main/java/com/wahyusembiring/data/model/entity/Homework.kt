package com.wahyusembiring.data.model.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.DeadlineTime
import com.wahyusembiring.data.model.Time
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "homework",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Homework(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),

    val title: String,

    @ColumnInfo(name = "due_date")
    val dueDate: LocalDate,

    val reminder: LocalTime?,

    val deadline: DeadlineTime?,

    @ColumnInfo(name = "subject_id")
    val subjectId: String,

    val completed: Boolean = false,

    val attachments: List<Attachment>,

    val description: String,

    val score: Int? = null
)