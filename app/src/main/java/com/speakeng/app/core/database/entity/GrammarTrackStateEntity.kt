package com.speakeng.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Single-row table recording the epoch day the learner first opened the grammar track, so lessons can unlock by elapsed calendar days. */
@Entity(tableName = "grammar_track_state")
data class GrammarTrackStateEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val startedAtEpochDay: Long,
) {
    companion object {
        const val SINGLETON_ID = 0
    }
}
