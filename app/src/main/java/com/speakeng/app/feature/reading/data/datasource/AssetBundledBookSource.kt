package com.speakeng.app.feature.reading.data.datasource

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AssetBundledBookSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : BundledBookSource {

    override fun list(): List<String> =
        context.assets.list(ASSET_DIR)?.filter { it.endsWith(".txt") }.orEmpty()

    override fun readText(fileName: String): String =
        context.assets.open("$ASSET_DIR/$fileName").bufferedReader().use { it.readText() }

    private companion object {
        const val ASSET_DIR = "books"
    }
}
