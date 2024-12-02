package com.wahyusembiring.data.model

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
import java.time.LocalTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@Serializable
data class OfficeHour(
   val day: Int,

   @Serializable(with = LocalTimeSerializer::class)
   @SerialName("start_time")
   val startTime: LocalTime,

   @Serializable(with = LocalTimeSerializer::class)
   @SerialName("end_time")
   val endTime: LocalTime

)

object LocalTimeSerializer : KSerializer<LocalTime> {
   override val descriptor: SerialDescriptor
      get() = PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.LONG)

   override fun serialize(encoder: Encoder, value: LocalTime) {
      encoder.encodeInt(value.toSecondOfDay())
   }

   override fun deserialize(decoder: Decoder): LocalTime {
      return LocalTime.ofSecondOfDay(decoder.decodeInt().toLong())
   }
}