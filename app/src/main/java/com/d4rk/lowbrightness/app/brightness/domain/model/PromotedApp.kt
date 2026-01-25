package com.d4rk.lowbrightness.app.brightness.domain.model

data class PromotedApp(
    val name: String,
    val packageName: String,
    val iconLogo: String,
    val category: String = "",
)
