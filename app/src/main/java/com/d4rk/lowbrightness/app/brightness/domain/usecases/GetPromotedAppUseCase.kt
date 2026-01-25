package com.d4rk.lowbrightness.app.brightness.domain.usecases

import com.d4rk.lowbrightness.app.brightness.domain.model.PromotedApp
import com.d4rk.lowbrightness.app.brightness.domain.repository.PromotedAppsRepository

class GetPromotedAppUseCase(
    private val repository: PromotedAppsRepository,
    private val developerAppsApiUrl: String,
) {
    suspend operator fun invoke(currentPackage: String): PromotedApp? {
        return repository
            .fetchPromotedApps(developerAppsApiUrl)
            .filter { app ->
                app.category.equals("tools", true) || app.category.equals("utilities", true)
            }
            .filterNot { app -> app.packageName == currentPackage }
            .randomOrNull()
    }
}
