package com.wahyusembiring.ui.component.popup.picker.examcategorypicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.wahyusembiring.data.model.entity.ExamCategory
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamCategoryPicker(
    initialCategory: ExamCategory,
    onDismissRequest: () -> Unit,
    onCategoryPicked: (ExamCategory) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        content = {
            ExamCategoryPickerContent(
                initialCategory = initialCategory,
                onCancelButtonClicked = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        onDismissRequest()
                    }
                },
                onCategoryPicked = {
                    onCategoryPicked(it)
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        onDismissRequest()
                    }
                }
            )
        }
    )
}

@Composable
private fun ColumnScope.ExamCategoryPickerContent(
    initialCategory: ExamCategory,
    onCancelButtonClicked: () -> Unit,
    onCategoryPicked: (ExamCategory) -> Unit
) {
    val categories = ExamCategory.entries.map {
        when (it) {
            ExamCategory.WRITTEN -> stringResource(R.string.written_test)
            ExamCategory.ORAL -> stringResource(R.string.oral_test)
            ExamCategory.PRACTICAL -> stringResource(R.string.practical_test)
        }
    }
    var selectedCategoryIndex by remember {
        mutableIntStateOf(
            ExamCategory.entries.indexOf(
                initialCategory
            )
        )
    }

    Text(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(bottom = MaterialTheme.spacing.Medium),
        style = MaterialTheme.typography.titleLarge,
        text = stringResource(R.string.exam_category)
    )
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        categories.forEachIndexed { index, category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        role = Role.RadioButton
                    )
                    .padding(
                        horizontal = MaterialTheme.spacing.Medium,
                        vertical = MaterialTheme.spacing.Small
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                RadioButton(
                    modifier = Modifier.semantics { contentDescription = category },
                    selected = selectedCategoryIndex == index,
                    onClick = null
                )
                Text(
                    modifier = Modifier.padding(start = MaterialTheme.spacing.Medium),
                    text = category
                )
            }
        }
    }
    Row(
        modifier = Modifier
            .align(Alignment.End)
    ) {
        TextButton(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.Medium),
            onClick = onCancelButtonClicked
        ) {
            Text(text = stringResource(id = R.string.cancel))
        }
        Button(
            modifier = Modifier
                .padding(MaterialTheme.spacing.Medium),
            onClick = {
                onCategoryPicked(ExamCategory.entries[selectedCategoryIndex])
            }
        ) {
            Text(text = stringResource(id = R.string.confirm))
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun ExamCategoryPickerPreview() {
    Column {
        ExamCategoryPickerContent(
            initialCategory = ExamCategory.WRITTEN,
            onCancelButtonClicked = { },
            onCategoryPicked = { }
        )
    }
}