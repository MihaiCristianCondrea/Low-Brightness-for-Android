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
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.ext.activity
import com.d4rk.lowbrightness.app.brightness.domain.ext.plus
import com.d4rk.lowbrightness.app.brightness.domain.ext.requestAllPermissions
import com.d4rk.lowbrightness.app.brightness.domain.services.isAccessibilityServiceRunning
import com.d4rk.lowbrightness.app.brightness.ui.views.ActionsCard
import com.d4rk.lowbrightness.app.brightness.ui.views.BottomImage
import com.d4rk.lowbrightness.app.brightness.ui.views.ColorCard
import com.d4rk.lowbrightness.app.brightness.ui.views.IntensityCard
import com.d4rk.lowbrightness.app.brightness.ui.views.ScheduleCard
import com.d4rk.lowbrightness.app.brightness.ui.views.cards.PromotedAppCard
import com.d4rk.lowbrightness.app.brightness.ui.views.dialogs.ShowAccessibilityDisclosure
import com.d4rk.lowbrightness.app.brightness.ui.views.dialogs.requestAllPermissionsWithAccessibilityAndShow
import com.d4rk.lowbrightness.ui.component.showToast
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@Composable
fun BrightnessScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val currentContext = rememberUpdatedState(context)
    val viewModel: BrightnessViewModel = koinViewModel()
    val screenState = viewModel.uiState.collectAsStateWithLifecycle().value
    val uiState = screenState.data

    val mediumRectangleAdConfig: AdsConfig =
        koinInject(qualifier = named("banner_medium_rectangle"))
    val largeBannerAdConfig: AdsConfig =
        koinInject(qualifier = named("large_banner"))

    val noAccessibilityPermissionText =
        rememberUpdatedState(stringResource(R.string.no_accessibility_permission))

    val showAccessibilityDialog = remember { mutableStateOf(false) }
    val runAfterPermission = remember { mutableStateOf(false) }

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
            onRunNightScreenClick = {
                if (isAccessibilityServiceRunning(context)) {
                    requestAllPermissionsWithAccessibilityAndShow(context)
                } else {
                    runAfterPermission.value = true
                    showAccessibilityDialog.value = true
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
        AdBanner(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SizeConstants.MediumSize),
            adsConfig = mediumRectangleAdConfig
        )
        uiState?.promotedApp?.let { promotedApp ->
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

    if (showAccessibilityDialog.value) {
        ShowAccessibilityDisclosure(
            onDismissRequest = { showAccessibilityDialog.value = false },
            onContinue = {
                showAccessibilityDialog.value = false
                startForResult.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        )
    }
}
