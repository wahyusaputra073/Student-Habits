package com.wahyusembiring.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.Subject

data class ExamWithSubject(
    @Embedded val exam: Exam,
    @Relation(
        parentColumn = "subject_id",
        entityColumn = "id"
    )
    val subject: Subject
)
