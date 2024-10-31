package com.wahyusembiring.subject.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.data.model.SubjectWithExamAndHomework
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.ExamCategory
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.subject.R
import com.wahyusembiring.ui.component.dropdown.Dropdown
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.UIText


@Suppress("t")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssigntmentPicker(
    subjects: List<SubjectWithExamAndHomework>,
    onAssignmentPicked: (Any) -> Unit,
    onAssignmentClick: (Any) -> Unit,
    onDismissRequest: () -> Unit,
    onCreateNewSubjectClick: () -> Unit
) {
    val selectedSubject = subjects.firstOrNull()
    val context = LocalContext.current
    var selectedAssignment by remember { mutableStateOf<Any?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                onClick = onDismissRequest
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null
                )
            }
            Button(
                enabled = selectedAssignment != null,
                onClick = {
                    selectedAssignment?.let(onAssignmentPicked)
                }
            ) {
                Text(text = stringResource(R.string.next))
            }
        }
        Dropdown(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.Medium),
            selected = selectedSubject,
            items = subjects,
            title = {
                if (it?.subject?.name != null) {
                    UIText.DynamicString(it.subject.name)
                } else {
                    UIText.StringResource(R.string.you_don_t_have_any_subject)
                }
            },
            onItemClick = {

            },
            emptyContent = {

            }
        )
        Column(
            modifier = Modifier.selectableGroup()
        ) {
            if (selectedSubject != null) {
                for (exam in selectedSubject.exams) {
                    ExamListItem(
                        color = selectedSubject.subject.color,
                        exam = exam,
                        selected = selectedAssignment == exam,
                        onRadioButtonClick = {
                            selectedAssignment = exam
                        },
                        onClick = {
                            onAssignmentClick(exam)
                        }
                    )
                }
                for (homework in selectedSubject.homeworks) {
                    HomeworkListItem(
                        color = selectedSubject.subject.color,
                        homework = homework,
                        selected = selectedAssignment == homework,
                        onRadioButtonClick = {
                            selectedAssignment = homework
                        },
                        onClick = {
                            onAssignmentClick(homework)
                        }
                    )
                }
                if (selectedSubject.exams.isEmpty() && selectedSubject.homeworks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.no_any_assignment_for_this_subject))
                    }
                }
            } else {
                Button(
                    onClick = onCreateNewSubjectClick
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = null
                    )
                    Text(text = stringResource(R.string.new_subject))
                }
            }
        }
    }
}

@Composable
private fun ExamListItem(
    selected: Boolean,
    color: Color,
    exam: Exam,
    onRadioButtonClick: () -> Unit,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .clickable { onClick() },
        leadingContent = {
            Icon(
                painter = painterResource(
                    id = com.wahyusembiring.ui.R.drawable.ic_exam
                ),
                contentDescription = null,
                tint = color
            )
        },
        headlineContent = {
            Text(text = exam.title)
        },
        supportingContent = {
            Text(
                text = when (exam.category) {
                    ExamCategory.WRITTEN -> stringResource(R.string.written_test)
                    ExamCategory.ORAL -> stringResource(R.string.oral_test)
                    ExamCategory.PRACTICAL -> stringResource(R.string.practical_test)
                }
            )
        },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = onRadioButtonClick
            )
        }
    )
}

@Composable
private fun HomeworkListItem(
    selected: Boolean,
    color: Color,
    homework: Homework,
    onRadioButtonClick: () -> Unit,
    onClick: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier
            .clickable { onClick() },
        leadingContent = {
            Icon(
                painter = painterResource(
                    id = com.wahyusembiring.ui.R.drawable.ic_homework
                ),
                contentDescription = null,
                tint = color
            )
        },
        headlineContent = {
            Text(text = homework.title)
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.homework)
            )
        },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = onRadioButtonClick
            )
        }
    )
}