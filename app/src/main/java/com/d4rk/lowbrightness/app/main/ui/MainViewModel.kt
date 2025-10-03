package com.d4rk.lowbrightness.app.main.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.app.main.domain.repository.NavigationRepository
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.successData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.lowbrightness.app.main.domain.action.MainAction
import com.d4rk.lowbrightness.app.main.domain.action.MainEvent
import com.d4rk.lowbrightness.app.main.domain.model.UiMainScreen
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel(
    private val navigationRepository: NavigationRepository
) : ScreenViewModel<UiMainScreen , MainEvent , MainAction>(initialState = UiStateScreen(data = UiMainScreen())) {

    init {
        onEvent(event = MainEvent.LoadNavigation)
    }

    override fun onEvent(event : MainEvent) {
        when (event) {
            is MainEvent.LoadNavigation -> loadNavigationItems()
        }
    }

    private fun loadNavigationItems() {
        viewModelScope.launch {
            navigationRepository.getNavigationDrawerItems()
                .catch { error ->
                    screenState.successData {
                        copy(
                            showSnackbar = true,
                            snackbarMessage = error.message ?: "Failed to load navigation"
                        )
                    }
                }
                .collect { items ->
                    screenState.successData { copy(navigationDrawerItems = items) }
                }
        }
    }
}