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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.d4rk.android.libs.apptoolkit.app.consent.domain.usecases.ApplyInitialConsentUseCase
import com.d4rk.android.libs.apptoolkit.app.main.ui.factory.GmsHostFactory
import com.d4rk.android.libs.apptoolkit.app.startup.ui.StartupActivity
import com.d4rk.android.libs.apptoolkit.app.theme.ui.style.AppTheme
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.context.openActivity
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.ext.openAccessibilitySettings
import com.d4rk.lowbrightness.app.brightness.domain.ext.requestAllPermissionsAndShow
import com.d4rk.lowbrightness.app.brightness.domain.ext.requestSystemAlertWindowPermission
import com.d4rk.lowbrightness.app.brightness.domain.receivers.NightScreenReceiver
import com.d4rk.lowbrightness.app.brightness.domain.services.isAccessibilityServiceRunning
import com.d4rk.lowbrightness.app.brightness.ui.views.dialogs.ShowAccessibilityDisclosure
import com.d4rk.lowbrightness.app.main.ui.contract.MainAction
import com.d4rk.lowbrightness.app.main.ui.contract.MainEvent
import com.d4rk.lowbrightness.core.data.local.datastore.DataStore
import com.d4rk.lowbrightness.core.utils.extensions.showToast
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PERMISSION_AND_SHOW_ACTION = "requestPermissionsAndShow"
    }

    private val dataStore: DataStore by inject()
    private val dispatchers: DispatcherProvider by inject()
    private val viewModel: MainViewModel by viewModel()
    private val applyInitialConsentUseCase: ApplyInitialConsentUseCase by inject()
    private val gmsHostFactory: GmsHostFactory by inject()

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
        observeActions()
    }

    override fun onResume() {
        super.onResume()
        handleGmsEvents()
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
                    async(dispatchers.io) { applyInitialConsentUseCase.invoke() }
                awaitAll(adsInitialization, consentInitialization)
            }
        }
    }

    private fun handleStartup(intentAction: String?) {
        lifecycleScope.launch {
            val isFirstLaunch: Boolean = withContext(context = dispatchers.io) { dataStore.startup.first() }
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
                            val accessibilitySettingsIntent =
                                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            runCatching {
                                accessibilitySettingsLauncher.launch(accessibilitySettingsIntent)
                            }.onFailure {
                                if (!openAccessibilitySettings()) {
                                    getString(R.string.no_accessibility_permission).showToast()
                                }
                            }
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

    private fun observeActions() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionEvent.collect { action ->
                    when (action) {
                        is MainAction.ReviewOutcomeReported -> Unit
                        is MainAction.InAppUpdateResultReported -> Unit
                    }
                }
            }
        }
    }

    private fun handleGmsEvents() {
        viewModel.onEvent(event = MainEvent.RequestConsent(host = gmsHostFactory.createConsentHost(activity = this)))
        viewModel.onEvent(event = MainEvent.RequestReview(host = gmsHostFactory.createReviewHost(activity = this)))
        viewModel.onEvent(event = MainEvent.RequestInAppUpdate(host = gmsHostFactory.createUpdateHost(activity = this, launcher = updateResultLauncher)))
    }
}