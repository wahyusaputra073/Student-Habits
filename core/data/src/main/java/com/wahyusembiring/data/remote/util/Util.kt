package com.wahyusembiring.data.remote.util

import com.google.firebase.firestore.DocumentSnapshot
import com.wahyusembiring.data.local.Converter
import com.wahyusembiring.data.model.ThesisWithTask
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Lecturer
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.model.entity.Task
import com.wahyusembiring.data.model.entity.Thesis

const val USER_COLLECTION_ID = "user"

fun Exam.toHashMap(converter: Converter): HashMap<String, *> {
    return hashMapOf(
        "title" to title,
        "description" to description,
        "due_date" to converter.dateToLong(date),
        "reminder" to reminder?.let { converter.timeToString(it) },
        "subject_id" to subjectId,
        "attachments" to converter.listOfAttachmentToJsonString(attachments),
        "score" to score,
        "category" to converter.examCategoryToString(category)
    )
}

fun DocumentSnapshot.toExam(converter: Converter): Exam {
    return Exam(
        id = id.toInt(),
        title = get("title", String::class.java)!!,
        description = get("description", String::class.java) ?: "",
        date = get("due_date", Long::class.java)
            .let { converter.longToDate(it!!) },
        reminder = get("reminder", String::class.java)
            .let { converter.stringToTime(it!!) },
        subjectId = get("subject_id", Int::class.java)!!,
        attachments = get("attachments", String::class.java)
            .let { converter.jsonStringToListOfAttachment(it!!) },
        score = get("score", Int::class.java),
        category = get("grade", String::class.java)
            .let { converter.stringToExamCategory(it!!) }
    )
}

fun Homework.toHashMap(converter: Converter): HashMap<String, *> {
    return hashMapOf(
        "title" to title,
        "description" to description,
        "due_date" to converter.dateToLong(dueDate),
        "completed" to completed,
        "reminder" to reminder?.let { converter.timeToString(it) },
        "subject_id" to subjectId,
        "attachments" to converter.listOfAttachmentToJsonString(attachments),
        "score" to score
    )
}

fun DocumentSnapshot.toHomework(converter: Converter): Homework {
    return Homework(
        id = id.toInt(),
        title = get("title", String::class.java)!!,
        description = get("description", String::class.java) ?: "",
        dueDate = get("due_date", Long::class.java)
            .let { converter.longToDate(it!!) },
        completed = get("completed", Boolean::class.java) ?: false,
        reminder = get("reminder", String::class.java)
            .let { converter.stringToTime(it!!) },
        subjectId = get("subject_id", Int::class.java)!!,
        attachments = get("attachments", String::class.java)
            .let { converter.jsonStringToListOfAttachment(it!!) },
        score = get("score", Int::class.java)
    )
}

fun Reminder.toHashMap(converter: Converter): HashMap<String, *> {
    return hashMapOf(
        "title" to title,
        "description" to description,
        "due_date" to converter.dateToLong(date),
        "reminder" to converter.timeToString(time),
        "color" to converter.colorToInt(color),
        "completed" to completed,
        "attachments" to converter.listOfAttachmentToJsonString(attachments)
    )
}

fun DocumentSnapshot.toReminder(converter: Converter): Reminder {
    return Reminder(
        id = id.toInt(),
        title = get("title", String::class.java)!!,
        description = get("description", String::class.java) ?: "",
        date = get("due_date", Long::class.java)
            .let { converter.longToDate(it!!) },
        time = get("reminder", String::class.java)
            .let { converter.stringToTime(it!!) },
        color = get("color", Int::class.java)
            .let { converter.intToColor(it!!) },
        completed = get("completed", Boolean::class.java) ?: false,
        attachments = get("attachments", String::class.java)
            .let { converter.jsonStringToListOfAttachment(it!!) }
    )
}

fun Subject.toHashMap(converter: Converter): HashMap<String, *> {
    return hashMapOf(
        "name" to name,
        "color" to converter.colorToInt(color),
        "room" to room,
        "lecturer_id" to lecturerId,
        "description" to description
    )
}

