package com.d4rk.lowbrightness.core.di.modules.settings.modules

import com.d4rk.android.libs.apptoolkit.app.about.data.repository.AboutRepositoryImpl
import com.d4rk.android.libs.apptoolkit.app.about.domain.repository.AboutRepository
import com.d4rk.android.libs.apptoolkit.app.about.domain.usecases.CopyDeviceInfoUseCase
import com.d4rk.android.libs.apptoolkit.app.about.domain.usecases.GetAboutInfoUseCase
import com.d4rk.android.libs.apptoolkit.app.about.ui.AboutViewModel
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.AboutSettingsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.BuildInfoProvider
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.AppAboutSettingsProvider
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.AppBuildInfoProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
val aboutModule: Module = module {
    single<AboutSettingsProvider> { AppAboutSettingsProvider(context = get()) }
    single<BuildInfoProvider> { AppBuildInfoProvider() }
    single<AboutRepository> { AboutRepositoryImpl(deviceProvider = get(), buildInfoProvider = get(), context = get(), firebaseController = get()) }
    single<GetAboutInfoUseCase> { GetAboutInfoUseCase(repository = get(), firebaseController = get()) }
    single<CopyDeviceInfoUseCase> { CopyDeviceInfoUseCase(repository = get(), firebaseController = get()) }

    viewModel {
        AboutViewModel(
            getAboutInfo = get(),
            copyDeviceInfo = get(),
            dispatchers = get(),
            firebaseController = get(),
        )
    }
}
