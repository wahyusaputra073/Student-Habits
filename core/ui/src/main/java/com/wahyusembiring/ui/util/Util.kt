package com.wahyusembiring.ui.util

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils

fun getWindowSize(context: Context): Size {
    val windowManager =
        context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    var width = 0
    var height = 0
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val bounds = windowManager.currentWindowMetrics.bounds
        width = bounds.width()
        height = bounds.height()
    } else {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
    }
    return Size(width.toFloat(), height.toFloat())
}

fun checkForPermissionOrLaunchPermissionLauncher(
    context: Context,
    permissionToRequest: List<String>,
    permissionRequestLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    onPermissionAlreadyGranted: () -> Unit
) {
    if (permissionToRequest.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }) {
        onPermissionAlreadyGranted()
    } else {
        permissionRequestLauncher.launch(permissionToRequest.toTypedArray())
    }
}

fun getPhotoAccessPermissionRequest(): List<String> {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        )

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(Manifest.permission.READ_MEDIA_IMAGES)
        else -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}

fun getFileAccessPermissionRequest(): List<String> {
    return when {
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        else -> emptyList()
    }
}

fun Color.adjustHSL(
    hue: Float? = null,
    saturation: Float? = null,
    lightness: Float? = null
): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(toArgb(), hsl)
    hue?.let { hsl[0] = it }
    saturation?.let { hsl[1] = it }
    lightness?.let { hsl[2] = it }
    return Color(ColorUtils.HSLToColor(hsl))
}

fun Color.contrastColor(): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(toArgb(), hsl)
    val brightness = hsl[2]
    return if (brightness > 0.5f) Color.Black else Color.White
}

tailrec fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

private fun getVectorBitmap(context: Context, drawableId: Int): Bitmap? {

    var bitmap: Bitmap? = null

    when (val drawable = ContextCompat.getDrawable(context, drawableId)) {

        is BitmapDrawable -> {
            bitmap = drawable.bitmap
        }

        is VectorDrawable -> {

            bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

        }
    }

    return bitmap
}