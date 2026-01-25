package com.d4rk.lowbrightness.app.brightness.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.core.ui.navigation.NavigationEntryBuilder
import com.d4rk.android.libs.apptoolkit.core.ui.window.AppWindowWidthSizeClass
import com.d4rk.lowbrightness.app.brightness.ui.BrightnessScreen
import com.d4rk.lowbrightness.app.main.ui.views.navigation.AppNavigationEntryContext
import com.d4rk.lowbrightness.app.main.utils.constants.AppNavKey
import com.d4rk.lowbrightness.app.main.utils.constants.BrightnessRoute

fun brightnessEntryBuilder(
    context: AppNavigationEntryContext,
): NavigationEntryBuilder<AppNavKey> = {
    entry<BrightnessRoute> {
        BrightnessEntryContent(
            paddingValues = context.paddingValues,
            windowWidthSizeClass = context.windowWidthSizeClass,
        )
    }
}

@Composable
private fun BrightnessEntryContent(
    paddingValues: PaddingValues,
    windowWidthSizeClass: AppWindowWidthSizeClass,
) {
    if (windowWidthSizeClass == AppWindowWidthSizeClass.Compact) {
        BrightnessScreen(paddingValues = paddingValues)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.fillMaxWidth(0.6f)) {
                BrightnessScreen(paddingValues = PaddingValues())
            }
        }
    }
}
