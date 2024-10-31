package com.wahyusembiring.ui.component.scoredialog

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.State
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.ui.R

data class ScoreDialog(
    val exam: ExamWithSubject,
    val initialScore: Int = 0
)

@Composable
fun ScoreDialog(
    initialScore: Int = 0,
    onDismissRequest: () -> Unit,
    onMarkNotDoneYet: () -> Unit,
    onScoreConfirmed: (score: Int) -> Unit
) {
    var score by remember { mutableStateOf(initialScore.toString()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(
                onClick = {
                    onMarkNotDoneYet()
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(R.string.undone))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onScoreConfirmed(score.toIntOrNull() ?: 0)
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(R.string.done))
            }
        },
        title = {
            Text(text = stringResource(R.string.enter_your_exam_score))
        },
        text = {
            OutlinedTextField(
                value = score,
                onValueChange = { score = it },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
        }
    )
}