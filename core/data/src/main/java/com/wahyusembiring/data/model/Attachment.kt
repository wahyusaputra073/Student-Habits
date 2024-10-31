package com.wahyusembiring.data.model

import android.net.Uri

interface Attachment {
    val uri: Uri
}

data class Link(override val uri: Uri) : Attachment

data class Image(
    override val uri: Uri,
    val fileName: String,
) : Attachment

data class File(
    override val uri: Uri,
    val fileName: String,
) : Attachment