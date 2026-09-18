package com.speakeng.app.feature.reading.data.datasource

import android.net.Uri
import java.io.InputStream

/** Abstracts reading the bytes behind a picked SAF [Uri] so BookRepositoryImpl can be unit tested. */
interface UploadSource {
    fun open(uri: Uri): InputStream?
}
