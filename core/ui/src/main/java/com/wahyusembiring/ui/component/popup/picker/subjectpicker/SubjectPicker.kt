package com.wahyusembiring.ui.component.popup.picker.subjectpicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.ui.component.modalbottomsheet.component.AddNewSubject
import com.wahyusembiring.ui.component.modalbottomsheet.component.SubjectListItem
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectPicker(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit,
    subjects: List<Subject>,
    onSubjectSelected: (subject: Subject) -> Unit,
    navigateToCreateSubjectScreen: () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        SubjectPickerContent(
            onSubjectSelected = onSubjectSelected,
            onCancelButtonClicked = onDismissRequest,
            navigateToCreateSubjectScreen = navigateToCreateSubjectScreen,
            subjects = subjects
        )
    }

}

@Composable
private fun ColumnScope.SubjectPickerContent(
    onSubjectSelected: (subject: Subject) -> Unit,
    onCancelButtonClicked: () -> Unit,
    navigateToCreateSubjectScreen: () -> Unit,
    subjects: List<Subject> = emptyList()
) {
    Text(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(bottom = MaterialTheme.spacing.Medium),
        style = MaterialTheme.typography.titleLarge,
        text = stringResource(R.string.pick_a_subject)
    )
    val listItemColors = ListItemDefaults.colors(containerColor = Color.Transparent)
    AddNewSubject(
        colors = listItemColors,
        onClicked = navigateToCreateSubjectScreen
    )
    subjects.forEach { subject ->
        SubjectListItem(
            colors = listItemColors,
            subject = subject,
            onClicked = onSubjectSelected,
            onDeleteSubClick ={}
        )
    }
    Button(
        modifier = Modifier
            .align(Alignment.End)
            .padding(MaterialTheme.spacing.Medium),
        onClick = onCancelButtonClicked
    ) {
        Text(text = stringResource(id = R.string.cancel))
    }
}