fun DocumentSnapshot.toSubject(converter: Converter): Subject {
    return Subject(
        id = id.toInt(),
        name = get("name", String::class.java)!!,
        color = get("color", Int::class.java).let { converter.intToColor(it!!) },
        room = get("room", String::class.java)!!,
        lecturerId = get("lecturer_id", Int::class.java)!!,
        description = get("description", String::class.java) ?: ""
    )
}

fun Lecturer.toHashMap(converter: Converter): HashMap<String, *> {
    return hashMapOf(
        "name" to name,
        "photo" to photo?.let { converter.uriToString(it) },
        "phone" to converter.listOfStringToJsonString(phone),
        "email" to converter.listOfStringToJsonString(email),
        "address" to converter.listOfStringToJsonString(address),
        "office_hour" to converter.listOfOfficeHourToJsonString(officeHour),
        "website" to converter.listOfStringToJsonString(website)
    )
}

fun DocumentSnapshot.toLecturer(converter: Converter): Lecturer {
    return Lecturer(
        id = id.toInt(),
        name = get("name", String::class.java)!!,
        photo = get("photo", String::class.java)
            ?.let { converter.stringToUri(it) },
        phone = get("phone", String::class.java)
            .let { converter.jsonStringToListOfString(it!!) },
        email = get("email", String::class.java)
            .let { converter.jsonStringToListOfString(it!!) },
        address = get("address", String::class.java)
            .let { converter.jsonStringToListOfString(it!!) },
        officeHour = get("office_hour", String::class.java)
            .let { converter.jsonStringToListOfOfficeHour(it!!) },
        website = get("website", String::class.java)
            .let { converter.jsonStringToListOfString(it!!) },
    )
}

fun Thesis.toHashMap(converter: Converter): HashMap<String, *> {
    return hashMapOf(
        "title" to title,
        "articles" to converter.listOfFileToJsonString(articles)
    )
}

fun ThesisWithTask.toHashMap(converter: Converter): HashMap<String, *> {
    return hashMapOf(
        "title" to thesis.title,
        "articles" to converter.listOfFileToJsonString(thesis.articles),
        "tasks" to tasks.map {
            mapOf(
                "task_id" to it.id,
                "thesis_id" to it.thesisId,
                "name" to it.name,
                "is_completed" to it.isCompleted,
                "due_date" to converter.dateToLong(it.dueDate)
            )
        }
    )
}

@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toThesisWithTask(converter: Converter): ThesisWithTask {
    val thesis = Thesis(
        id = id.toInt(),
        title = get("title", String::class.java)!!,
        articles = get("articles", String::class.java)
            .let { converter.jsonStringToListOfFile(it!!) }
    )
    val tasks = (get("tasks") as List<Map<String, Any>>).map { taskDto ->
        Task(
            id = (taskDto["task_id"] as Long).toInt(),
            thesisId = (taskDto["thesis_id"] as Long).toInt(),
            name = taskDto["name"] as String,
            isCompleted = taskDto["is_completed"] as Boolean,
            dueDate = converter.longToDate(taskDto["due_date"] as Long)
        )
    }
    return ThesisWithTask(thesis, tasks)
}

fun Task.toHashMap(converter: Converter): HashMap<String, *> {
    return hashMapOf(
        "task_id" to id,
        "thesis_id" to thesisId,
        "name" to name,
        "is_completed" to isCompleted,
        "due_date" to converter.dateToLong(dueDate)
    )
}

//fun DocumentSnapshot.toTask(converter: Converter): Task {
//    return Task(
//        id = id.toInt(),
//        thesisId = get("thesis_id", Int::class.java)!!,
//        name = get("name", String::class.java)!!,
//        isCompleted = get("is_completed", Boolean::class.java) ?: false,
//        dueDate = get("due_date", Long::class.java)
//            .let { converter.longToDate(it!!) }
//    )
//}