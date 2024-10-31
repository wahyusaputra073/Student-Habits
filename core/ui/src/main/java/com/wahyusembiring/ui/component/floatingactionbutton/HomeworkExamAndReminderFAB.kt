package com.wahyusembiring.ui.component.floatingactionbutton

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.component.floatingactionbutton.component.MultiFloatingActionButton
import com.wahyusembiring.ui.component.floatingactionbutton.component.Scrim
import com.wahyusembiring.ui.component.floatingactionbutton.component.SubFloatingActionButton


@Composable
fun HomeworkExamAndReminderFAB(
    isExpanded: Boolean,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onReminderFabClick: () -> Unit,
    onExamFabClick: () -> Unit,
    onHomeworkFabClick: () -> Unit
) {

    Scrim(isVisible = isExpanded)
    MultiFloatingActionButton(
        mainFloatingActionButton = {
            MainFloatingActionButton(
                onClick = onClick,
                isExpanded = isExpanded
            )
        },
        subFloatingActionButton = {
            SubFloatingActionButton(
                isExpanded = isExpanded,
                onReminderFabClick = {
                    onDismiss()
                    onReminderFabClick()
                },
                onExamFabClick = {
                    onDismiss()
                    onExamFabClick()
                },
                onHomeworkFabClick = {
                    onDismiss()
                    onHomeworkFabClick()
                }
            )
        }
    )
}

@Composable
private fun MainFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isExpanded: Boolean
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick
    ) {
        val animatedDegree by animateFloatAsState(
            label = "Icon Rotation Animation",
            targetValue = if (isExpanded) 135f else 0f,
        )
        Icon(
            modifier = Modifier.rotate(animatedDegree),
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = stringResource(R.string.create_task)
        )
    }
}

@Composable
private fun ColumnScope.SubFloatingActionButton(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onReminderFabClick: () -> Unit,
    onExamFabClick: () -> Unit,
    onHomeworkFabClick: () -> Unit
) {
    SubFloatingActionButton(
        isVisible = isExpanded,
        onClick = onReminderFabClick,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_reminder),
            contentDescription = stringResource(R.string.add_reminder)
        )
    }
    SubFloatingActionButton(
        isVisible = isExpanded,
        onClick = onExamFabClick,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_exam),
            contentDescription = stringResource(R.string.add_exam_schedule)
        )
    }
    SubFloatingActionButton(
        isVisible = isExpanded,
        onClick = onHomeworkFabClick,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_homework),
            contentDescription = stringResource(R.string.add_homework)
        )
    }
}