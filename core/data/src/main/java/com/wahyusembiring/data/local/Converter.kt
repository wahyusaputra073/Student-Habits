package com.wahyusembiring.data.local

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.File
import com.wahyusembiring.data.model.entity.ExamCategory
import com.wahyusembiring.data.model.OfficeHour
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.data.util.toAttachment
import com.wahyusembiring.data.util.toFile
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

@ProvidedTypeConverter
class Converter(
    private val appContext: Context
) {

    @TypeConverter
    fun uriToString(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun stringToUri(string: String): Uri {
        return Uri.parse(string)
    }

    @TypeConverter
    fun dateToLong(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun longToDate(long: Long): Date {
        return Date(long)
    }

    @TypeConverter
    fun timeToString(time: Time): String {
        return "${time.hour}:${time.minute}"
    }

    @TypeConverter
    fun stringToTime(string: String): Time {
        val (hour, minute) = string.split(":")
        return Time(hour.toInt(), minute.toInt())
    }

    @TypeConverter
    fun colorToInt(color: Color): Int {
        return color.toArgb()
    }

    @TypeConverter
    fun intToColor(int: Int): Color {
        return Color(int)
    }

    @TypeConverter
    fun listOfStringToJsonString(listOfString: List<String>): String {
        return Json.encodeToString(listOfString)
    }

    @TypeConverter
    fun jsonStringToListOfString(jsonString: String): List<String> {
        return Json.decodeFromString(jsonString)
    }

    @TypeConverter
    fun listOfUriToJsonString(listOfUri: List<Uri>): String {
        val uris = listOfUri.map { it.toString() }
        return Json.encodeToString(uris)
    }

    @TypeConverter
    fun jsonStringToListOfUri(jsonString: String): List<Uri> {
        val uris = Json.decodeFromString<List<String>>(jsonString)
        return uris.map { Uri.parse(it) }
    }

    @TypeConverter
    fun listOfAttachmentToJsonString(listOfAttachment: List<Attachment>): String {
        val listOfUri = listOfAttachment.map { it.uri }
        return listOfUriToJsonString(listOfUri)
    }

    @TypeConverter
    fun jsonStringToListOfAttachment(jsonString: String): List<Attachment> {
        val uris = jsonStringToListOfUri(jsonString)
        return uris.map { it.toAttachment(appContext) }
    }

    @TypeConverter
    fun listOfFileToJsonString(listOfFile: List<File>): String {
        val listOfUri = listOfFile.map { it.uri }
        return listOfUriToJsonString(listOfUri)
    }

    @TypeConverter
    fun jsonStringToListOfFile(jsonString: String): List<File> {
        val uris = jsonStringToListOfUri(jsonString)
        return uris.map { it.toFile(appContext) }
    }

    @TypeConverter
    fun examCategoryToString(category: ExamCategory): String {
        return category.name
    }

    @TypeConverter
    fun stringToExamCategory(string: String): ExamCategory {
        return ExamCategory.valueOf(string)
    }

    @TypeConverter
    fun listOfOfficeHourToJsonString(listOfOfficeHour: List<OfficeHour>): String {
        return Json.encodeToString(listOfOfficeHour)
    }

    @TypeConverter
    fun jsonStringToListOfOfficeHour(jsonString: String): List<OfficeHour> {
        return Json.decodeFromString(jsonString)
    }

}