package com.d4rk.lowbrightness.app.brightness.data.remote

import com.d4rk.lowbrightness.app.brightness.domain.model.PromotedApp
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PromotedAppsRemoteDataSource {
    fun fetchPromotedApps(url: String): List<PromotedApp> {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 10_000
        }
        return try {
            if (connection.responseCode !in 200..299) {
                emptyList()
            } else {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val appsJson = JSONObject(response)
                    .optJSONObject("data")
                    ?.optJSONArray("apps")
                    ?: return emptyList()

                buildList {
                    for (i in 0 until appsJson.length()) {
                        val item = appsJson.optJSONObject(i) ?: continue
                        val category = item.optJSONObject("category")
                            ?.optString("category_id")
                            ?.ifBlank { null }
                            ?: item.optString("category")
                        val name = item.optString("name")
                        val packageName = item.optString("packageName")
                        val iconLogo = item.optString("iconLogo")
                        if (name.isBlank() || packageName.isBlank() || iconLogo.isBlank()) {
                            continue
                        }
                        add(
                            PromotedApp(
                                name = name,
                                packageName = packageName,
                                iconLogo = iconLogo,
                                category = category
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
            emptyList()
        } finally {
            connection.disconnect()
        }
    }
}
