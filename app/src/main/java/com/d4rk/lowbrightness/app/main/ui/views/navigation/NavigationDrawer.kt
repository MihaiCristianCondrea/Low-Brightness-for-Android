package com.d4rk.lowbrightness.app.main.ui.views.navigation

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.d4rk.android.libs.apptoolkit.app.main.ui.navigation.handleNavigationItemClick
import com.d4rk.android.libs.apptoolkit.app.main.ui.views.dialogs.ChangelogDialog
import com.d4rk.android.libs.apptoolkit.app.main.ui.views.navigation.NavigationDrawerItemContent
import com.d4rk.android.libs.apptoolkit.core.ui.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.ui.navigation.NavigationState
import com.d4rk.android.libs.apptoolkit.core.ui.navigation.Navigator
import com.d4rk.android.libs.apptoolkit.core.ui.views.modifiers.hapticDrawerSwipe
import com.d4rk.android.libs.apptoolkit.core.ui.views.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.window.AppWindowWidthSizeClass
import com.d4rk.lowbrightness.app.main.ui.MainScaffoldContent
import com.d4rk.lowbrightness.app.main.ui.state.MainUiState
import com.d4rk.lowbrightness.app.main.utils.constants.AppNavKey
import com.d4rk.lowbrightness.app.main.utils.constants.BrightnessRoute
import com.d4rk.lowbrightness.app.main.utils.constants.NavigationRoutes
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun NavigationDrawer(
    uiState: MainUiState,
    windowWidthSizeClass: AppWindowWidthSizeClass,
    navigationState: NavigationState<AppNavKey>,
    navigator: Navigator<AppNavKey>,
) {
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val context: Context = LocalContext.current
    val changelogUrl: String = koinInject(qualifier = named("github_changelog"))

    val showChangelog = rememberSaveable { mutableStateOf(false) }

    val appRouteHandlers = remember(navigator) {
        mapOf(
            NavigationRoutes.ROUTE_BRIGHTNESS to { _: NavigationDrawerItem ->
                navigator.navigate(BrightnessRoute)
            },
        )
    }

    ModalNavigationDrawer(
        modifier = Modifier.hapticDrawerSwipe(state = drawerState),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                LargeVerticalSpacer()

                uiState.navigationDrawerItems.forEach { item: NavigationDrawerItem ->
                    NavigationDrawerItemContent(
                        item = item,
                        dividerRoutes = persistentSetOf(), // no dividers needed
                        handleNavigationItemClick = {
                            handleNavigationItemClick(
                                context = context,
                                item = item,
                                drawerState = drawerState,
                                coroutineScope = coroutineScope,
                                onChangelogRequested = { showChangelog.value = true },
                                additionalHandlers = appRouteHandlers,
                            )
                        }
                    )
                }
            }
        }
    ) {
        MainScaffoldContent(
            drawerState = drawerState,
            windowWidthSizeClass = windowWidthSizeClass,
            navigationState = navigationState,
            navigator = navigator,
        )
    }

    if (showChangelog.value) {
        ChangelogDialog(
            changelogUrl = changelogUrl,
            onDismiss = { showChangelog.value = false }
        )
    }
}
