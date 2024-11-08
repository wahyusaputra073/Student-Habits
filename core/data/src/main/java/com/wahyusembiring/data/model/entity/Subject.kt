package com.wahyusembiring.data.model.entity

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "subject",
    foreignKeys = [
        ForeignKey(
            entity = Lecturer::class,
            parentColumns = ["id"],
            childColumns = ["lecturer_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)

data class Subject(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),

    val name: String,

    val color: Color,

    val room: String,

    @ColumnInfo("lecturer_id")
    val lecturerId: String,

    val description: String,
)