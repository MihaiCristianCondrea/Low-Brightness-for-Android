package com.d4rk.lowbrightness.app.main.ui.state

import androidx.compose.runtime.Immutable
import com.d4rk.android.libs.apptoolkit.core.ui.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.utils.platform.UiTextHelper
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MainUiState(
    val showSnackbar: Boolean = false,
    val snackbarMessage: UiTextHelper = UiTextHelper.DynamicString(""),
    val showDialog: Boolean = false,
    val navigationDrawerItems: ImmutableList<NavigationDrawerItem> = persistentListOf()
)
