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
        "notes" to notes,
        "due_date" to converter.localDateTimeToLong(dueDate),
        "deadline" to converter.localDateTimeToLong(deadline),
        "due_reminder" to dueReminder?.let { converter.localDateTimeToLong(it) },
        "deadline_reminder" to deadlineReminder?.let { converter.localDateTimeToLong(it) },
        "subject_id" to subjectId,
        "score" to score,
        "category" to converter.examCategoryToString(category)
    )
}

fun DocumentSnapshot.toExam(converter: Converter): Exam {
    return Exam(
        id = id,
        title = get("title", String::class.java)!!,
        notes = get("notes", String::class.java) ?: "",
        dueDate = get("due_date", Long::class.java)
            .let { converter.longToLocalDateTime(it!!) },
        deadline = get("deadline", Long::class.java)
            .let { converter.longToLocalDateTime(it!!) },
        dueReminder = get("due_reminder", Long::class.java)
            .let { converter.longToLocalDateTime(it!!) },
        deadlineReminder = get("deadline_reminder", Long::class.java)
            .let { converter.longToLocalDateTime(it!!) },
        subjectId = get("subject_id", String::class.java)!!,
        score = get("score", Int::class.java),
        category = get("category", String::class.java)
            .let { converter.stringToExamCategory(it!!) }
    )
}

fun Homework.toHashMap(converter: Converter): HashMap<String, *> {
    return hashMapOf(
        "title" to title,
        "notes" to notes,
        "due_date" to converter.localDateTimeToLong(dueDate),
        "deadline" to converter.localDateTimeToLong(deadline),
        "completed" to completed,
        "due_reminder" to dueReminder?.let { converter.localDateTimeToLong(it) },
        "deadline_reminder" to deadlineReminder?.let { converter.localDateTimeToLong(it) },
        "subject_id" to subjectId,
    )
}

fun DocumentSnapshot.toHomework(converter: Converter): Homework {
    return Homework(
        id = id,
        title = get("title", String::class.java)!!,
        notes = get("notes", String::class.java) ?: "",
        dueDate = get("due_date", Long::class.java)
            .let { converter.longToLocalDateTime(it!!) },
        deadline = get("deadline", Long::class.java)
            .let { converter.longToLocalDateTime(it!!) },
        completed = get("completed", Boolean::class.java) ?: false,
        dueReminder = get("due_reminder", Long::class.java)
            .let { converter.longToLocalDateTime(it!!) },
        deadlineReminder = get("deadline_reminder", Long::class.java)
            .let { converter.longToLocalDateTime(it!!) },
        subjectId = get("subject_id", String::class.java)!!,
    )
}

fun Reminder.toHashMap(converter: Converter): HashMap<String, *> {
    return hashMapOf(
        "title" to title,
        "notes" to notes,
        "reminder_dates" to reminderDates.map {
            converter.localDateTimeToLong(it)
        },
    )
}

@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toReminder(converter: Converter): Reminder {
    return Reminder(
        id = id,
        title = get("title", String::class.java)!!,
        notes = get("notes", String::class.java) ?: "",
        reminderDates = (get("reminder_dates") as? List<Long>)?.map {
            converter.longToLocalDateTime(it)
        } ?: emptyList()
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
        id = id,
        name = get("name", String::class.java)!!,
        color = get("color", Int::class.java).let { converter.intToColor(it!!) },
        room = get("room", String::class.java)!!,
        lecturerId = get("lecturer_id", String::class.java)!!,
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
        id = id,
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
                "due_date" to converter.localDateToLong(it.dueDate)
            )
        }
    )
}

@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toThesisWithTask(converter: Converter): ThesisWithTask {
    val thesis = Thesis(
        id = id,
        title = get("title", String::class.java)!!,
        articles = get("articles", String::class.java)
            .let { converter.jsonStringToListOfFile(it!!) }
    )
    val tasks = (get("tasks") as List<Map<String, Any>>).map { taskDto ->
        Task(
            id = taskDto["task_id"] as String,
            thesisId = taskDto["thesis_id"] as String,
            name = taskDto["name"] as String,
            isCompleted = taskDto["is_completed"] as Boolean,
            dueDate = converter.longToLocalDate(taskDto["due_date"] as Long)
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
        "due_date" to converter.localDateToLong(dueDate)
    )
}