package com.speakeng.app.feature.reading.data.datasource

/** Abstracts the `assets/books/` folder so BookRepositoryImpl can be unit tested without Android assets. */
interface BundledBookSource {
    /** File names (e.g. "a_walk_in_the_park.txt") of the bundled sample books. */
    fun list(): List<String>

    fun readText(fileName: String): String
}
