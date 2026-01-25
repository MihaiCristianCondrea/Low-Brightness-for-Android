package com.d4rk.lowbrightness.core.di

import android.content.Context
import com.d4rk.lowbrightness.core.di.modules.app.modules.adsModule
import com.d4rk.lowbrightness.core.di.modules.app.modules.appModule
import com.d4rk.lowbrightness.core.di.modules.app.modules.onboardingModule
import com.d4rk.lowbrightness.core.di.modules.apptoolkit.appToolkitModules
import com.d4rk.lowbrightness.core.di.modules.core.modules.coreModule
import com.d4rk.lowbrightness.core.di.modules.core.modules.dispatchersModule
import com.d4rk.lowbrightness.core.di.modules.settings.modules.themeModule
import com.d4rk.lowbrightness.core.di.modules.settings.settingsModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun initializeKoin(context: Context) {
    startKoin {
        androidContext(androidContext = context)
        modules(
            modules = buildList {
                add(dispatchersModule)
                add(coreModule)
                add(appModule)
                addAll(settingsModules)
                add(adsModule)
                addAll(appToolkitModules)
                add(themeModule)
                add(onboardingModule)
            }
        )
    }
}
