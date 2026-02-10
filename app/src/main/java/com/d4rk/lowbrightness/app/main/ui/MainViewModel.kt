package com.d4rk.lowbrightness.app.main.ui

import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.app.consent.domain.model.ConsentHost
import com.d4rk.android.libs.apptoolkit.app.consent.domain.usecases.RequestConsentUseCase
import com.d4rk.android.libs.apptoolkit.app.main.domain.model.InAppUpdateHost
import com.d4rk.android.libs.apptoolkit.app.main.domain.model.InAppUpdateResult
import com.d4rk.android.libs.apptoolkit.app.main.domain.usecases.RequestInAppUpdateUseCase
import com.d4rk.android.libs.apptoolkit.app.review.domain.model.ReviewHost
import com.d4rk.android.libs.apptoolkit.app.review.domain.model.ReviewOutcome
import com.d4rk.android.libs.apptoolkit.app.review.domain.usecases.RequestInAppReviewUseCase
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.Errors
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.onFailure
import com.d4rk.android.libs.apptoolkit.core.domain.repository.FirebaseController
import com.d4rk.android.libs.apptoolkit.core.ui.base.LoggedScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.ui.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.ui.state.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.ui.state.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.state.dismissSnackbar
import com.d4rk.android.libs.apptoolkit.core.ui.state.setLoading
import com.d4rk.android.libs.apptoolkit.core.ui.state.setNoData
import com.d4rk.android.libs.apptoolkit.core.ui.state.setSuccess
import com.d4rk.android.libs.apptoolkit.core.ui.state.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.errors.asUiText
import com.d4rk.android.libs.apptoolkit.core.utils.platform.UiTextHelper
import com.d4rk.lowbrightness.app.main.domain.usecases.GetNavigationDrawerItemsUseCase
import com.d4rk.lowbrightness.app.main.ui.contract.MainAction
import com.d4rk.lowbrightness.app.main.ui.contract.MainEvent
import com.d4rk.lowbrightness.app.main.ui.state.MainUiState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class MainViewModel(
    private val getNavigationDrawerItemsUseCase: GetNavigationDrawerItemsUseCase,
    private val requestConsentUseCase: RequestConsentUseCase,
    private val requestInAppReviewUseCase: RequestInAppReviewUseCase,
    private val requestInAppUpdateUseCase: RequestInAppUpdateUseCase,
    private val dispatchers: DispatcherProvider,
    firebaseController: FirebaseController,
) : LoggedScreenViewModel<MainUiState, MainEvent, MainAction>(
    initialState = UiStateScreen(data = MainUiState()),
    firebaseController = firebaseController,
    screenName = "Main",
) {

    private var navigationJob: Job? = null
    private var consentJob: Job? = null
    private var reviewJob: Job? = null
    private var updateJob: Job? = null

    init {
        onEvent(MainEvent.LoadNavigation)
    }

    override fun handleEvent(event: MainEvent) {
        when (event) {
            is MainEvent.LoadNavigation -> loadNavigationItems()
            is MainEvent.RequestConsent -> requestConsent(host = event.host)
            is MainEvent.RequestReview -> requestReview(host = event.host)
            is MainEvent.RequestInAppUpdate -> requestInAppUpdate(host = event.host)
        }
    }

    private fun loadNavigationItems() {
        startOperation(action = Actions.LOAD_NAVIGATION)
        navigationJob = navigationJob.restart {
            getNavigationDrawerItemsUseCase.invoke()
                .flowOn(dispatchers.io)
                .onStart {
                    updateStateThreadSafe {
                        screenState.dismissSnackbar()
                        screenState.setLoading()
                    }
                }
                .onEach { items: List<NavigationDrawerItem> ->
                    updateStateThreadSafe {
                        val immutable = items.toImmutableList()
                        val base = screenData ?: MainUiState()
                        val updated = base.copy(navigationDrawerItems = immutable)

                        if (items.isEmpty()) {
                            screenState.setNoData(data = updated)
                        } else {
                            screenState.setSuccess(data = updated)
                        }
                    }
                }
                .catchReport(action = Actions.LOAD_NAVIGATION) {
                    updateStateThreadSafe {
                        val base = screenData ?: MainUiState()

                        if (base.navigationDrawerItems.isEmpty()) {
                            screenState.setNoData(data = base)
                        } else {
                            screenState.setSuccess(data = base)
                        }

                        screenState.showSnackbar(
                            UiSnackbar(
                                type = ScreenMessageType.SNACKBAR,
                                message = UiTextHelper.StringResource(R.string.error_failed_to_load_navigation),
                                isError = true,
                                timeStamp = System.nanoTime(),
                            )
                        )
                    }
                }

                .launchIn(viewModelScope)
        }
    }

    private fun requestConsent(host: ConsentHost) {
        startOperation(
            action = Actions.REQUEST_CONSENT,
            extra = mapOf(ExtraKeys.HOST to host.activity::class.java.name)
        )
        consentJob = consentJob.restart {
            requestConsentUseCase.invoke(host = host)
                .flowOn(dispatchers.main)
                .onEach { result: DataState<Unit, Errors> ->
                    result.onFailure { error ->
                        updateStateThreadSafe {
                            screenState.showSnackbar(
                                UiSnackbar(
                                    type = ScreenMessageType.SNACKBAR,
                                    message = error.asUiText(),
                                    isError = true,
                                    timeStamp = System.nanoTime(),
                                )
                            )
                        }
                    }
                }
                .catchReport(
                    action = Actions.REQUEST_CONSENT,
                    extra = mapOf(ExtraKeys.HOST to host.activity::class.java.name)
                ) {
                    updateStateThreadSafe {
                        screenState.showSnackbar(
                            UiSnackbar(
                                type = ScreenMessageType.SNACKBAR,
                                message = Errors.UseCase.FAILED_TO_LOAD_CONSENT_INFO.asUiText(),
                                isError = true,
                                timeStamp = System.nanoTime(),
                            )
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun requestReview(host: ReviewHost) {
        startOperation(
            action = Actions.REQUEST_REVIEW,
            extra = mapOf(ExtraKeys.HOST to host.activity::class.java.name)
        )
        reviewJob = reviewJob.restart {
            launchReport(
                action = Actions.REQUEST_REVIEW,
                extra = mapOf(ExtraKeys.HOST to host.activity::class.java.name),
                block = {
                    val outcome = withContext(dispatchers.io) {
                        requestInAppReviewUseCase(host = host)
                    }
                    sendAction(action = MainAction.ReviewOutcomeReported(outcome = outcome))
                },
                onError = {
                    sendAction(action = MainAction.ReviewOutcomeReported(outcome = ReviewOutcome.Failed))
                }
            )
        }
    }

    private fun requestInAppUpdate(host: InAppUpdateHost) {
        startOperation(action = Actions.REQUEST_UPDATE)
        updateJob = updateJob.restart {
            requestInAppUpdateUseCase(host = host)
                .flowOn(dispatchers.io)
                .onEach { result ->
                    sendAction(action = MainAction.InAppUpdateResultReported(result = result))
                }
                .catchReport(action = Actions.REQUEST_UPDATE) {
                    sendAction(action = MainAction.InAppUpdateResultReported(result = InAppUpdateResult.Failed))
                }
                .launchIn(viewModelScope)
        }
    }

    private object Actions {
        const val LOAD_NAVIGATION: String = "loadNavigationItems"
        const val REQUEST_CONSENT: String = "requestConsent"
        const val REQUEST_REVIEW: String = "requestReview"
        const val REQUEST_UPDATE: String = "requestInAppUpdate"
    }

    private object ExtraKeys {
        const val HOST: String = "host"
    }
}
