package com.d4rk.lowbrightness.core.di.modules.settings.modules

import com.d4rk.android.libs.apptoolkit.app.diagnostics.data.repository.UsageAndDiagnosticsRepositoryImpl
import com.d4rk.android.libs.apptoolkit.app.diagnostics.domain.repository.UsageAndDiagnosticsRepository
import com.d4rk.android.libs.apptoolkit.app.diagnostics.ui.UsageAndDiagnosticsViewModel
import com.d4rk.android.libs.apptoolkit.data.local.datastore.CommonDataStore
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val usageAndDiagnosticsModule: Module =
    module {
        single<UsageAndDiagnosticsRepository> {
            UsageAndDiagnosticsRepositoryImpl(
                dataSource = get<CommonDataStore>(),
                configProvider = get(),
                dispatchers = get(),
            )
        }

        viewModel {
            UsageAndDiagnosticsViewModel(repository = get(), firebaseController = get())
        }
    }
