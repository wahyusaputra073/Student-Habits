package com.wahyusembiring.data.model.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.Time
import java.util.Date

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
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    @ColumnInfo(name = "due_date")
    val dueDate: Date,

    val reminder: Time?,

    @ColumnInfo(name = "subject_id")
    val subjectId: Int,

    val completed: Boolean = false,

    val attachments: List<Attachment>,

    val description: String,

    val score: Int? = null
)