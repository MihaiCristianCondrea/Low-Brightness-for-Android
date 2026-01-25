package com.d4rk.lowbrightness.app.brightness.domain.repository

import com.d4rk.lowbrightness.app.brightness.domain.model.PromotedApp

interface PromotedAppsRepository {
    suspend fun fetchPromotedApps(url: String): List<PromotedApp>
}
