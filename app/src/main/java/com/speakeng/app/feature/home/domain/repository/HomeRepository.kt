package com.speakeng.app.feature.home.domain.repository

import com.speakeng.app.feature.home.domain.model.HomeSummary

interface HomeRepository {
    suspend fun getHomeSummary(): Result<HomeSummary>
}
