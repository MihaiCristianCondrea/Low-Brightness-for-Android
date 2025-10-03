package com.d4rk.lowbrightness.core.di.modules


import com.d4rk.android.libs.apptoolkit.app.main.data.repository.MainRepositoryImpl
import com.d4rk.android.libs.apptoolkit.app.main.domain.repository.NavigationRepository
import com.d4rk.android.libs.apptoolkit.app.onboarding.utils.interfaces.providers.OnboardingProvider
import com.d4rk.android.libs.apptoolkit.data.client.KtorClient
import com.d4rk.android.libs.apptoolkit.data.core.ads.AdsCoreManager
import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.app.main.ui.MainViewModel
import com.d4rk.lowbrightness.app.onboarding.utils.interfaces.providers.AppOnboardingProvider
import com.d4rk.lowbrightness.core.data.datastore.DataStore
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule : Module = module {
    single<DataStore> { DataStore(context = get(), dispatchers = get()) }
    single<AdsCoreManager> { AdsCoreManager(context = get(), buildInfoProvider = get(), dispatchers = get()) }
    single { KtorClient.createClient(enableLogging = BuildConfig.DEBUG) }

    single<OnboardingProvider> { AppOnboardingProvider() }

    single<NavigationRepository> { MainRepositoryImpl(dispatchers = get()) }

    viewModel { MainViewModel(navigationRepository = get()) }
}