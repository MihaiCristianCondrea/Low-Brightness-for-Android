package com.d4rk.lowbrightness.app.main.ui

import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.Errors
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.onFailure
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.onSuccess
import com.d4rk.android.libs.apptoolkit.core.domain.repository.FirebaseController
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.ui.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.ui.state.ScreenState
import com.d4rk.android.libs.apptoolkit.core.ui.state.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.state.successData
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.errors.toError
import com.d4rk.android.libs.apptoolkit.core.utils.platform.UiTextHelper
import com.d4rk.lowbrightness.app.main.domain.usecases.GetNavigationDrawerItemsUseCase
import com.d4rk.lowbrightness.app.main.ui.contract.MainAction
import com.d4rk.lowbrightness.app.main.ui.contract.MainEvent
import com.d4rk.lowbrightness.app.main.ui.state.MainUiState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class MainViewModel(
    private val getNavigationDrawerItemsUseCase: GetNavigationDrawerItemsUseCase,
    private val firebaseController: FirebaseController,
    private val dispatchers: DispatcherProvider,
) : ScreenViewModel<MainUiState, MainEvent, MainAction>(
    initialState = UiStateScreen(data = MainUiState())
) {

    init {
        onEvent(MainEvent.LoadNavigation)
    }

    override fun onEvent(event: MainEvent) {
        when (event) {
            MainEvent.LoadNavigation -> loadNavigationItems()
        }
    }

    private fun loadNavigationItems() {
        viewModelScope.launch {
            getNavigationDrawerItemsUseCase()
                .flowOn(dispatchers.io)
                .map<List<NavigationDrawerItem>, DataState<List<NavigationDrawerItem>, Errors>> { items ->
                    if (items.isEmpty()) {
                        DataState.Error(error = Errors.UseCase.NO_DATA)
                    } else {
                        DataState.Success(items)
                    }
                }
                .catch { throwable ->
                    if (throwable is CancellationException) throw throwable
                    firebaseController.reportViewModelError(
                        viewModelName = "MainViewModel",
                        action = "loadNavigationItems",
                        throwable = throwable,
                    )
                    emit(
                        DataState.Error(
                            error = throwable.toError(default = Errors.UseCase.INVALID_STATE)
                        )
                    )
                }
                .collect { result ->
                    result
                        .onSuccess { items ->
                            screenState.successData {
                                copy(
                                    navigationDrawerItems = items.toImmutableList(),
                                    showSnackbar = false,
                                    snackbarMessage = UiTextHelper.DynamicString("")
                                )
                            }
                        }
                        .onFailure {
                            val message =
                                UiTextHelper.StringResource(R.string.error_failed_to_load_navigation)
                            screenState.update { current ->
                                current.copy(
                                    screenState = ScreenState.Error(),
                                    data = current.data?.copy(
                                        showSnackbar = true,
                                        snackbarMessage = message
                                    )
                                )
                            }
                        }
                }
        }
    }
}
