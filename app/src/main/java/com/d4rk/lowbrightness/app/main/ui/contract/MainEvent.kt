package com.d4rk.lowbrightness.app.main.ui.contract

import com.d4rk.android.libs.apptoolkit.app.consent.domain.model.ConsentHost
import com.d4rk.android.libs.apptoolkit.app.main.domain.model.InAppUpdateHost
import com.d4rk.android.libs.apptoolkit.app.review.domain.model.ReviewHost
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent

sealed interface MainEvent : UiEvent {
    data object LoadNavigation : MainEvent
    data class RequestConsent(val host: ConsentHost) : MainEvent
    data class RequestReview(val host: ReviewHost) : MainEvent
    data class RequestInAppUpdate(val host: InAppUpdateHost) : MainEvent
}
