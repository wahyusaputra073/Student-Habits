package com.wahyusembiring.data.model.entity

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val color: Color,

    val room: String,

    @ColumnInfo("lecturer_id")
    val lecturerId: Int,

    val description: String,
)