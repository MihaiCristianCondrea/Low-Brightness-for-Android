@file:Suppress("DEPRECATION")

package com.d4rk.lowbrightness

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.AppThemeConfig
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.ColorPalette
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.ThemePaletteProvider
import com.d4rk.android.libs.apptoolkit.core.BaseCoreManager
import com.d4rk.android.libs.apptoolkit.core.data.local.datastore.CommonDataStore
import com.d4rk.android.libs.apptoolkit.core.data.remote.ads.AdsCoreManager
import com.d4rk.android.libs.apptoolkit.core.utils.constants.colorscheme.StaticPaletteIds
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.date.isChristmasSeason
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.date.isHalloweenSeason
import com.d4rk.lowbrightness.core.di.initializeKoin
import com.d4rk.lowbrightness.core.utils.constants.ads.AdsConstants
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.koin.android.ext.android.getKoin
import java.time.LocalDate
import java.time.ZoneId

lateinit var appContext: Context

class LowBrightness : BaseCoreManager(), DefaultLifecycleObserver {

    private var currentActivity: Activity? = null

    private val adsCoreManager: AdsCoreManager by lazy { getKoin().get<AdsCoreManager>() }

    override fun onCreate() {
        initializeKoin(context = this)
        applyDefaultColorPalette()
        super<BaseCoreManager>.onCreate()
        appContext = applicationContext
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer = this)
    }

    override suspend fun onInitializeApp(): Unit = supervisorScope {
        listOf(
            async { initializeAds() }
        ).awaitAll()
    }

    private suspend fun initializeAds() {
        adsCoreManager.initializeAds(AdsConstants.APP_OPEN_UNIT_ID)
    }

    private fun applyDefaultColorPalette() {
        val colorPalette: ColorPalette = resolveDefaultColorPalette()
        AppThemeConfig.customLightScheme = colorPalette.lightColorScheme
        AppThemeConfig.customDarkScheme = colorPalette.darkColorScheme
        ThemePaletteProvider.defaultPalette = colorPalette
    }

    private fun resolveDefaultColorPalette(): ColorPalette {
        val dataStore: CommonDataStore = getKoin().get()

        val hasInteractedWithSettings: Boolean = runBlocking {
            dataStore.settingsInteracted.first()
        }

        if (!hasInteractedWithSettings) {
            val staticPaletteId: String = runBlocking {
                dataStore.staticPaletteId.first()
            }

            val today: LocalDate = LocalDate.now(ZoneId.systemDefault())
            val shouldUseSeasonalPalette: Boolean = staticPaletteId == StaticPaletteIds.DEFAULT

            if (shouldUseSeasonalPalette) {
                return when {
                    today.isHalloweenSeason -> ThemePaletteProvider.paletteById(StaticPaletteIds.HALLOWEEN)
                    today.isChristmasSeason -> ThemePaletteProvider.paletteById(StaticPaletteIds.CHRISTMAS)
                    else -> getKoin().get()
                }
            }
        }

        return getKoin().get()
    }

    override fun onStart(owner: LifecycleOwner) {
        currentActivity?.let { adsCoreManager.showAdIfAvailable(it, owner.lifecycleScope) }
    }

    override fun onResume(owner: LifecycleOwner) {
        owner.lifecycleScope.launch {
            billingRepository.processPastPurchases()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {
        if (currentActivity === activity) currentActivity = null
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity === activity) currentActivity = null
    }
}