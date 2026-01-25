package com.d4rk.lowbrightness.core.di.modules.settings.modules

import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.ColorPalette
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.google.blue.bluePalette
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.google.green.greenPalette
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.google.red.redPalette
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.google.yellow.yellowPalette
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.monochrome.monochromePalette
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.rose.rosePalette
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.colors.special.christmas.christmasPalette
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val themeModule: Module = module {
    single(named("monochromePalette")) { monochromePalette }
    single(named("bluePalette")) { bluePalette }
    single(named("greenPalette")) { greenPalette }
    single(named("redPalette")) { redPalette }
    single(named("yellowPalette")) { yellowPalette }
    single(named("rosePalette")) { rosePalette }
    single(named("christmasPalette")) { christmasPalette }

    single<ColorPalette> { get(named("monochromePalette")) }
}
