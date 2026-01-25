package com.d4rk.lowbrightness.app.main.ui.views.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Stable
import com.d4rk.android.libs.apptoolkit.core.ui.navigation.NavigationEntryBuilder
import com.d4rk.android.libs.apptoolkit.core.ui.window.AppWindowWidthSizeClass
import com.d4rk.lowbrightness.app.brightness.ui.navigation.brightnessEntryBuilder
import com.d4rk.lowbrightness.app.main.utils.constants.AppNavKey

/**
 * Context shared by all navigation entry builders in the app module.
 */
@Stable
data class AppNavigationEntryContext(
    val paddingValues: PaddingValues,
    val windowWidthSizeClass: AppWindowWidthSizeClass,
)

/**
 * Default app navigation builders that can be extended with additional entries.
 */
fun appNavigationEntryBuilders(
    context: AppNavigationEntryContext,
    additionalEntryBuilders: List<NavigationEntryBuilder<AppNavKey>> = emptyList(),
): List<NavigationEntryBuilder<AppNavKey>> = buildList {
    addAll(defaultAppNavigationEntryBuilders(context))
    addAll(additionalEntryBuilders)
}

private fun defaultAppNavigationEntryBuilders(
    context: AppNavigationEntryContext,
): List<NavigationEntryBuilder<AppNavKey>> = listOf(
    brightnessEntryBuilder(context),
)