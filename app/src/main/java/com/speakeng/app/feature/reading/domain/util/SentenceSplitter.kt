package com.speakeng.app.feature.reading.domain.util

/**
 * Splits English text into sentences on `.`, `!`, `?` followed by whitespace.
 * This is a simple rule-based splitter, not a linguistic one: abbreviations like
 * "Mr." or "e.g." will be mis-split. Good enough for short practice texts; a proper
 * NLP-based splitter is out of scope for this feature.
 */
object SentenceSplitter {
    private val SENTENCE_BOUNDARY = Regex("(?<=[.!?])\\s+")

    fun split(text: String): List<String> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return emptyList()
        return trimmed.split(SENTENCE_BOUNDARY)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}
