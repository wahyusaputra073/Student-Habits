package com.wahyusembiring.data.model.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wahyusembiring.data.model.OfficeHour

@Entity(
    tableName = "lecturer"
)
data class Lecturer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val photo: Uri?,

    val name: String,

    val phone: List<String>,

    val email: List<String>,

    val address: List<String>,

    val officeHour: List<OfficeHour>,

    val website: List<String>,
)