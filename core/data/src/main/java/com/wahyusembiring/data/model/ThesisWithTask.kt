package com.wahyusembiring.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.wahyusembiring.data.model.entity.Task
import com.wahyusembiring.data.model.entity.Thesis

data class ThesisWithTask(
    @Embedded val thesis: Thesis,
    @Relation(
        parentColumn = "id",
        entityColumn = "thesis_id"
    )
    val tasks: List<Task>
)