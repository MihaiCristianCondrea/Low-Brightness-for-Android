package com.d4rk.lowbrightness.core.di.modules.app.modules

import com.d4rk.lowbrightness.app.brightness.data.remote.PromotedAppsRemoteDataSource
import com.d4rk.lowbrightness.app.brightness.data.repository.PromotedAppsRepositoryImpl
import com.d4rk.lowbrightness.app.brightness.domain.repository.PromotedAppsRepository
import com.d4rk.lowbrightness.app.brightness.domain.usecases.GetPromotedAppUseCase
import com.d4rk.lowbrightness.app.brightness.ui.BrightnessViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val brightnessModule: Module = module {
    single { PromotedAppsRemoteDataSource() }
    single<PromotedAppsRepository> { PromotedAppsRepositoryImpl(remoteDataSource = get()) }
    single {
        GetPromotedAppUseCase(
            repository = get(),
            developerAppsApiUrl = get(named("developer_apps_api_url")),
        )
    }
    viewModel {
        BrightnessViewModel(
            getPromotedAppUseCase = get(),
            dispatchers = get(),
        )
    }
}
