package com.wahyusembiring.ui.component.v2.overviewcard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.imageLoader
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing

@Composable
fun EmptyEventCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.Medium)
                    .width(200.dp),
                painter = painterResource(R.drawable.relaxing),
                contentDescription = stringResource(R.string.no_event_picture),
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            Column {
                Text(
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.Small),
                    text = "There are no events",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "You can add new events with the button in the bottom right corner",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
private fun EmptyEventCardPreview() {
    EmptyEventCard()
}