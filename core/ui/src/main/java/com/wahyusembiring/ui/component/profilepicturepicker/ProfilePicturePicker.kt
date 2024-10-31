package com.wahyusembiring.ui.component.profilepicturepicker

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.getActivity
import com.wahyusembiring.ui.util.getPhotoAccessPermissionRequest

@Composable
fun ProfilePicturePicker(
    modifier: Modifier = Modifier,
    imageUri: Uri?,
    onImageSelected: (uri: Uri?) -> Unit
) {
    val context = LocalContext.current
    var permissionRationale: List<String>? by remember {
        mutableStateOf(null)
    }

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = {
                context.contentResolver.takePersistableUriPermission(
                    it ?: return@rememberLauncherForActivityResult,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                onImageSelected(it)
            }
        )

    val imagePermissionRequestLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                onImagePermissionRequestLauncherResult(
                    context = context,
                    permissionsResult = it,
                    onAllPermissionsGranted = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onShouldShowPermissionRationale = { permissions ->
                        permissionRationale = permissions
                    },
                    onUserPermanentlyDeniedPermissions = { }
                )
            }
        )

    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable {
                onPhotoClick(
                    context = context,
                    permissionsLauncher = imagePermissionRequestLauncher,
                    photoPickerLauncher = photoPickerLauncher,
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val scrimColor = MaterialTheme.colorScheme.scrim
        val cameraImageVector: ImageVector = ImageVector.vectorResource(id = R.drawable.ic_photo)
        val cameraImagePainter = rememberVectorPainter(image = cameraImageVector)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawArc(
                        color = scrimColor.copy(alpha = 0.2f),
                        useCenter = true,
                        startAngle = 0f,
                        sweepAngle = 180f,
                        topLeft = Offset(0f, size.height / 2)
                    )
                    val iconWidth = size.height / 4 * 0.7f

                    translate(
                        left = (size.width / 2) - (iconWidth / 2),
                        top = (size.height / 4 * 3) + ((size.height - (size.height / 4 * 3)) / 2) - ((iconWidth / 2) + (iconWidth / 2 * 0.15f)),
                    ) {
                        with(cameraImagePainter) {
                            draw(
                                size = Size(iconWidth, iconWidth),
                                alpha = 0.5f,
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MaterialTheme.spacing.Medium),
                        painter = painterResource(id = R.drawable.ic_person),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null
                    )
                }
            } else {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
    if (permissionRationale != null) {
        InformationAlertDialog(
            onButtonClicked = {
                onPhotoClick(
                    context = context,
                    permissionToRequest = permissionRationale ?: return@InformationAlertDialog,
                    permissionsLauncher = imagePermissionRequestLauncher,
                    photoPickerLauncher = photoPickerLauncher,
                )
            },
            buttonText = "Ok",
            title = "Permission Required",
            message = "Our app need permission to access your photo. Please grant the permission.",
            onDismissRequest = { permissionRationale = null }
        )
    }
}

private fun onPhotoClick(
    context: Context,
    permissionToRequest: List<String> = getPhotoAccessPermissionRequest(),
    permissionsLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
) {
    if (permissionToRequest.all { isPermissionGranted(context, it) }) {
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    } else {
        val deniedPermissions = permissionToRequest.filter { !isPermissionGranted(context, it) }
        permissionsLauncher.launch(deniedPermissions.toTypedArray())
    }
}

private fun isPermissionGranted(
    context: Context,
    permission: String
): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

private fun onImagePermissionRequestLauncherResult(
    context: Context,
    permissionsResult: Map<String, Boolean>,
    onAllPermissionsGranted: () -> Unit,
    onShouldShowPermissionRationale: (permissions: List<String>) -> Unit,
    onUserPermanentlyDeniedPermissions: (permissions: List<String>) -> Unit,
) {
    if (permissionsResult.all { it.value }) {
        onAllPermissionsGranted()
    } else {
        val deniedPermissions = permissionsResult.filter { !it.value }.keys
        val (permissionToShowRationale, permanentlyDeniedPermissions) =
            deniedPermissions.partition {
                ActivityCompat.shouldShowRequestPermissionRationale(context.getActivity()!!, it)
            }
        onShouldShowPermissionRationale(permissionToShowRationale)
        onUserPermanentlyDeniedPermissions(permanentlyDeniedPermissions)
    }
}