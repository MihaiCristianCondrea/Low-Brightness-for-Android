package com.d4rk.lowbrightness.core.di.modules.apptoolkit.modules

import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.core.utils.constants.HelpConstants
import com.d4rk.android.libs.apptoolkit.app.help.data.local.HelpLocalDataSource
import com.d4rk.android.libs.apptoolkit.app.help.data.remote.HelpRemoteDataSource
import com.d4rk.android.libs.apptoolkit.app.help.data.repository.FaqRepositoryImpl
import com.d4rk.android.libs.apptoolkit.app.help.domain.repository.FaqRepository
import com.d4rk.android.libs.apptoolkit.app.help.domain.usecases.GetFaqUseCase
import com.d4rk.android.libs.apptoolkit.app.help.ui.HelpViewModel
import com.d4rk.android.libs.apptoolkit.app.review.domain.usecases.ForceInAppReviewUseCase
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.string.faqCatalogUrl
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val helpModule: Module = module {
    single<HelpLocalDataSource> { HelpLocalDataSource(context = get()) }
    single<HelpRemoteDataSource> { HelpRemoteDataSource(client = get()) }
    single<FaqRepository> {
        FaqRepositoryImpl(
            localDataSource = get(),
            remoteDataSource = get(),
            catalogUrl = com.d4rk.android.libs.apptoolkit.core.utils.constants.help.HelpConstants.FAQ_BASE_URL.faqCatalogUrl(
                isDebugBuild = BuildConfig.DEBUG
            ),
            productId = HelpConstants.FAQ_PRODUCT_ID,
            firebaseController = get(),
        )
    }
    single<GetFaqUseCase> { GetFaqUseCase(repository = get(), firebaseController = get()) }

    viewModel {
        HelpViewModel(
            getFaqUseCase = get(),
            forceInAppReviewUseCase = get<ForceInAppReviewUseCase>(),
            dispatchers = get<DispatcherProvider>(),
            firebaseController = get(),
        )
    }
}
