package com.d4rk.lowbrightness.app.settings.settings.utils.providers

import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.BuildInfoProvider
import com.d4rk.lowbrightness.BuildConfig

class AppBuildInfoProvider : BuildInfoProvider {

    override val packageName: String get() = BuildConfig.APPLICATION_ID

    override val appVersion: String get() = BuildConfig.VERSION_NAME

    override val appVersionCode: Int
        get() {
            return BuildConfig.VERSION_CODE
        }

    override val isDebugBuild: Boolean
        get() {
            return BuildConfig.DEBUG
        }
}
