package com.d4rk.lowbrightness.core.di.modules.settings.modules

import com.d4rk.android.libs.apptoolkit.app.advanced.data.repository.CacheRepositoryImpl
import com.d4rk.android.libs.apptoolkit.app.advanced.domain.repository.CacheRepository
import com.d4rk.android.libs.apptoolkit.app.advanced.ui.AdvancedSettingsViewModel
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.AdvancedSettingsProvider
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.AppAdvancedSettingsProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val advancedSettingsModule: Module = module {
    single<AdvancedSettingsProvider> { AppAdvancedSettingsProvider(context = get()) }
    single<CacheRepository> { CacheRepositoryImpl(context = get(), firebaseController = get()) }

    viewModel {
        AdvancedSettingsViewModel(
            repository = get(),
            dispatchers = get(),
            firebaseController = get(),
        )
    }
}
