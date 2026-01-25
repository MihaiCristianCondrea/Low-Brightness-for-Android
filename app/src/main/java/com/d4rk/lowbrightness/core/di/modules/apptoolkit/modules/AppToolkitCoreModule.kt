package com.d4rk.lowbrightness.core.di.modules.apptoolkit.modules

import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.app.startup.utils.interfaces.providers.AppStartupProvider
import com.d4rk.android.libs.apptoolkit.app.startup.ui.StartupViewModel
import com.d4rk.android.libs.apptoolkit.app.startup.utils.interfaces.providers.StartupProvider
import com.d4rk.android.libs.apptoolkit.core.ui.model.AppVersionInfo
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appToolkitCoreModule: Module =
    module {
        single<StartupProvider> { AppStartupProvider() }
        viewModel { StartupViewModel() }

        single<AppVersionInfo> {
            AppVersionInfo(
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE.toLong()
            )
        }
    }
