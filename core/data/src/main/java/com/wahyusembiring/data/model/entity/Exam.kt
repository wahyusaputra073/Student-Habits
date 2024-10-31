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
    tableName = "exam",
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
data class Exam(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val date: Date,

    val reminder: Time?,

    @ColumnInfo(name = "subject_id")
    val subjectId: Int,

    val category: ExamCategory,

    val score: Int? = null,

    val attachments: List<Attachment>,

    val description: String,
)

enum class ExamCategory {
    WRITTEN, ORAL, PRACTICAL
}