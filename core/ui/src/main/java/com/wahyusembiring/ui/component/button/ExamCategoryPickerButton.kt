package com.wahyusembiring.ui.component.button

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.data.model.entity.ExamCategory
import com.wahyusembiring.ui.R

@Composable
fun ExamCategoryPickerButton(
    modifier: Modifier = Modifier,
    examCategory: ExamCategory,
    onClicked: (() -> Unit)? = null,
) {
    ListItem(
        modifier = modifier
            .then(
                if (onClicked != null) {
                    Modifier.clickable { onClicked() }
                } else {
                    Modifier
                }
            ),
        leadingContent = {
            Icon(
                painter = when (examCategory) {
                    ExamCategory.WRITTEN -> painterResource(id = R.drawable.ic_written_test)
                    ExamCategory.ORAL -> painterResource(id = R.drawable.ic_oral_test)
                    ExamCategory.PRACTICAL -> painterResource(id = R.drawable.ic_practical_test)
                },
                contentDescription = stringResource(R.string.pick_test_category),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        headlineContent = {
            Text(
                text = when (examCategory) {
                    ExamCategory.WRITTEN -> stringResource(R.string.written_test)
                    ExamCategory.ORAL -> stringResource(R.string.oral_test)
                    ExamCategory.PRACTICAL -> stringResource(R.string.practical_test)
                },
            )
        }
    )
}