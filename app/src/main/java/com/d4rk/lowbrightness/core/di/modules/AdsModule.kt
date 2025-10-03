package com.d4rk.lowbrightness.core.di.modules

import com.d4rk.android.libs.apptoolkit.app.ads.data.DefaultAdsSettingsRepository
import com.d4rk.android.libs.apptoolkit.app.ads.domain.repository.AdsSettingsRepository
import com.d4rk.android.libs.apptoolkit.app.ads.ui.AdsSettingsViewModel
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.BuildInfoProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore
import com.d4rk.lowbrightness.core.utils.constants.ads.AdsConstants
import com.google.android.gms.ads.AdSize
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val adsModule : Module = module {

    single<AdsSettingsRepository> {
        DefaultAdsSettingsRepository(
            dataStore = CommonDataStore.getInstance(get()),
            buildInfoProvider = get<BuildInfoProvider>(),
            dispatchers = get()
        )
    }

    viewModel {
        AdsSettingsViewModel(repository = get())
    }

    single<AdsConfig> {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.BANNER)
    }

    single<AdsConfig>(named(name = "large_banner")) {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.LARGE_BANNER)
    }

    single<AdsConfig>(named(name = "banner_medium_rectangle")) {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.MEDIUM_RECTANGLE)
    }

    single<AdsConfig>(named(name = "no_data_banner_ad")) {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.MEDIUM_RECTANGLE)
    }

    single<AdsConfig>(named(name = "bottom_nav_bar_full_banner_ad")) {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.FULL_BANNER)
    }

    single<AdsConfig>(named(name = "help_large_banner_ad")) {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.LARGE_BANNER)
    }

    single<AdsConfig>(named(name = "support_banner_ad")) {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.MEDIUM_RECTANGLE)
    }
}