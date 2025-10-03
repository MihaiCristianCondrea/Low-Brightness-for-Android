package com.d4rk.lowbrightness.core.di.modules

import com.d4rk.android.libs.apptoolkit.app.about.data.DefaultAboutRepository
import com.d4rk.android.libs.apptoolkit.app.about.domain.repository.AboutRepository
import com.d4rk.android.libs.apptoolkit.app.about.domain.usecases.CopyDeviceInfoUseCase
import com.d4rk.android.libs.apptoolkit.app.about.domain.usecases.ObserveAboutInfoUseCase
import com.d4rk.android.libs.apptoolkit.app.about.ui.AboutViewModel
import com.d4rk.android.libs.apptoolkit.app.advanced.data.CacheRepository
import com.d4rk.android.libs.apptoolkit.app.advanced.data.DefaultCacheRepository
import com.d4rk.android.libs.apptoolkit.app.advanced.ui.AdvancedSettingsViewModel
import com.d4rk.android.libs.apptoolkit.app.diagnostics.data.repository.DefaultUsageAndDiagnosticsRepository
import com.d4rk.android.libs.apptoolkit.app.diagnostics.domain.repository.UsageAndDiagnosticsRepository
import com.d4rk.android.libs.apptoolkit.app.diagnostics.ui.UsageAndDiagnosticsViewModel
import com.d4rk.android.libs.apptoolkit.app.permissions.domain.repository.PermissionsRepository
import com.d4rk.android.libs.apptoolkit.app.permissions.ui.PermissionsViewModel
import com.d4rk.android.libs.apptoolkit.app.settings.general.data.DefaultGeneralSettingsRepository
import com.d4rk.android.libs.apptoolkit.app.settings.general.domain.repository.GeneralSettingsRepository
import com.d4rk.android.libs.apptoolkit.app.settings.general.ui.GeneralSettingsViewModel
import com.d4rk.android.libs.apptoolkit.app.settings.settings.ui.SettingsViewModel
import com.d4rk.android.libs.apptoolkit.app.settings.utils.interfaces.SettingsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.AboutSettingsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.AdvancedSettingsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.BuildInfoProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.DisplaySettingsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.GeneralSettingsContentProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.PrivacySettingsProvider
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.AppAboutSettingsProvider
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.AppAdvancedSettingsProvider
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.AppBuildInfoProvider
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.AppDisplaySettingsProvider
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.AppPrivacySettingsProvider
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.AppSettingsProvider
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.AppSettingsScreens
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.PermissionsSettingsRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<SettingsProvider> { AppSettingsProvider() }

    viewModel {
        SettingsViewModel(
            settingsProvider = get(),
            dispatchers = get(),
        )
    }

    single { AppSettingsScreens() }
    single<AboutSettingsProvider> { AppAboutSettingsProvider(context = get()) }
    single<AdvancedSettingsProvider> { AppAdvancedSettingsProvider(context = get()) }
    single<DisplaySettingsProvider> { AppDisplaySettingsProvider(context = get()) }
    single<PrivacySettingsProvider> { AppPrivacySettingsProvider(context = get()) }
    single<BuildInfoProvider> { AppBuildInfoProvider(context = get()) }
    single<GeneralSettingsContentProvider> { GeneralSettingsContentProvider(displayProvider = get(), privacyProvider = get(), customScreens = get<AppSettingsScreens>().customScreens) }
    single<CacheRepository> { DefaultCacheRepository(context = get(), dispatchers = get()) }
    single<AboutRepository> {
        DefaultAboutRepository(
            deviceProvider = get(),
            configProvider = get(),
            context = get(),
            dispatchers = get(),
        )
    }
    single { ObserveAboutInfoUseCase(repository = get()) }
    single { CopyDeviceInfoUseCase(repository = get()) }
    single<GeneralSettingsRepository> {
        DefaultGeneralSettingsRepository(dispatchers = get())
    }
    viewModel {
        GeneralSettingsViewModel(repository = get())
    }

    single<PermissionsRepository> { PermissionsSettingsRepository(context = get(), dispatchers = get()) }
    viewModel {
        PermissionsViewModel(
            permissionsRepository = get(),
        )
    }

    viewModel { AdvancedSettingsViewModel(repository = get()) }

    viewModel {
        AboutViewModel(
            observeAboutInfo = get(),
            copyDeviceInfo = get(),
        )
    }

    single<UsageAndDiagnosticsRepository> {
        DefaultUsageAndDiagnosticsRepository(
            dataSource = CommonDataStore.getInstance(get()),
            configProvider = get(),
            dispatchers = get(),
        )
    }

    viewModel {
        UsageAndDiagnosticsViewModel(repository = get())
    }
}
