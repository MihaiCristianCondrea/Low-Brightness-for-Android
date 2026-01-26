package com.d4rk.lowbrightness.core.utils.constants.ads

import com.d4rk.android.libs.apptoolkit.core.utils.constants.ads.DebugAdsConstants
import com.d4rk.lowbrightness.BuildConfig

object AdsConstants {

    val BANNER_AD_UNIT_ID : String
        get() = if (BuildConfig.DEBUG) {
            DebugAdsConstants.BANNER_AD_UNIT_ID
        }
        else {
            "ca-app-pub-5294151573817700/8570145928"
        }

    val LARGE_BANNER_AD_UNIT_ID : String
        get() = if (BuildConfig.DEBUG) {
            DebugAdsConstants.BANNER_AD_UNIT_ID
        }
        else {
            "ca-app-pub-5294151573817700/1359415448"
        }

    val MEDIUM_RECTANGLE_BANNER_AD_UNIT_ID : String
        get() = if (BuildConfig.DEBUG) {
            DebugAdsConstants.BANNER_AD_UNIT_ID
        }
        else {
            "ca-app-pub-5294151573817700/8771001606"
        }

    val NO_DATA_BANNER_AD_UNIT_ID : String
        get() = if (BuildConfig.DEBUG) {
            DebugAdsConstants.BANNER_AD_UNIT_ID
        }
        else {
            "ca-app-pub-5294151573817700/5182399940"
        }

    val HELP_SCREEN_BANNER_AD_UNIT_ID : String
        get() = if (BuildConfig.DEBUG) {
            DebugAdsConstants.BANNER_AD_UNIT_ID
        }
        else {
            "ca-app-pub-5294151573817700/9840540693"
        }

    val SUPPORT_SCREEN_BANNER_AD_UNIT_ID : String
        get() = if (BuildConfig.DEBUG) {
            DebugAdsConstants.BANNER_AD_UNIT_ID
        }
        else {
            "ca-app-pub-5294151573817700/6420170435"
        }

    val APP_OPEN_UNIT_ID : String
        get() = if (BuildConfig.DEBUG) {
            DebugAdsConstants.APP_OPEN_AD_UNIT_ID
        }
        else {
            "ca-app-pub-5294151573817700/5249073936"
        }

    val APP_DETAILS_NATIVE_AD_UNIT_ID: String
        get() = nativeAdUnitId(
            "ca-app-pub-5294151573817700/8031005318"
        )

    val NATIVE_AD_UNIT_ID: String
        get() = nativeAdUnitId(
            "ca-app-pub-5294151573817700/6186111619"
        )

    val NO_DATA_NATIVE_AD_UNIT_ID: String
        get() = nativeAdUnitId(
            "ca-app-pub-5294151573817700/7759542575"
        )

    val HELP_NATIVE_AD_UNIT_ID: String
        get() = nativeAdUnitId(
            "ca-app-pub-5294151573817700/1975403869"
        )

    val SUPPORT_NATIVE_AD_UNIT_ID: String
        get() = nativeAdUnitId(
            "ca-app-pub-5294151573817700/4419063361"
        )

    private fun nativeAdUnitId(releaseId: String): String =
        if (BuildConfig.DEBUG) {
            DebugAdsConstants.NATIVE_AD_UNIT_ID
        } else {
            releaseId
        }
}
