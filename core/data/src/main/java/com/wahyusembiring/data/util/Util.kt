package com.wahyusembiring.data.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.database.getLongOrNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.File
import com.wahyusembiring.data.model.Image
import com.wahyusembiring.data.model.Link
import com.wahyusembiring.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


fun Uri.toFile(context: Context): File {
    val mediaMetadata = getMediaMetadata(context, this)
    return File(this, mediaMetadata.displayName!!)
}

fun Uri.toAttachment(context: Context): Attachment {
    return when (scheme) {
        "http", "https" -> {
            Link(this)
        }

        "content" -> {
            val mediaMetadata = getMediaMetadata(context, this)
            val mimeType = mediaMetadata.mimeType ?: throw NullPointerException("Mime type is null")
            when {
                mimeType.startsWith("image/") -> {
                    Image(this, mediaMetadata.displayName!!)
                }

                else -> {
                    File(this, mediaMetadata.displayName!!)
                }
            }
        }

        else -> throw IllegalArgumentException("Unsupported scheme: $scheme")
    }
}

private data class MediaMetadata(
    val displayName: String? = null,
    val mimeType: String? = null,
)

private suspend fun getMediaMetadataAsync(context: Context, uri: Uri): MediaMetadata {
    return withContext(Dispatchers.IO) {
        getMediaMetadata(context, uri)
    }
}

private fun getMediaMetadata(context: Context, uri: Uri): MediaMetadata {
    return context.contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    )?.use { cursor ->
        val displayNameColumnIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val mimeTypeColumnIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

        if (cursor.moveToFirst()) {
            val displayName = cursor.getString(displayNameColumnIndex)
            val mimeType = cursor.getString(mimeTypeColumnIndex)

            return@use MediaMetadata(displayName, mimeType)
        } else {
            throw Error("Unexpected null returned by cursor.moveToFirst() method")
        }
    } ?: throw Error("Unexpected null returned by contentResolver query")
}

fun FirebaseUser.toUser(): User {
    return User(
        id = uid,
        name = displayName,
        email = email,
        photoUrl = photoUrl?.toString(),
    )
}