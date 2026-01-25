package com.d4rk.lowbrightness.core.di.modules.apptoolkit.modules

import com.d4rk.android.libs.apptoolkit.app.support.billing.BillingRepository
import com.d4rk.android.libs.apptoolkit.app.support.ui.SupportViewModel
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val supportModule: Module =
    module {
        single(createdAtStart = true) {
            val dispatchers = get<DispatcherProvider>()
            BillingRepository.getInstance(
                context = get(),
                dispatchers = dispatchers,
                externalScope = CoroutineScope(SupervisorJob() + dispatchers.io)
            )
        }

        viewModel {
            SupportViewModel(billingRepository = get(), firebaseController = get())
        }
    }
