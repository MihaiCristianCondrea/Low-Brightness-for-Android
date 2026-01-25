package com.d4rk.lowbrightness.app.main.utils.constants

import androidx.compose.runtime.Immutable
import com.d4rk.android.libs.apptoolkit.core.ui.model.navigation.StableNavKey
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.serialization.Serializable

@Immutable
@Serializable
sealed interface AppNavKey : StableNavKey

@Serializable
data object BrightnessRoute : AppNavKey

object NavigationRoutes {
    const val ROUTE_BRIGHTNESS: String = "brightness"

    val topLevelRoutes: ImmutableSet<AppNavKey> =
        persistentSetOf(BrightnessRoute)
}

fun String.toNavKeyOrDefault(): AppNavKey = // FIXME: Function "toNavKeyOrDefault" is never used
    when (this) {
        NavigationRoutes.ROUTE_BRIGHTNESS -> BrightnessRoute
        else -> BrightnessRoute
    }
