package com.d4rk.lowbrightness.core.di.modules.app.modules

import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.app.main.data.repository.MainNavigationRepositoryImpl
import com.d4rk.lowbrightness.app.main.domain.usecases.GetNavigationDrawerItemsUseCase
import com.d4rk.lowbrightness.app.main.ui.MainViewModel
import com.d4rk.android.libs.apptoolkit.app.main.domain.repository.NavigationRepository
import com.d4rk.android.libs.apptoolkit.core.utils.constants.api.ApiLanguages
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.boolean.toApiEnvironment
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.string.developerAppsApiUrl
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule: Module = module {
    single<NavigationRepository> { MainNavigationRepositoryImpl() }
    single<GetNavigationDrawerItemsUseCase> { GetNavigationDrawerItemsUseCase(navigationRepository = get()) }
    viewModel {
        MainViewModel(
            getNavigationDrawerItemsUseCase = get(),
            firebaseController = get(),
            dispatchers = get(),
        )
    }

    single<String>(qualifier = named(name = "developer_apps_api_url")) {
        val environment = BuildConfig.DEBUG.toApiEnvironment()
        environment.developerAppsApiUrl(language = ApiLanguages.DEFAULT)
    }
}
