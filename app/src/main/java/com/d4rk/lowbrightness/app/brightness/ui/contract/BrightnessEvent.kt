package com.d4rk.lowbrightness.app.brightness.ui.contract

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent

sealed interface BrightnessEvent : UiEvent {
    data object LoadPromotedApp : BrightnessEvent
}
