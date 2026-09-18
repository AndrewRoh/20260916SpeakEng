package com.speakeng.app.feature.pronunciation.domain.util

import com.speakeng.app.feature.pronunciation.domain.model.PronunciationResult
import com.speakeng.app.feature.pronunciation.domain.model.WordMatchResult

/**
 * Scores how closely a recognized transcript matches a target sentence, word by word.
 *
 * This is NOT phoneme-level pronunciation assessment (that would need a paid service like
 * Azure/Google Pronunciation Assessment, which is not wired up yet) — it is a simple word-match
 * accuracy over the STT transcript, meant as a rough, reference-only signal.
 */
object PronunciationEvaluator {

    fun evaluate(targetSentence: String, recognizedText: String): PronunciationResult {
        val targetWords = normalize(targetSentence)
        val recognizedWords = normalize(recognizedText)
        val wordResults = alignWords(targetWords, recognizedWords)
        val accuracy = if (wordResults.isEmpty()) {
            0
        } else {
            (wordResults.count { it.isCorrect } * 100) / wordResults.size
        }
        return PronunciationResult(recognizedText = recognizedText, accuracy = accuracy, wordResults = wordResults)
    }

    private fun normalize(text: String): List<String> = text
        .lowercase()
        .replace(Regex("[^a-z0-9'\\s]"), "")
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }

    /**
     * Aligns [target] against [recognized] using word-level edit distance (Wagner-Fischer DP,
     * same idea as Levenshtein distance but over word tokens instead of characters). This avoids
     * a single inserted/deleted word cascading into every later word looking "wrong" under a
     * naive positional comparison. Returns one [WordMatchResult] per target word: correct only
     * when the alignment matches it 1:1 to the same recognized word; substituted or missing
     * target words are marked incorrect. Extra (inserted) recognized words have no target
     * counterpart and are not represented in the result.
     */
    private fun alignWords(target: List<String>, recognized: List<String>): List<WordMatchResult> {
        val n = target.size
        val m = recognized.size
        val dp = Array(n + 1) { IntArray(m + 1) }
        for (i in 0..n) dp[i][0] = i
        for (j in 0..m) dp[0][j] = j
        for (i in 1..n) {
            for (j in 1..m) {
                dp[i][j] = if (target[i - 1] == recognized[j - 1]) {
                    dp[i - 1][j - 1]
                } else {
                    1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
                }
            }
        }

        val results = ArrayDeque<WordMatchResult>()
        var i = n
        var j = m
        while (i > 0 || j > 0) {
            when {
                i > 0 && j > 0 && target[i - 1] == recognized[j - 1] && dp[i][j] == dp[i - 1][j - 1] -> {
                    results.addFirst(WordMatchResult(target[i - 1], isCorrect = true))
                    i--
                    j--
                }
                i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1 -> {
                    // Substitution: a different word was recognized in this target word's place.
                    results.addFirst(WordMatchResult(target[i - 1], isCorrect = false))
                    i--
                    j--
                }
                i > 0 && dp[i][j] == dp[i - 1][j] + 1 -> {
                    // Deletion: this target word has no counterpart in the transcript.
                    results.addFirst(WordMatchResult(target[i - 1], isCorrect = false))
                    i--
                }
                else -> {
                    // Insertion: an extra recognized word with no target counterpart.
                    j--
                }
            }
        }
        return results.toList()
    }
}
