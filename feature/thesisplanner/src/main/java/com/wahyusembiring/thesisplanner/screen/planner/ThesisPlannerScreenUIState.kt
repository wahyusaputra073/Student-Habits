package com.wahyusembiring.thesisplanner.screen.planner

import android.net.Uri
import com.wahyusembiring.data.model.File
import com.wahyusembiring.data.model.ThesisWithTask
import com.wahyusembiring.data.model.entity.Task
import com.wahyusembiring.data.model.entity.Thesis

data class ThesisPlannerScreenUIState(
    val thesis: ThesisWithTask? = null,
    val thesisTitle: String = "",
    val editedThesisTitle: String = "",
    val articles: List<File> = emptyList(),
    val tasks: List<Task> = emptyList(),

    //popup
    val showDatePicker: Boolean = false,
    val showCreateTaskDialog: Boolean = false,
    val articlePendingDelete: File? = null,
    val taskPendingDelete: Task? = null,
)