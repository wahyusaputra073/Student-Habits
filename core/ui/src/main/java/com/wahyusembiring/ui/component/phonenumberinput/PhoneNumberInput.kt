package com.wahyusembiring.ui.component.phonenumberinput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing

@Composable
fun PhoneNumberInput(
    modifier: Modifier = Modifier,
    phoneNumbers: List<String>,
    onNewPhoneNumber: (String) -> Unit,
    onDeletePhoneNumber: (String) -> Unit // Menambahkan parameter untuk menghapus nomor telepon
) {
    var showPhoneNumberDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = MaterialTheme.spacing.Medium),
                text = stringResource(R.string.phone_number),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = { showPhoneNumberDialog = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        HorizontalDivider()
        Column(
            modifier = Modifier.padding(start = MaterialTheme.spacing.Large)
        ) {
            if (phoneNumbers.isEmpty()) {
                EmptyPhoneNumber()
            }
            for (phoneNumber in phoneNumbers) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                PhoneNumberListItem(phoneNumber = phoneNumber,
                    onDeleteClick = { onDeletePhoneNumber(phoneNumber) })

            }
        }
    }

    if (showPhoneNumberDialog) {
        PhoneNumberInputDialog(
            onDismissRequest = { showPhoneNumberDialog = false },
            onPhoneNumberAddClick = onNewPhoneNumber
        )
    }
}

@Composable
private fun PhoneNumberListItem(
    phoneNumber: String,
    onDeleteClick: () -> Unit // Menambahkan parameter untuk menghapus

) {
    var expanded by remember { mutableStateOf(false) } // State untuk dropdown

    Row(
        modifier = Modifier.requiredHeight(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_contact),
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
        VerticalDivider(
            modifier = Modifier.fillMaxHeight()
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
        Text(
            text = phoneNumber,
//            modifier = Modifier.weight(1f) // Menggunakan weight agar teks mengisi ruang
        )
        Box {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier.size(24.dp) // Ukuran ikon titik tiga
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more_vertical),
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.secondary // Menggunakan warna yang sama dengan ikon kontak
                )
            }
            // Dropdown menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        onDeleteClick()
                        expanded = false // Menutup dropdown setelah klik
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyPhoneNumber() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.Medium,
                vertical = MaterialTheme.spacing.Small
            ),
            text = stringResource(R.string.no_phone_number),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontStyle = FontStyle.Italic
            )
        )
    }
}

@Composable
private fun PhoneNumberInputDialog(
    onDismissRequest: () -> Unit,
    onPhoneNumberAddClick: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(R.string.enter_phone_number))
        },
        text = {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text(text = stringResource(R.string.phone_number)) },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onPhoneNumberAddClick(phoneNumber)
                    onDismissRequest().also { phoneNumber = "" }
                }
            ) {
                Text(text = stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}