package com.wahyusembiring.data.model.entity

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wahyusembiring.data.model.File

@Entity(
    tableName = "thesis"
)
data class Thesis(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val articles: List<File>
)