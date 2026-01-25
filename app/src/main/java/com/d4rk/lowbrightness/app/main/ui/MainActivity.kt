package com.d4rk.lowbrightness.app.main.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.d4rk.android.libs.apptoolkit.app.main.utils.InAppUpdateHelper
import com.d4rk.android.libs.apptoolkit.app.startup.ui.StartupActivity
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.AppTheme
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.context.openActivity
import com.d4rk.android.libs.apptoolkit.core.utils.platform.ConsentFormHelper
import com.d4rk.android.libs.apptoolkit.core.utils.platform.ConsentManagerHelper
import com.d4rk.android.libs.apptoolkit.core.utils.platform.ReviewHelper
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.ext.requestAllPermissionsAndShow
import com.d4rk.lowbrightness.app.brightness.domain.ext.requestSystemAlertWindowPermission
import com.d4rk.lowbrightness.app.brightness.domain.receivers.NightScreenReceiver
import com.d4rk.lowbrightness.app.brightness.domain.services.isAccessibilityServiceRunning
import com.d4rk.lowbrightness.app.brightness.ui.views.dialogs.ShowAccessibilityDisclosure
import com.d4rk.lowbrightness.core.data.local.datastore.DataStore
import com.d4rk.lowbrightness.ui.component.showToast
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PERMISSION_AND_SHOW_ACTION = "requestPermissionsAndShow"
    }

    private val dataStore: DataStore by inject()
    private val dispatchers: DispatcherProvider by inject()

    private val updateResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {}

    private val accessibilitySettingsLauncher =
        registerForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            if (isAccessibilityServiceRunning(this)) {
                requestAllPermissionsAndShow()
            } else {
                getString(R.string.no_accessibility_permission).showToast()
            }
        }

    private var keepSplashVisible: Boolean = true
    private var showAccessibilityDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashVisible }
        enableEdgeToEdge()
        initializeDependencies()
        handleStartup(intentAction = intent?.action)
        checkInAppReview()
    }

    override fun onResume() {
        super.onResume()
        checkForUpdates()
        checkUserConsent()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        doIntentAction(intent.action)
    }

    private fun initializeDependencies() {
        lifecycleScope.launch {
            coroutineScope {
                val adsInitialization =
                    async(dispatchers.default) { MobileAds.initialize(this@MainActivity) {} }
                val consentInitialization =
                    async(dispatchers.io) { ConsentManagerHelper.applyInitialConsent(dataStore) }

                awaitAll(adsInitialization, consentInitialization)
            }
        }
    }

    private fun handleStartup(intentAction: String?) {
        lifecycleScope.launch {
            val isFirstLaunch: Boolean = withContext(dispatchers.io) {
                dataStore.startup.first()
            }

            keepSplashVisible = false

            if (isFirstLaunch) {
                startStartupActivity()
            } else {
                setMainActivityContent()
                doIntentAction(intentAction)
            }
        }
    }

    private fun startStartupActivity() {
        openActivity(activityClass = StartupActivity::class.java)
        finish()
    }

    private fun setMainActivityContent() {
        setContent {
            AppTheme {
                MainScreen()

                if (showAccessibilityDialog) {
                    ShowAccessibilityDisclosure(
                        onDismissRequest = { showAccessibilityDialog = false },
                        onContinue = {
                            showAccessibilityDialog = false
                            accessibilitySettingsLauncher.launch(
                                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            )
                        }
                    )
                }
            }
        }
    }

    private fun doIntentAction(action: String?) {
        when (action) {
            REQUEST_PERMISSION_AND_SHOW_ACTION -> requestPermissionAndShow()
            else -> Unit
        }
    }

    private fun requestPermissionAndShow() {
        requestSystemAlertWindowPermission(
            onGranted = {
                if (isAccessibilityServiceRunning(this)) {
                    NightScreenReceiver.sendBroadcast(
                        context = this,
                        action = NightScreenReceiver.SHOW_DIALOG_AND_NIGHT_SCREEN_ACTION
                    )
                } else {
                    showAccessibilityDialog = true
                }
            }
        )
    }

    private fun checkUserConsent() {
        lifecycleScope.launch {
            val consentInfo: ConsentInformation = withContext(dispatchers.io) {
                UserMessagingPlatform.getConsentInformation(this@MainActivity)
            }
            ConsentFormHelper.showConsentFormIfRequired(
                activity = this@MainActivity,
                consentInfo = consentInfo
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun checkInAppReview() {
        lifecycleScope.launch {
            val (sessionCount: Int, hasPrompted: Boolean) = coroutineScope {
                val sessionCountDeferred = async(dispatchers.io) { dataStore.sessionCount.first() }
                val hasPromptedDeferred =
                    async(dispatchers.io) { dataStore.hasPromptedReview.first() }

                awaitAll(sessionCountDeferred, hasPromptedDeferred)
                sessionCountDeferred.getCompleted() to hasPromptedDeferred.getCompleted()
            }
            ReviewHelper.launchInAppReviewIfEligible(
                activity = this@MainActivity,
                sessionCount = sessionCount,
                hasPromptedBefore = hasPrompted,
                scope = this
            ) {
                launch(dispatchers.io) { dataStore.setHasPromptedReview(value = true) }
            }
            withContext(dispatchers.io) { dataStore.incrementSessionCount() }
        }
    }

    private fun checkForUpdates() {
        InAppUpdateHelper.performUpdate(
            appUpdateManager = AppUpdateManagerFactory.create(this@MainActivity),
            updateResultLauncher = updateResultLauncher,
        )
    }
}