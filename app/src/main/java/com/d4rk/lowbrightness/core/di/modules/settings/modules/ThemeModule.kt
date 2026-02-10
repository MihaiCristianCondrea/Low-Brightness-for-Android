package com.d4rk.lowbrightness.core.di.modules.settings.modules

import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.ColorPalette
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.monochrome.monochromePalette
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val themeModule: Module = module {
    single<ColorPalette>(named("monochromePalette")) { monochromePalette }
    single<ColorPalette> { get(named("monochromePalette")) }
}
