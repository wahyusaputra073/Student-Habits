package com.wahyusembiring.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Duration
import java.time.LocalDate
import java.util.UUID
import kotlin.time.Duration.Companion.days

@Entity(
    tableName = "task",
    foreignKeys = [
        ForeignKey(
            entity = Thesis::class,
            parentColumns = ["id"],
            childColumns = ["thesis_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)

@Serializable
data class Task(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),

    @SerialName("thesis_id")
    @ColumnInfo(name = "thesis_id")
    val thesisId: String,

    val name: String,

    @SerialName("is_completed")
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @SerialName("due_date")
    @Serializable(with = LocalDateSerializer::class)
    @ColumnInfo(name = "due_date")
    val dueDate: LocalDate
)

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeLong(value.toEpochDay().days.inWholeMilliseconds)
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.ofEpochDay(Duration.ofMillis(decoder.decodeLong()).toDays())
    }
}