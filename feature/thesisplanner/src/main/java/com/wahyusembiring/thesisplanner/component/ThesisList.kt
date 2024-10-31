package com.wahyusembiring.thesisplanner.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.thesisplanner.R
import com.wahyusembiring.thesisplanner.screen.thesisselection.Thesis
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.theme.spacing

@Composable
fun ThesisList(
    listOfThesis: List<Thesis>,
    onThesisClick: (Thesis) -> Unit,
    onDeleteThesis: (Thesis) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = listOfThesis,
            key = { it.thesis.id }
        ) { thesis ->
            var showConfirmationDialog by remember { mutableStateOf(false) }
            ThesisCard(
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spacing.Medium,
                    vertical = MaterialTheme.spacing.Small
                ),
                thesis = thesis,
                onClick = { onThesisClick(thesis) },
                onDeleteClick = {
                    showConfirmationDialog = true
                }
            )
            if (showConfirmationDialog) {
                ConfirmationAlertDialog(
                    onPositiveButtonClick = {
                        onDeleteThesis(thesis)
                        showConfirmationDialog = false
                    },
                    onNegativeButtonClick = { showConfirmationDialog = false },
                    title = stringResource(id = R.string.delete_thesis),
                    message = stringResource(id = R.string.are_you_sure_you_want_to_delete_this_thesis),
                    positiveButtonText = stringResource(id = R.string.yes),
                    negativeButtonText = stringResource(id = R.string.no),
                    onDismissRequest = { showConfirmationDialog = false }
                )
            }
        }
    }
}