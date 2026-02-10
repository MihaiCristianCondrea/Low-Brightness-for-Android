package com.d4rk.lowbrightness.core.di.modules.app.modules

import com.d4rk.android.libs.apptoolkit.app.consent.data.local.ConsentPreferencesDataSource
import com.d4rk.android.libs.apptoolkit.app.consent.data.remote.datasource.ConsentRemoteDataSource
import com.d4rk.android.libs.apptoolkit.app.consent.data.remote.datasource.UmpConsentRemoteDataSource
import com.d4rk.android.libs.apptoolkit.app.consent.data.repository.ConsentRepositoryImpl
import com.d4rk.android.libs.apptoolkit.app.consent.domain.repository.ConsentRepository
import com.d4rk.android.libs.apptoolkit.app.consent.domain.usecases.ApplyConsentSettingsUseCase
import com.d4rk.android.libs.apptoolkit.app.consent.domain.usecases.ApplyInitialConsentUseCase
import com.d4rk.android.libs.apptoolkit.app.consent.domain.usecases.RequestConsentUseCase
import com.d4rk.android.libs.apptoolkit.core.data.local.datastore.CommonDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

val consentModule: Module = module {
    single<ConsentPreferencesDataSource> { get<CommonDataStore>() }
    single<ConsentRemoteDataSource> { UmpConsentRemoteDataSource() }
    single<ConsentRepository> {
        ConsentRepositoryImpl(
            remote = get(),
            local = get(),
            configProvider = get(),
            firebaseController = get(),
        )
    }
    single { RequestConsentUseCase(repository = get(), firebaseController = get()) }
    single { ApplyInitialConsentUseCase(repository = get(), firebaseController = get()) }
    single { ApplyConsentSettingsUseCase(repository = get(), firebaseController = get()) }
}
