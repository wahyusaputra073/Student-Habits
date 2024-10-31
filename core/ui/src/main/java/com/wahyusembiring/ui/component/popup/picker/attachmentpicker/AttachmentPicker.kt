package com.wahyusembiring.ui.component.popup.picker.attachmentpicker

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.imageLoader
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.File
import com.wahyusembiring.data.model.Image
import com.wahyusembiring.data.model.Link
import com.wahyusembiring.data.util.toAttachment
import com.wahyusembiring.ui.component.popup.alertdialog.LinkInputDialog
import com.wahyusembiring.ui.component.modalbottomsheet.component.NavigationAndActionButtonHeader
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.checkForPermissionOrLaunchPermissionLauncher
import com.wahyusembiring.ui.util.getFileAccessPermissionRequest
import com.wahyusembiring.ui.util.getPhotoAccessPermissionRequest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentPicker(
    state: AttachmentPickerState = rememberAttachmentPickerState(true),
    onAttachmentsConfirmed: (attachments: List<Attachment>) -> Unit,
    onDismissRequest: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val photoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
            coroutineScope.launch { onPhotoPickerLauncherResult(context, it, state) }
        }
    val documentPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
            coroutineScope.launch { onDocumentPickerLauncherResult(context, it, state) }
        }
    val mediaImagePermissionRequestLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    val documentPermissionRequestLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            documentPickerLauncher.launch(arrayOf("*/*"))
        }
    var showLinkInputDialog by remember { mutableStateOf(false) }


    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state.materialSheetState
    ) {
        PickAttachmentModalBottomSheetContent(
            state = state,
            onSaveButtonClicked = { onAttachmentsConfirmed(state.attachments) },
            onLinkButtonClicked = { showLinkInputDialog = true },
            onPhotoButtonClicked = {
                checkForPermissionOrLaunchPermissionLauncher(
                    context = context,
                    permissionToRequest = getPhotoAccessPermissionRequest(),
                    permissionRequestLauncher = mediaImagePermissionRequestLauncher,
                    onPermissionAlreadyGranted = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                )
            },
            onFileButtonClicked = {
                checkForPermissionOrLaunchPermissionLauncher(
                    context = context,
                    permissionToRequest = getFileAccessPermissionRequest(),
                    permissionRequestLauncher = documentPermissionRequestLauncher,
                    onPermissionAlreadyGranted = { documentPickerLauncher.launch(arrayOf("*/*")) }
                )
            },
            onDismissRequest = onDismissRequest
        )
    }
    if (showLinkInputDialog) {
        LinkInputDialog(
            onDismissRequest = { showLinkInputDialog = false },
            onLinkConfirmed = {
                state.addAttachment(
                    Link(uri = it)
                )
            }
        )
    }
}

@Composable
private fun ColumnScope.PickAttachmentModalBottomSheetContent(
    state: AttachmentPickerState,
    onSaveButtonClicked: () -> Unit,
    onLinkButtonClicked: () -> Unit,
    onPhotoButtonClicked: () -> Unit,
    onFileButtonClicked: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val scrollState = rememberScrollState()

    NavigationAndActionButtonHeader(
        onNavigationButtonClicked = { state.hideBottomSheet(onDismissRequest = onDismissRequest) },
        onActionButtonClicked = onSaveButtonClicked,
        navigationButtonDescription = stringResource(R.string.close_pick_attachment)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
    ) {
        items(
            items = state.attachments.filterIsInstance<Link>()
        ) {
            LinkAttachment(link = it)
        }
        items(
            items = state.attachments.filterIsInstance<Image>()
        ) {
            PhotoAttachment(photo = it)
        }
        items(
            items = state.attachments.filterIsInstance<File>()
        ) {
            FileAttachment(file = it)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scrollable(
                state = scrollState,
                orientation = Orientation.Horizontal
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onLinkButtonClicked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_link_internet),
                contentDescription = stringResource(R.string.link)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
            Text(text = stringResource(R.string.link))
        }
        Button(onClick = onPhotoButtonClicked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_photo),
                contentDescription = stringResource(R.string.photo)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
            Text(text = stringResource(R.string.photo))
        }
        Button(onClick = onFileButtonClicked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_file),
                contentDescription = stringResource(R.string.file)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
            Text(text = stringResource(R.string.file))
        }
    }
}

@Composable
private fun LinkAttachment(
    link: Link
) {
    ListItem(
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_link_internet),
                contentDescription = link.uri.toString()
            )
        },
        headlineContent = {
            Text(text = link.uri.toString())
        },
        trailingContent = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more_vertical),
                    contentDescription = stringResource(id = R.string.more_option)
                )
            }
        }
    )
}

@Composable
private fun PhotoAttachment(
    photo: Image
) {
    val context = LocalContext.current

    ListItem(
        leadingContent = {
            AsyncImage(
                modifier = Modifier
                    .size(50.dp),
                model = photo.uri,
                contentDescription = photo.fileName,
                imageLoader = context.imageLoader,
            )
        },
        headlineContent = {
            Text(text = photo.fileName)
        },
        trailingContent = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more_vertical),
                    contentDescription = stringResource(id = R.string.more_option)
                )
            }
        }
    )
}

@Composable
private fun FileAttachment(
    file: File
) {
    ListItem(
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_file),
                contentDescription = file.fileName
            )
        },
        headlineContent = {
            Text(text = file.fileName)
        },
        trailingContent = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more_vertical),
                    contentDescription = stringResource(id = R.string.more_option)
                )
            }
        }
    )
}

private suspend fun onPhotoPickerLauncherResult(
    context: Context,
    uris: List<Uri>,
    state: AttachmentPickerState
) {
    val photos = uris.map { uri ->
        val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, flag)
        uri.toAttachment(context)
    }
    state.addAttachment(*photos.toTypedArray())
}

private suspend fun onDocumentPickerLauncherResult(
    context: Context,
    uris: List<Uri>,
    state: AttachmentPickerState
) {
    val files = uris.map { uri ->
        val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, flag)
        uri.toAttachment(context)
    }
    state.addAttachment(*files.toTypedArray())
}