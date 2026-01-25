package com.d4rk.lowbrightness.app.brightness.ui

import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.ui.state.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.state.successData
import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.app.brightness.domain.usecases.GetPromotedAppUseCase
import com.d4rk.lowbrightness.app.brightness.ui.contract.BrightnessAction
import com.d4rk.lowbrightness.app.brightness.ui.contract.BrightnessEvent
import com.d4rk.lowbrightness.app.brightness.ui.state.BrightnessUiState
import kotlinx.coroutines.launch

class BrightnessViewModel(
    private val getPromotedAppUseCase: GetPromotedAppUseCase,
    private val dispatchers: DispatcherProvider,
) : ScreenViewModel<BrightnessUiState, BrightnessEvent, BrightnessAction>(
    initialState = UiStateScreen(data = BrightnessUiState())
) {

    init {
        onEvent(BrightnessEvent.LoadPromotedApp)
    }

    override fun onEvent(event: BrightnessEvent) {
        when (event) {
            BrightnessEvent.LoadPromotedApp -> loadPromotedApp()
        }
    }

    private fun loadPromotedApp() {
        viewModelScope.launch(dispatchers.io) {
            val suggestedApp = getPromotedAppUseCase(BuildConfig.APPLICATION_ID)
            screenState.successData {
                copy(promotedApp = suggestedApp)
            }
        }
    }
}
