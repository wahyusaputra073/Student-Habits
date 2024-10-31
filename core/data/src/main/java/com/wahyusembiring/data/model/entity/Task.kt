package com.wahyusembiring.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Date

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
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerialName("thesis_id")
    @ColumnInfo(name = "thesis_id")
    val thesisId: Int,

    val name: String,

    @SerialName("is_completed")
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @SerialName("due_date")
    @Serializable(with = DateSerializer::class)
    @ColumnInfo(name = "due_date")
    val dueDate: Date
)

object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeLong(value.time)
    }

    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeLong())
    }
}