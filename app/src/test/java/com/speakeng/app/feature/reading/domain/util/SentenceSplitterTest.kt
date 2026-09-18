package com.speakeng.app.feature.reading.domain.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SentenceSplitterTest {

    @Test
    fun `splits on period`() {
        assertEquals(
            listOf("Hello there.", "How are you."),
            SentenceSplitter.split("Hello there. How are you."),
        )
    }

    @Test
    fun `splits on exclamation and question marks`() {
        assertEquals(
            listOf("Watch out!", "Are you okay?", "I am fine."),
            SentenceSplitter.split("Watch out! Are you okay? I am fine."),
        )
    }

    @Test
    fun `collapses extra whitespace between sentences`() {
        assertEquals(
            listOf("One.", "Two."),
            SentenceSplitter.split("One.   Two."),
        )
    }

    @Test
    fun `keeps the last sentence without trailing punctuation`() {
        assertEquals(
            listOf("First sentence.", "Second without a period"),
            SentenceSplitter.split("First sentence. Second without a period"),
        )
    }

    @Test
    fun `returns empty list for blank text`() {
        assertEquals(emptyList<String>(), SentenceSplitter.split("   "))
    }
}
