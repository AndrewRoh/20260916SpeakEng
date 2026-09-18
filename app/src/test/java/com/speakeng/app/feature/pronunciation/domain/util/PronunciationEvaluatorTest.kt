package com.speakeng.app.feature.pronunciation.domain.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PronunciationEvaluatorTest {

    @Test
    fun `exact match scores 100`() {
        val result = PronunciationEvaluator.evaluate("I have a pet dog", "I have a pet dog")

        assertEquals(100, result.accuracy)
        assertTrue(result.wordResults.all { it.isCorrect })
    }

    @Test
    fun `normalizes case and punctuation before comparing`() {
        val result = PronunciationEvaluator.evaluate("Hello, world!", "hello world")

        assertEquals(100, result.accuracy)
    }

    @Test
    fun `completely different transcript scores 0`() {
        val result = PronunciationEvaluator.evaluate("I have a pet dog", "banana rocket sky")

        assertEquals(0, result.accuracy)
        assertTrue(result.wordResults.all { !it.isCorrect })
    }

    @Test
    fun `a substituted word is marked incorrect without shifting the rest`() {
        val result = PronunciationEvaluator.evaluate("I have a pet dog", "I have a big dog")

        val words = result.wordResults.associate { it.word to it.isCorrect }
        assertEquals(mapOf("i" to true, "have" to true, "a" to true, "pet" to false, "dog" to true), words)
        assertEquals(80, result.accuracy)
    }

    @Test
    fun `a missing word is marked incorrect without shifting the rest`() {
        val result = PronunciationEvaluator.evaluate("I have a pet dog", "I have a dog")

        val words = result.wordResults.associate { it.word to it.isCorrect }
        assertEquals(mapOf("i" to true, "have" to true, "a" to true, "pet" to false, "dog" to true), words)
    }

    @Test
    fun `extra recognized words do not affect target word alignment`() {
        val result = PronunciationEvaluator.evaluate("I have a dog", "um I have a really nice dog yeah")

        assertEquals(100, result.accuracy)
    }

    @Test
    fun `blank target sentence scores 0 instead of dividing by zero`() {
        val result = PronunciationEvaluator.evaluate("", "hello")

        assertEquals(0, result.accuracy)
        assertTrue(result.wordResults.isEmpty())
    }
}
