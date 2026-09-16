package com.speakeng.app.feature.home.domain.usecase

import com.speakeng.app.feature.home.domain.model.HomeSummary
import com.speakeng.app.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeSummaryUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): Result<HomeSummary> = homeRepository.getHomeSummary()
}
