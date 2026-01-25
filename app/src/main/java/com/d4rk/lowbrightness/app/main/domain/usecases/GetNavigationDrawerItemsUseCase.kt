package com.d4rk.lowbrightness.app.main.domain.usecases

import com.d4rk.android.libs.apptoolkit.app.main.domain.repository.NavigationRepository
import com.d4rk.android.libs.apptoolkit.core.ui.model.navigation.NavigationDrawerItem
import kotlinx.coroutines.flow.Flow

class GetNavigationDrawerItemsUseCase(
    private val navigationRepository: NavigationRepository,
) {
    operator fun invoke(): Flow<List<NavigationDrawerItem>> {
        return navigationRepository.getNavigationDrawerItems()
    }
}
