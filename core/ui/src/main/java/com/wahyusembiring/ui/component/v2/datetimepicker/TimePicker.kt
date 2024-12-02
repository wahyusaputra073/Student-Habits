package com.wahyusembiring.ui.component.v2.datetimepicker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.HabitTheme
import com.wahyusembiring.ui.theme.primaryDark
import com.wahyusembiring.ui.theme.primaryLight
import com.wahyusembiring.ui.theme.spacing
import java.time.LocalTime
import kotlin.math.sin

class TimePickerState(
    private val initialTime: LocalTime = LocalTime.now()
) {
    private val selectedTimeState = mutableStateOf(initialTime)
    var selectedTime: LocalTime
        get() = selectedTimeState.value
        set(value) {
            selectedTimeState.value = value
        }
}

@Composable
fun rememberTimePickerState(
    initialTime: LocalTime = LocalTime.now()
): TimePickerState {
    return remember { TimePickerState(initialTime) }
}

enum class TimePickerStyle {
    SPINNER, CLOCK
}

@Composable
fun TimePicker(
    state: TimePickerState = rememberTimePickerState(),
    title: String = stringResource(R.string.select_time),
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    allowedTime: ((LocalTime) -> Boolean)? = null
) {

    Dialog(
        onDismissRequest = onDismissRequest
    ) {

        var isNight by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val context = LocalContext.current

        LaunchedEffect(state.selectedTime, allowedTime) {
            val isInAllowedTime = allowedTime?.invoke(state.selectedTime) ?: true
            errorMessage = if (isInAllowedTime) {
                null
            } else {
                context.getString(R.string.the_selected_time_is_not_allowed)
            }
        }
        
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Box(
                modifier = Modifier.height(IntrinsicSize.Max)
            ) {
                AnimatedDayPhasedBackground(
                    time = state.selectedTime,
                    onDayChange = { isNight = it }
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePickerHeader(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Large)
                            .padding(top = MaterialTheme.spacing.Large, bottom = MaterialTheme.spacing.Medium),
                        state = state,
                        title = title,
                        isNight = isNight,
                        onConfirmButtonClick = {
                            onTimeSelected(state.selectedTime)
                        },
                        isButtonEnabled = errorMessage == null,
                        errorMessage = errorMessage,
                    )
                    MaterialTimePicker(state = state)
                    Spacer(modifier = Modifier.height(130.dp))
                }
            }
        }
    }
}

@Composable
internal fun TimePickerHeader(
    modifier: Modifier = Modifier,
    state: TimePickerState = rememberTimePickerState(),
    title: String = stringResource(R.string.select_time),
    isNight: Boolean,
    isButtonEnabled: Boolean = true,
    errorMessage: String? = null,
    onConfirmButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                textAlign = TextAlign.Start,
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isNight) primaryDark else primaryLight
            )
            Button(
                enabled = isButtonEnabled,
                onClick = onConfirmButtonClick
            ) {
                Text(stringResource(R.string.confirm))
            }
        }
        AnimatedVisibility(
            visible = errorMessage != null
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.Medium)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small
                    ),
                textAlign = TextAlign.Center,
                text = errorMessage ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MaterialTimePicker(
    state: TimePickerState = rememberTimePickerState(),
) {
    val materialTimePickerState = androidx.compose.material3.rememberTimePickerState(
        initialHour = state.selectedTime.hour,
        initialMinute = state.selectedTime.minute
    )

    LaunchedEffect(materialTimePickerState.hour, materialTimePickerState.minute) {
        state.selectedTime = LocalTime.of(materialTimePickerState.hour, materialTimePickerState.minute)
    }

    androidx.compose.material3.TimePicker(
        modifier = Modifier,
        colors = TimePickerDefaults.colors().run {
            copy(
                timeSelectorUnselectedContainerColor = timeSelectorUnselectedContainerColor.copy(alpha = 0.35f),
                timeSelectorSelectedContainerColor = timeSelectorSelectedContainerColor.copy(alpha = 0.8f),
                clockDialColor = clockDialColor.copy(alpha = 0.7f),
            )
        },
        state = materialTimePickerState,
    )
}

private fun skyColor(atTime: LocalTime): Color {
    val skyFixColorList: List<Pair<Color, ClosedRange<LocalTime>>> = listOf(
        Color(0xFF191919) to LocalTime.MIN..LocalTime.of(4, 59),
        Color(0xFF2C3E50) to LocalTime.of(5, 0)..LocalTime.of(5, 59),
        Color(0xFFFFB78D) to LocalTime.of(6, 0)..LocalTime.of(6, 59),
        Color(0xFF00BFFF) to LocalTime.of(7, 0)..LocalTime.of(16, 59),
        Color(0xFFF27121) to LocalTime.of(17, 0)..LocalTime.of(17, 59),
        Color(0xFF2C3E50) to LocalTime.of(18, 0)..LocalTime.of(18, 59),
        Color(0xFF191919) to LocalTime.of(19, 0)..LocalTime.MAX,
    )
    return skyFixColorList.first { atTime in it.second }.first
}

@Composable
internal fun AnimatedDayPhasedBackground(
    time: LocalTime,
    onDayChange: ((isNight: Boolean) -> Unit)? = null
) {

    LaunchedEffect(time) {
        onDayChange?.invoke(time.hour in 0..6 || time.hour in 18..23)
    }

    val second = time.toSecondOfDay()
    val secondInWholeDay = 86400.0
    val maxHeight = 120
    val bottomOffset = 50

    val radian = second * 2 * Math.PI / secondInWholeDay
    val offset = Math.PI / 2
    val sine = sin(radian + offset).toFloat()

    val animateSun = animateFloatAsState(
        targetValue = sine,
        label = "sun"
    )

    val animateMoon = animateFloatAsState(
        targetValue = sine,
        label = "moon"
    )

    val animateSkyColor = animateColorAsState(
        targetValue = skyColor(atTime = time),
        label = "sky"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = animateSkyColor.value)
    ) {

        Image(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset {
                    IntOffset(
                        x = 0,
                        y = (((animateMoon.value * -maxHeight) + bottomOffset) * density).toInt()
                    )
                },
            painter = painterResource(R.drawable.moon),
            contentDescription = null,
        )

        Image(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset {
                    IntOffset(
                        x = 0,
                        y = (((animateSun.value * maxHeight) + bottomOffset) * density).toInt()
                    )
                },
            painter = painterResource(R.drawable.sun),
            contentDescription = null
        )

        Image(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .scale(scaleX = 1f, scaleY = 0.7f)
                .offset(y = bottomOffset.dp),
            painter = painterResource(R.drawable.mountain),
            contentDescription = null
        )

    }
}

@Preview
@Composable
private fun TimePickerPreview() {
    HabitTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            TimePicker(
                onDismissRequest = {},
                onTimeSelected = {}
            )
        }
    }
}