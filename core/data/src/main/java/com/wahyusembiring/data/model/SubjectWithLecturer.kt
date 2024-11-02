package com.wahyusembiring.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.wahyusembiring.data.model.entity.Lecturer
import com.wahyusembiring.data.model.entity.Subject

data class SubjectWithLecturer(
    @Embedded val subject: Subject,
    @Relation(
        parentColumn = "lecturer_id",
        entityColumn = "id"
    )
    val lecturer: Lecturer
)
