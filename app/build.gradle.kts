import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(notation = libs.plugins.android.application)
    alias(notation = libs.plugins.compose.compiler)
    alias(notation = libs.plugins.about.libraries)
    alias(notation = libs.plugins.mannodermaus)
    alias(notation = libs.plugins.googlePlayServices)
    alias(notation = libs.plugins.googleFirebase)
    alias(notation = libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.d4rk.lowbrightness"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.d4rk.lowbrightness"
        minSdk = 26
        targetSdk = 36
        versionCode = 54
        versionName = "5.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        @Suppress("UnstableApiUsage")
        androidResources.localeFilters += listOf(
            "ar-rEG",
            "bg-rBG",
            "bn-rBD",
            "de-rDE",
            "en",
            "es-rGQ",
            "es-rMX",
            "fil-rPH",
            "fr-rFR",
            "hi-rIN",
            "hu-rHU",
            "in-rID",
            "it-rIT",
            "ja-rJP",
            "ko-rKR",
            "pl-rPL",
            "pt-rBR",
            "ro-rRO",
            "ru-rRU",
            "sv-rSE",
            "th-rTH",
            "tr-rTR",
            "uk-rUA",
            "ur-rPK",
            "vi-rVN",
            "zh-rTW"
        )
        vectorDrawables {
            useSupportLibrary = true
        }

        val githubProps = Properties()
        val githubFile = rootProject.file("github.properties")
        val githubToken = if (githubFile.exists()) {
            githubProps.load(githubFile.inputStream())
            githubProps["GITHUB_TOKEN"].toString()
        } else {
            ""
        }
        buildConfigField("String", "GITHUB_TOKEN", "\"$githubToken\"")
    }

    signingConfigs {
        create("release")

        val signingProps = Properties()
        val signingFile = rootProject.file("signing.properties")

        if (signingFile.exists()) {
            signingProps.load(signingFile.inputStream())

            signingConfigs.getByName("release").apply {
                storeFile = file(signingProps["STORE_FILE"].toString())
                storePassword = signingProps["STORE_PASSWORD"].toString()
                keyAlias = signingProps["KEY_ALIAS"].toString()
                keyPassword = signingProps["KEY_PASSWORD"].toString()
            }
        } else {
            android.buildTypes.getByName("release").signingConfig = null
        }
    }

    buildTypes {
        release {
            val signingFile = rootProject.file("signing.properties")
            signingConfig = if (signingFile.exists()) {
                signingConfigs.getByName("release")
            } else {
                null
            }
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    buildTypes.forEach { buildType ->
        with(receiver = buildType) {
            multiDexEnabled = true
            proguardFiles(
                getDefaultProguardFile(name = "proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    packaging {
        resources {
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/io.netty.versions.properties")
        }
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
            it.jvmArgs("-XX:+EnableDynamicAgentLoading")
        }
    }
}

dependencies {

    // App Core
    implementation(dependencyNotation = "com.github.MihaiCristianCondrea:App-Toolkit-for-Android:2.0.2") {
        isTransitive = true
    }

    implementation(dependencyNotation = libs.preference.ktx)
    implementation(dependencyNotation = libs.materialdatetimepicker)
    implementation(dependencyNotation = libs.compose.material3.window.size)
    implementation(dependencyNotation = libs.accompanist.navigation.animation)
    implementation(dependencyNotation = libs.profileinstaller)
    implementation(dependencyNotation = libs.compose.color.picker)
    implementation(dependencyNotation = libs.compose.color.picker.android)

    // Unit Tests
    testImplementation(dependencyNotation = libs.bundles.unitTest)
    testRuntimeOnly(dependencyNotation = libs.bundles.unitTestRuntime)

    // Instrumentation Tests
    androidTestImplementation(dependencyNotation = libs.bundles.instrumentationTest)
    debugImplementation(dependencyNotation = libs.androidx.ui.test.manifest)
}
