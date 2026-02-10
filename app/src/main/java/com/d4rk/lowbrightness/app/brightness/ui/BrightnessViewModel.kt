package com.d4rk.lowbrightness.app.brightness.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.repository.FirebaseController
import com.d4rk.android.libs.apptoolkit.core.ui.base.LoggedScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.ui.state.ScreenState
import com.d4rk.android.libs.apptoolkit.core.ui.state.UiStateScreen
import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.app.brightness.domain.usecases.GetPromotedAppUseCase
import com.d4rk.lowbrightness.app.brightness.ui.contract.BrightnessAction
import com.d4rk.lowbrightness.app.brightness.ui.contract.BrightnessEvent
import com.d4rk.lowbrightness.app.brightness.ui.state.BrightnessUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class BrightnessViewModel(
    private val getPromotedAppUseCase: GetPromotedAppUseCase,
    private val dispatchers: DispatcherProvider,
    firebaseController: FirebaseController,
) : LoggedScreenViewModel<BrightnessUiState, BrightnessEvent, BrightnessAction>(
    initialState = UiStateScreen(data = BrightnessUiState()),
    firebaseController = firebaseController,
    screenName = SCREEN_NAME,
) {

    private var loadPromotedAppJob: Job? = null

    init {
        onEvent(BrightnessEvent.LoadPromotedApp)
    }

    override fun handleEvent(event: BrightnessEvent) {
        when (event) {
            BrightnessEvent.LoadPromotedApp -> loadPromotedApp()
        }
    }

    private fun loadPromotedApp() {
        loadPromotedAppJob = loadPromotedAppJob.restart {
            launchReport(
                action = ACTION_LOAD_PROMOTED_APP,
                extra = mapOf("application_id" to BuildConfig.APPLICATION_ID),
                block = {
                    val suggestedApp = withContext(dispatchers.io) {
                        getPromotedAppUseCase(BuildConfig.APPLICATION_ID)
                    }

                    updateStateThreadSafe {
                        val current = screenState.value
                        val data = current.data ?: BrightnessUiState()

                        screenState.value = current.copy(
                            screenState = ScreenState.Success(),
                            data = data.copy(promotedApp = suggestedApp),
                        )
                    }
                },
                onError = {
                    // No-op
                },
            )
        }
    }

    private companion object {
        private const val SCREEN_NAME: String = "Brightness"
        private const val ACTION_LOAD_PROMOTED_APP: String = "load_promoted_app"
    }
}
