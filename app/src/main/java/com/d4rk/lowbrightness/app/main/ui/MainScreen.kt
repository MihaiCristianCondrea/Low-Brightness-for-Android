package com.d4rk.lowbrightness.app.main.ui

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.d4rk.android.libs.apptoolkit.app.main.ui.navigation.handleNavigationItemClick
import com.d4rk.android.libs.apptoolkit.app.main.ui.views.dialogs.ChangelogDialog
import com.d4rk.android.libs.apptoolkit.app.main.ui.views.navigation.LeftNavigationRail
import com.d4rk.android.libs.apptoolkit.app.main.ui.views.navigation.MainTopAppBar
import com.d4rk.android.libs.apptoolkit.core.ui.navigation.NavigationState
import com.d4rk.android.libs.apptoolkit.core.ui.navigation.Navigator
import com.d4rk.android.libs.apptoolkit.core.ui.navigation.rememberNavigationState
import com.d4rk.android.libs.apptoolkit.core.ui.state.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.views.snackbar.DefaultSnackbarHost
import com.d4rk.android.libs.apptoolkit.core.ui.window.AppWindowWidthSizeClass
import com.d4rk.android.libs.apptoolkit.core.ui.window.rememberWindowWidthSizeClass
import com.d4rk.lowbrightness.app.main.ui.state.MainUiState
import com.d4rk.lowbrightness.app.main.ui.views.navigation.AppNavigationHost
import com.d4rk.lowbrightness.app.main.ui.views.navigation.NavigationDrawer
import com.d4rk.lowbrightness.app.main.utils.constants.AppNavKey
import com.d4rk.lowbrightness.app.main.utils.constants.BrightnessRoute
import com.d4rk.lowbrightness.app.main.utils.constants.NavigationRoutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@Composable
fun MainScreen() {
    val windowWidthSizeClass: AppWindowWidthSizeClass = rememberWindowWidthSizeClass()
    val viewModel: MainViewModel = koinViewModel()
    val screenState: UiStateScreen<MainUiState> =
        viewModel.uiState.collectAsStateWithLifecycle().value

    val uiState: MainUiState = screenState.data ?: MainUiState()

    val navigationState: NavigationState<AppNavKey> = rememberNavigationState(
        startRoute = BrightnessRoute,
        topLevelRoutes = NavigationRoutes.topLevelRoutes,
    )
    val navigator: Navigator<AppNavKey> = remember { Navigator(navigationState) }

    if (windowWidthSizeClass == AppWindowWidthSizeClass.Compact) {
        NavigationDrawer(
            uiState = uiState,
            windowWidthSizeClass = windowWidthSizeClass,
            navigationState = navigationState,
            navigator = navigator,
        )
    } else {
        MainScaffoldTabletContent(
            uiState = uiState,
            windowWidthSizeClass = windowWidthSizeClass,
            navigationState = navigationState,
            navigator = navigator,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldContent(
    drawerState: DrawerState,
    windowWidthSizeClass: AppWindowWidthSizeClass,
    navigationState: NavigationState<AppNavKey>,
    navigator: Navigator<AppNavKey>,
) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .imePadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(
                navigationIcon = if (drawerState.isOpen)
                    Icons.AutoMirrored.Outlined.MenuOpen
                else Icons.Default.Menu,
                onNavigationIconClick = { coroutineScope.launch { drawerState.open() } },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { DefaultSnackbarHost(snackbarState = snackBarHostState) },
    ) { paddingValues ->
        AppNavigationHost(
            navigationState = navigationState,
            navigator = navigator,
            paddingValues = paddingValues,
            windowWidthSizeClass = windowWidthSizeClass,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldTabletContent(
    uiState: MainUiState,
    windowWidthSizeClass: AppWindowWidthSizeClass,
    navigationState: NavigationState<AppNavKey>,
    navigator: Navigator<AppNavKey>,
) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val isRailExpanded = rememberSaveable(windowWidthSizeClass) {
        mutableStateOf(windowWidthSizeClass >= AppWindowWidthSizeClass.Expanded)
    }

    val context: Context = LocalContext.current
    val changelogUrl: String = koinInject(qualifier = named("github_changelog"))
    val showChangelog = rememberSaveable { mutableStateOf(false) }

    val currentRoute: AppNavKey = navigationState.currentRoute

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(
                navigationIcon = if (isRailExpanded.value)
                    Icons.AutoMirrored.Outlined.MenuOpen
                else Icons.Default.Menu,
                onNavigationIconClick = { isRailExpanded.value = !isRailExpanded.value },
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->
        LeftNavigationRail<AppNavKey>(
            drawerItems = uiState.navigationDrawerItems,
            currentRoute = currentRoute,
            isRailExpanded = isRailExpanded.value,
            paddingValues = paddingValues,
            onDrawerItemClick = { item ->
                handleNavigationItemClick(
                    context = context,
                    item = item,
                    onChangelogRequested = { showChangelog.value = true },
                    additionalHandlers = mapOf(
                        NavigationRoutes.ROUTE_BRIGHTNESS to { navigator.navigate(BrightnessRoute) }
                    )
                )
            },
            content = {
                AppNavigationHost(
                    navigationState = navigationState,
                    navigator = navigator,
                    paddingValues = PaddingValues(),
                    windowWidthSizeClass = windowWidthSizeClass,
                )
            }
        )
    }

    if (showChangelog.value) {
        ChangelogDialog(
            changelogUrl = changelogUrl,
            onDismiss = { showChangelog.value = false }
        )
    }
}
