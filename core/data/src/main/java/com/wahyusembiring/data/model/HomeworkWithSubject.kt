package com.wahyusembiring.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Subject

data class HomeworkWithSubject(
    @Embedded val homework: Homework,
    @Relation(
        parentColumn = "subject_id",
        entityColumn = "id"
    )
    val subject: Subject
)
