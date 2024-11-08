package com.wahyusembiring.data.model.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wahyusembiring.data.model.OfficeHour
import java.util.UUID

@Entity(
    tableName = "lecturer"
)
data class Lecturer(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),

    val photo: Uri?,

    val name: String,

    val phone: List<String>,

    val email: List<String>,

    val address: List<String>,

    val officeHour: List<OfficeHour>,

    val website: List<String>,
)