package com.speakeng.app.feature.reading.domain.model

enum class BookSource { BUNDLED, UPLOADED }

/**
 * [filePath] means different things depending on [source]: an asset-relative file name
 * (e.g. "a_walk_in_the_park.txt") for [BookSource.BUNDLED], or an absolute file path for
 * [BookSource.UPLOADED].
 */
data class Book(
    val id: String,
    val title: String,
    val source: BookSource,
    val filePath: String,
)
