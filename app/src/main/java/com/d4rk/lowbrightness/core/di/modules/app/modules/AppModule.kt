package com.d4rk.lowbrightness.core.di.modules.app.modules

import com.d4rk.android.libs.apptoolkit.app.main.data.repository.InAppUpdateRepositoryImpl
import com.d4rk.android.libs.apptoolkit.app.main.domain.repository.InAppUpdateRepository
import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.app.main.data.repository.MainNavigationRepositoryImpl
import com.d4rk.lowbrightness.app.main.domain.usecases.GetNavigationDrawerItemsUseCase
import com.d4rk.lowbrightness.app.main.ui.MainViewModel
import com.d4rk.android.libs.apptoolkit.app.main.domain.repository.NavigationRepository
import com.d4rk.android.libs.apptoolkit.app.main.domain.usecases.RequestInAppUpdateUseCase
import com.d4rk.android.libs.apptoolkit.app.main.ui.factory.GmsHostFactory
import com.d4rk.android.libs.apptoolkit.app.review.domain.usecases.RequestInAppReviewUseCase
import com.d4rk.android.libs.apptoolkit.core.utils.constants.api.ApiLanguages
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.boolean.toApiEnvironment
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.string.developerAppsApiUrl
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule: Module = module {
    single { GmsHostFactory() }
    single<NavigationRepository> { MainNavigationRepositoryImpl(firebaseController = get()) }
    single<GetNavigationDrawerItemsUseCase> {
        GetNavigationDrawerItemsUseCase(navigationRepository = get(), firebaseController = get())
    }
    single<InAppUpdateRepository> { InAppUpdateRepositoryImpl() }
    single { RequestInAppUpdateUseCase(repository = get()) }
    viewModel {
        MainViewModel(
            getNavigationDrawerItemsUseCase = get(),
            requestConsentUseCase = get(),
            requestInAppReviewUseCase = get<RequestInAppReviewUseCase>(),
            requestInAppUpdateUseCase = get<RequestInAppUpdateUseCase>(),
            firebaseController = get(),
            dispatchers = get(),
        )
    }

    single<String>(qualifier = named(name = "developer_apps_api_url")) {
        val environment = BuildConfig.DEBUG.toApiEnvironment()
        environment.developerAppsApiUrl(language = ApiLanguages.DEFAULT)
    }
}
