package com.d4rk.lowbrightness.core.di.modules.app.modules

import com.d4rk.android.libs.apptoolkit.app.ads.data.repository.AdsSettingsRepositoryImpl
import com.d4rk.android.libs.apptoolkit.app.ads.domain.repository.AdsSettingsRepository
import com.d4rk.android.libs.apptoolkit.app.ads.domain.usecases.ObserveAdsEnabledUseCase
import com.d4rk.android.libs.apptoolkit.app.ads.domain.usecases.SetAdsEnabledUseCase
import com.d4rk.android.libs.apptoolkit.app.ads.ui.AdsSettingsViewModel
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.BuildInfoProvider
import com.d4rk.android.libs.apptoolkit.core.ui.model.ads.AdsConfig
import com.d4rk.lowbrightness.core.utils.constants.ads.AdsConstants
import com.google.android.gms.ads.AdSize
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val adsModule: Module = module {

    single<AdsSettingsRepository> {
        AdsSettingsRepositoryImpl(
            dataStore = get(),
            buildInfoProvider = get<BuildInfoProvider>(),
        )
    }
    single<ObserveAdsEnabledUseCase> { ObserveAdsEnabledUseCase(repo = get()) }
    single<SetAdsEnabledUseCase> { SetAdsEnabledUseCase(repo = get()) }

    viewModel {
        AdsSettingsViewModel(
            repository = get(),
            dispatchers = get(),
            observeAdsEnabled = get(),
            setAdsEnabled = get(),
            firebaseController = get(),
        )
    }

    single<AdsConfig>(named(name = "banner")) { AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID, adSize = AdSize.BANNER) }
    single<AdsConfig>(named(name = "banner_help_screen")) { AdsConfig(bannerAdUnitId = AdsConstants.HELP_SCREEN_BANNER_AD_UNIT_ID, adSize = AdSize.BANNER) }
    single<AdsConfig>(named(name = "banner_medium_rectangle")) { AdsConfig(bannerAdUnitId = AdsConstants.MEDIUM_RECTANGLE_BANNER_AD_UNIT_ID, adSize = AdSize.MEDIUM_RECTANGLE) }
    single<AdsConfig>(named(name = "banner_no_data")) { AdsConfig(bannerAdUnitId = AdsConstants.NO_DATA_BANNER_AD_UNIT_ID, adSize = AdSize.BANNER) }
    single<AdsConfig>(named(name = "banner_support_screen")) { AdsConfig(bannerAdUnitId = AdsConstants.SUPPORT_SCREEN_BANNER_AD_UNIT_ID, adSize = AdSize.BANNER) }
    single<AdsConfig>(named(name = "large_banner")) { AdsConfig(bannerAdUnitId = AdsConstants.LARGE_BANNER_AD_UNIT_ID, adSize = AdSize.LARGE_BANNER) }
    single<AdsConfig>(named(name = "native_ad")) { AdsConfig(bannerAdUnitId = AdsConstants.NATIVE_AD_UNIT_ID) }
    single<AdsConfig>(named(name = "app_details_native_ad")) { AdsConfig(bannerAdUnitId = AdsConstants.APP_DETAILS_NATIVE_AD_UNIT_ID) }
    single<AdsConfig>(named(name = "no_data_native_ad")) { AdsConfig(bannerAdUnitId = AdsConstants.NO_DATA_NATIVE_AD_UNIT_ID) }
    single<AdsConfig>(named(name = "help_large_banner_ad")) { AdsConfig(bannerAdUnitId = AdsConstants.HELP_NATIVE_AD_UNIT_ID, adSize = AdSize.LARGE_BANNER) }
    single<AdsConfig>(named(name = "support_native_ad")) { AdsConfig(bannerAdUnitId = AdsConstants.SUPPORT_NATIVE_AD_UNIT_ID) }
}
