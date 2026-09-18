package com.speakeng.app.feature.reading.data.datasource

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject

class ContentResolverUploadSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : UploadSource {
    override fun open(uri: Uri): InputStream? = context.contentResolver.openInputStream(uri)
}
