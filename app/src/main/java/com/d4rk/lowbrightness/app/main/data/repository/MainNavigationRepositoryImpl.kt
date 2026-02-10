package com.d4rk.lowbrightness.app.main.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import com.d4rk.android.libs.apptoolkit.app.main.domain.repository.NavigationRepository
import com.d4rk.android.libs.apptoolkit.app.main.utils.constants.NavigationDrawerRoutes
import com.d4rk.android.libs.apptoolkit.core.domain.repository.FirebaseController
import com.d4rk.android.libs.apptoolkit.core.ui.model.navigation.NavigationDrawerItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import com.d4rk.android.libs.apptoolkit.R as ToolkitR

class MainNavigationRepositoryImpl(
    private val firebaseController: FirebaseController,
) : NavigationRepository {
    override fun getNavigationDrawerItems(): Flow<List<NavigationDrawerItem>> =
        flow {
            emit(
                listOf(
                    NavigationDrawerItem(
                        title = ToolkitR.string.settings,
                        selectedIcon = Icons.Outlined.Settings,
                        route = NavigationDrawerRoutes.ROUTE_SETTINGS,
                    ),
                    NavigationDrawerItem(
                        title = ToolkitR.string.help_and_feedback,
                        selectedIcon = Icons.AutoMirrored.Outlined.HelpOutline,
                        route = NavigationDrawerRoutes.ROUTE_HELP_AND_FEEDBACK,
                    ),
                    NavigationDrawerItem(
                        title = ToolkitR.string.updates,
                        selectedIcon = Icons.AutoMirrored.Outlined.EventNote,
                        route = NavigationDrawerRoutes.ROUTE_UPDATES,
                    ),
                    NavigationDrawerItem(
                        title = ToolkitR.string.share,
                        selectedIcon = Icons.Outlined.Share,
                        route = NavigationDrawerRoutes.ROUTE_SHARE,
                    )
                )
            )
        }.onStart {
            firebaseController.logBreadcrumb(
                message = "Navigation drawer items requested",
                attributes = mapOf("source" to "MainNavigationRepositoryImpl"),
            )
        }
}
