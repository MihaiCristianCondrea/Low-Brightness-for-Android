package com.d4rk.lowbrightness.app.main.domain.usecases

import com.d4rk.android.libs.apptoolkit.app.main.domain.repository.NavigationRepository
import com.d4rk.android.libs.apptoolkit.core.domain.repository.FirebaseController
import com.d4rk.android.libs.apptoolkit.core.ui.model.navigation.NavigationDrawerItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class GetNavigationDrawerItemsUseCase(
    private val navigationRepository: NavigationRepository,
    private val firebaseController: FirebaseController,
) {

    operator fun invoke(): Flow<List<NavigationDrawerItem>> {
        return navigationRepository.getNavigationDrawerItems()
            .onStart {
                firebaseController.logBreadcrumb(
                    message = "Navigation drawer load started",
                    attributes = mapOf("source" to "GetNavigationDrawerItemsUseCase"),
                )
            }
    }
}
