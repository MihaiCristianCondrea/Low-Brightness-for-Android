package com.d4rk.lowbrightness.app.brightness.data.repository

import com.d4rk.lowbrightness.app.brightness.data.remote.PromotedAppsRemoteDataSource
import com.d4rk.lowbrightness.app.brightness.domain.model.PromotedApp
import com.d4rk.lowbrightness.app.brightness.domain.repository.PromotedAppsRepository

class PromotedAppsRepositoryImpl(
    private val remoteDataSource: PromotedAppsRemoteDataSource
) : PromotedAppsRepository {
    override suspend fun fetchPromotedApps(url: String): List<PromotedApp> =
        remoteDataSource.fetchPromotedApps(url)
}
