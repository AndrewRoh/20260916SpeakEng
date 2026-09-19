package com.speakeng.app.feature.profile.data.repository

import com.speakeng.app.core.database.dao.ConversationDao
import com.speakeng.app.core.database.dao.PronunciationHistoryDao
import com.speakeng.app.feature.profile.domain.model.UserProfile
import com.speakeng.app.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import kotlin.math.roundToInt

/** TODO(Phase 5): source userName and sync stats with Firebase. */
class ProfileRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val pronunciationHistoryDao: PronunciationHistoryDao,
) : ProfileRepository {

    override suspend fun getUserProfile(): Result<UserProfile> = runCatching {
        val totalConversations = conversationDao.countAiReplies()
        val pronunciationAttempts = pronunciationHistoryDao.countAll()
        val averagePronunciationScore = (pronunciationHistoryDao.averageAccuracy() ?: 0.0).roundToInt()

        UserProfile(
            userName = "Learner",
            totalConversations = totalConversations,
            // No per-session timer exists yet, so this is a rough estimate rather than a
            // precisely measured duration: each conversation turn or pronunciation attempt is
            // assumed to take about SECONDS_PER_INTERACTION seconds.
            totalMinutesPracticed = ((totalConversations + pronunciationAttempts) * SECONDS_PER_INTERACTION) / 60,
            averagePronunciationScore = averagePronunciationScore,
        )
    }

    private companion object {
        const val SECONDS_PER_INTERACTION = 30
    }
}
