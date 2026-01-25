package com.d4rk.lowbrightness.app.brightness.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.utils.constants.api.ApiConstants
import com.d4rk.android.libs.apptoolkit.core.utils.constants.api.ApiEnvironments
import com.d4rk.android.libs.apptoolkit.core.utils.constants.api.ApiLanguages
import com.d4rk.android.libs.apptoolkit.core.utils.constants.api.ApiPaths
import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.app.brightness.domain.model.PromotedApp
import com.d4rk.lowbrightness.app.brightness.domain.repository.PromotedAppsRepository

class GetPromotedAppUseCase(
    private val repository: PromotedAppsRepository
) {
    suspend operator fun invoke(currentPackage: String, language: String = ApiLanguages.DEFAULT): PromotedApp? {
        val environment =
            if (BuildConfig.DEBUG) ApiEnvironments.ENV_DEBUG else ApiEnvironments.ENV_RELEASE

        val urlString =
            "${ApiConstants.BASE_REPOSITORY_URL}/$environment/$language/${ApiPaths.DEVELOPER_APPS_API}"
        return repository
            .fetchPromotedApps(urlString)
            .asSequence()
            .filter { app ->
                app.category.equals("tools", ignoreCase = true) ||
                        app.category.equals("utilities", ignoreCase = true)
            }
            .filterNot { app -> app.packageName == currentPackage }
            .toList()
            .randomOrNull()
    }
}
