package com.d4rk.lowbrightness.app.main.ui.contract

import com.d4rk.android.libs.apptoolkit.app.main.domain.model.InAppUpdateResult
import com.d4rk.android.libs.apptoolkit.app.review.domain.model.ReviewOutcome
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.ActionEvent

sealed interface MainAction : ActionEvent {
    data class ReviewOutcomeReported(val outcome: ReviewOutcome) : MainAction
    data class InAppUpdateResultReported(val result: InAppUpdateResult) : MainAction
}
