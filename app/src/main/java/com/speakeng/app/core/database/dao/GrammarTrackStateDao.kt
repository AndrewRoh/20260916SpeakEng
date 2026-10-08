package com.speakeng.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.speakeng.app.core.database.entity.GrammarTrackStateEntity

@Dao
interface GrammarTrackStateDao {
    @Query("SELECT * FROM grammar_track_state WHERE id = 0")
    suspend fun get(): GrammarTrackStateEntity?

    @Upsert
    suspend fun upsert(entity: GrammarTrackStateEntity)
}
