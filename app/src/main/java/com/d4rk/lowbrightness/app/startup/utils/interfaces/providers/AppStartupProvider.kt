package com.d4rk.lowbrightness.app.startup.utils.interfaces.providers

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import com.d4rk.android.libs.apptoolkit.app.onboarding.ui.OnboardingActivity
import com.d4rk.android.libs.apptoolkit.app.startup.utils.interfaces.providers.StartupProvider
import javax.inject.Inject

class AppStartupProvider @Inject constructor() : StartupProvider {
    override val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyArray()
    }

    override fun getNextIntent(context: Context) = Intent(context, OnboardingActivity::class.java)
}