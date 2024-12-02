package com.wahyusembiring.common.util

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.NOTIFICATION_ID_EXTRA
import com.wahyusembiring.common.NOTIFICATION_TITLE_EXTRA
import com.wahyusembiring.common.NotificationBroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KMutableProperty0


/**
 * Create a single instance of an object and store it in [volatileInstanceProp] property.
 *
 * @param volatileInstanceProp property to store the created instance
 * @param initializer a lambda function to create the instance
 *
 * @return [T] the created instance or the existing instance if it already exists
 * */
fun <T> createSingleton(
    volatileInstanceProp: KMutableProperty0<T?>,
    initializer: () -> T
): T {
    return volatileInstanceProp.get() ?: synchronized(Any()) {
        volatileInstanceProp.get() ?: initializer().also {
            volatileInstanceProp.set(it)
        }
    }
}

fun String?.isNotNullOrBlank(): Boolean = this?.isNotBlank() ?: false

fun ViewModel.launch(block: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch(block = block)
}

fun Int.withZeroPadding(length: Int = 2): String = this.toString().padStart(length, '0')



fun getNotificationReminderPermission(): List<String> {
    val permissions = mutableListOf<String>()

    // Android 33 and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    // Android 31 and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
        permissions.add(Manifest.permission.SCHEDULE_EXACT_ALARM)
    }

    return permissions
//    return listOf(Manifest.permission.POST_NOTIFICATIONS)
}

fun scheduleReminder(
    context: Context,
    localDateTime: LocalDateTime,
    title: String,
    reminderId: Int
) {
    val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
        putExtra(NOTIFICATION_ID_EXTRA, reminderId)
        putExtra(NOTIFICATION_TITLE_EXTRA, title)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminderId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
        pendingIntent
    )
}

@Composable
fun <T> CollectAsOneTimeEvent(eventFlow: Flow<T>, onEvent: suspend (event: T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(eventFlow, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            eventFlow.collectLatest(onEvent)
        }
    }
}

fun LocalDateTime.toString(pattern: String): String {
    val locale = Locale.getDefault()
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)
    return this.format(formatter)
}