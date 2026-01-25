package com.d4rk.lowbrightness.core.di.modules.settings.modules

import com.d4rk.android.libs.apptoolkit.app.permissions.domain.repository.PermissionsRepository
import com.d4rk.android.libs.apptoolkit.app.permissions.ui.PermissionsViewModel
import com.d4rk.lowbrightness.app.settings.settings.utils.providers.PermissionsSettingsRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val permissionsModule: Module =
    module {
        single<PermissionsRepository> {
            PermissionsSettingsRepository(
                context = get()
            )
        }
        viewModel {
            PermissionsViewModel(
                permissionsRepository = get(),
                firebaseController = get(),
            )
        }
    }
