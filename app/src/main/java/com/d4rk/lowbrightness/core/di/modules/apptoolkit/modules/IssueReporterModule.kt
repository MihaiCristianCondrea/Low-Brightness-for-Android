package com.d4rk.lowbrightness.core.di.modules.apptoolkit.modules

import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.android.libs.apptoolkit.app.issuereporter.data.local.DeviceInfoLocalDataSource
import com.d4rk.android.libs.apptoolkit.app.issuereporter.data.remote.IssueReporterRemoteDataSource
import com.d4rk.android.libs.apptoolkit.app.issuereporter.data.repository.IssueReporterRepositoryImpl
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.github.GithubTarget
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.providers.DeviceInfoProvider
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.repository.IssueReporterRepository
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.usecases.SendIssueReportUseCase
import com.d4rk.android.libs.apptoolkit.app.issuereporter.ui.IssueReporterViewModel
import com.d4rk.android.libs.apptoolkit.core.di.GithubToken
import com.d4rk.android.libs.apptoolkit.core.utils.constants.github.GithubConstants
import com.d4rk.android.libs.apptoolkit.core.utils.extensions.string.toToken
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

private val githubTokenQualifier = qualifier<GithubToken>()

val issueReporterModule: Module =
    module {
        single { IssueReporterRemoteDataSource(client = get()) }
        single<DeviceInfoProvider> { DeviceInfoLocalDataSource(get(), get()) }
        single<IssueReporterRepository> { IssueReporterRepositoryImpl(get(), get()) }
        single { SendIssueReportUseCase(get(), get()) }

        single(qualifier = named(name = "github_repository")) { "Low-Brightness-for-Android" }
        single<GithubTarget> {
            GithubTarget(
                username = GithubConstants.GITHUB_USER,
                repository = get(qualifier = named("github_repository")),
            )
        }

        single(qualifier = named("github_changelog")) {
            GithubConstants.githubChangelog(get<String>(named("github_repository")))
        }

        single(githubTokenQualifier) { BuildConfig.GITHUB_TOKEN.toToken() }

        viewModel {
            IssueReporterViewModel(
                sendIssueReport = get(),
                githubTarget = get(),
                githubToken = get(githubTokenQualifier),
                deviceInfoProvider = get(),
                firebaseController = get(),
            )
        }
    }
