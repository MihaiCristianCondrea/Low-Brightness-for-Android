package com.d4rk.lowbrightness.app.brightness.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.d4rk.android.libs.apptoolkit.core.ui.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.ui.views.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.ui.views.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.views.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.ext.activity
import com.d4rk.lowbrightness.app.brightness.domain.ext.openAccessibilitySettings
import com.d4rk.lowbrightness.app.brightness.domain.ext.openBatteryOptimizationSettings
import com.d4rk.lowbrightness.app.brightness.domain.ext.openPowerSaverSettings
import com.d4rk.lowbrightness.app.brightness.domain.ext.plus
import com.d4rk.lowbrightness.app.brightness.domain.ext.requestAllPermissions
import com.d4rk.lowbrightness.app.brightness.domain.ext.shouldSuggestBatteryOptimizationDialog
import com.d4rk.lowbrightness.app.brightness.domain.services.isAccessibilityServiceRunning
import com.d4rk.lowbrightness.app.brightness.ui.state.BrightnessUiState
import com.d4rk.lowbrightness.app.brightness.ui.views.cards.ActionsCard
import com.d4rk.lowbrightness.app.brightness.ui.views.BottomImage
import com.d4rk.lowbrightness.app.brightness.ui.views.cards.ColorCard
import com.d4rk.lowbrightness.app.brightness.ui.views.cards.IntensityCard
import com.d4rk.lowbrightness.app.brightness.ui.views.cards.ScheduleCard
import com.d4rk.lowbrightness.app.brightness.ui.views.cards.PromotedAppCard
import com.d4rk.lowbrightness.app.brightness.ui.views.dialogs.ShowAccessibilityDisclosure
import com.d4rk.lowbrightness.app.brightness.ui.views.dialogs.ShowBatteryOptimizationDialog
import com.d4rk.lowbrightness.app.brightness.ui.views.dialogs.requestAllPermissionsWithAccessibilityAndShow
import com.d4rk.lowbrightness.core.utils.extensions.showToast
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@Composable
fun BrightnessScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val currentContext = rememberUpdatedState(context)
    val viewModel: BrightnessViewModel = koinViewModel()
    val screenState = viewModel.uiState.collectAsStateWithLifecycle().value
    val fallbackState = screenState.data ?: BrightnessUiState()

    val mediumRectangleAdConfig: AdsConfig =
        koinInject(qualifier = named("banner_medium_rectangle"))
    val largeBannerAdConfig: AdsConfig =
        koinInject(qualifier = named("large_banner"))

    val noAccessibilityPermissionText =
        rememberUpdatedState(stringResource(R.string.no_accessibility_permission))

    val showAccessibilityDialog = remember { mutableStateOf(false) }
    val showBatteryOptimizationDialog = remember { mutableStateOf(false) }
    val runAfterPermission = remember { mutableStateOf(false) }

    val runNightScreenFlow = {
        if (isAccessibilityServiceRunning(context)) {
            requestAllPermissionsWithAccessibilityAndShow(context)
        } else {
            runAfterPermission.value = true
            showAccessibilityDialog.value = true
        }
    }

    val startForResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val context: Context = currentContext.value
        if (isAccessibilityServiceRunning(context)) {
            if (runAfterPermission.value) {
                requestAllPermissionsWithAccessibilityAndShow(context)
            } else {
                context.activity.requestAllPermissions()
            }
        } else {
            noAccessibilityPermissionText.value.showToast()
        }
    }
    ScreenStateHandler(
        screenState = screenState,
        onLoading = { LoadingScreen(paddingValues = paddingValues) },
        onEmpty = {
            BrightnessScreenContent(
                paddingValues = paddingValues,
                uiState = fallbackState,
                mediumRectangleAdConfig = mediumRectangleAdConfig,
                largeBannerAdConfig = largeBannerAdConfig,
                onRunNightScreenClick = {
                    if (context.shouldSuggestBatteryOptimizationDialog()) {
                        showBatteryOptimizationDialog.value = true
                    } else {
                        runNightScreenFlow()
                    }
                },
                onRequestPermissionsClick = {
                    if (isAccessibilityServiceRunning(context)) {
                        context.activity.requestAllPermissions()
                    } else {
                        runAfterPermission.value = false
                        showAccessibilityDialog.value = true
                    }
                }
            )
        },
        onSuccess = { uiState ->
            BrightnessScreenContent(
                paddingValues = paddingValues,
                uiState = uiState,
                mediumRectangleAdConfig = mediumRectangleAdConfig,
                largeBannerAdConfig = largeBannerAdConfig,
                onRunNightScreenClick = {
                    if (context.shouldSuggestBatteryOptimizationDialog()) {
                        showBatteryOptimizationDialog.value = true
                    } else {
                        runNightScreenFlow()
                    }
                },
                onRequestPermissionsClick = {
                    if (isAccessibilityServiceRunning(context)) {
                        context.activity.requestAllPermissions()
                    } else {
                        runAfterPermission.value = false
                        showAccessibilityDialog.value = true
                    }
                }
            )
        },
        onError = {
            BrightnessScreenContent(
                paddingValues = paddingValues,
                uiState = fallbackState,
                mediumRectangleAdConfig = mediumRectangleAdConfig,
                largeBannerAdConfig = largeBannerAdConfig,
                onRunNightScreenClick = {
                    if (context.shouldSuggestBatteryOptimizationDialog()) {
                        showBatteryOptimizationDialog.value = true
                    } else {
                        runNightScreenFlow()
                    }
                },
                onRequestPermissionsClick = {
                    if (isAccessibilityServiceRunning(context)) {
                        context.activity.requestAllPermissions()
                    } else {
                        runAfterPermission.value = false
                        showAccessibilityDialog.value = true
                    }
                }
            )
        }
    )

    if (showAccessibilityDialog.value) {
        ShowAccessibilityDisclosure(
            onDismissRequest = { showAccessibilityDialog.value = false },
            onContinue = {
                showAccessibilityDialog.value = false
                val accessibilitySettingsIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                runCatching {
                    startForResult.launch(accessibilitySettingsIntent)
                }.onFailure {
                    if (!context.openAccessibilitySettings()) {
                        noAccessibilityPermissionText.value.showToast()
                    }
                }
            }
        )
    }

    if (showBatteryOptimizationDialog.value) {
        ShowBatteryOptimizationDialog(
            onDismissRequest = { showBatteryOptimizationDialog.value = false },
            onContinue = {
                showBatteryOptimizationDialog.value = false
                runNightScreenFlow()
            },
            onDisableBatteryOptimization = {
                showBatteryOptimizationDialog.value = false
                context.openBatteryOptimizationSettings()
                runNightScreenFlow()
            },
            onOpenPowerSaverSettings = {
                context.openPowerSaverSettings()
            }
        )
    }
}

@Composable
private fun BrightnessScreenContent(
    paddingValues: PaddingValues,
    uiState: BrightnessUiState,
    mediumRectangleAdConfig: AdsConfig,
    largeBannerAdConfig: AdsConfig,
    onRunNightScreenClick: () -> Unit,
    onRequestPermissionsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                PaddingValues(horizontal = SizeConstants.LargeIncreasedSize) + paddingValues
            ),
    ) {
        IntensityCard()
        ColorCard()
        AdBanner(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SizeConstants.MediumSize),
            adsConfig = mediumRectangleAdConfig
        )
        ScheduleCard()
        ActionsCard(
            onRunNightScreenClick = onRunNightScreenClick,
            onRequestPermissionsClick = onRequestPermissionsClick
        )
        AdBanner(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SizeConstants.MediumSize),
            adsConfig = mediumRectangleAdConfig
        )
        uiState.promotedApp?.let { promotedApp ->
            PromotedAppCard(app = promotedApp)
        }
        BottomImage()
        AdBanner(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SizeConstants.MediumSize),
            adsConfig = largeBannerAdConfig
        )
    }
}
