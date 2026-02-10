package com.d4rk.lowbrightness.core.di.modules.apptoolkit.modules

import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.app.startup.utils.interfaces.providers.AppStartupProvider
import com.d4rk.android.libs.apptoolkit.app.startup.ui.StartupViewModel
import com.d4rk.android.libs.apptoolkit.app.startup.utils.interfaces.providers.StartupProvider
import com.d4rk.android.libs.apptoolkit.core.ui.model.AppVersionInfo
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.text.toLong

val appToolkitCoreModule: Module = module {
    single<StartupProvider> { AppStartupProvider() }
    single<AppVersionInfo> { AppVersionInfo(versionName = BuildConfig.VERSION_NAME, versionCode = BuildConfig.VERSION_CODE.toLong()) }

    viewModel {
        StartupViewModel(
            requestConsentUseCase = get(),
            dispatchers = get(),
            firebaseController = get()
        )
    }
}
