/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.webcompat.di

import kotlinx.coroutines.CoroutineScope
import mozilla.components.browser.state.state.BrowserState
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.service.nimbus.NimbusApi
import org.bluepowerrobotics.bpfox.components.AppStore
import org.bluepowerrobotics.bpfox.components.appstate.webcompat.WebCompatState
import org.bluepowerrobotics.bpfox.webcompat.DefaultGleanBrokenSiteReportSender
import org.bluepowerrobotics.bpfox.webcompat.middleware.DefaultNimbusExperimentsProvider
import org.bluepowerrobotics.bpfox.webcompat.middleware.DefaultWebCompatReporterRetrievalService
import org.bluepowerrobotics.bpfox.webcompat.middleware.WebCompatReporterNavigationMiddleware
import org.bluepowerrobotics.bpfox.webcompat.middleware.WebCompatReporterStorageMiddleware
import org.bluepowerrobotics.bpfox.webcompat.middleware.WebCompatReporterSubmissionMiddleware
import org.bluepowerrobotics.bpfox.webcompat.middleware.WebCompatReporterTelemetryMiddleware

/** Provides middleware for the WebCompat Reporter store. */
object WebCompatReporterMiddlewareProvider {

    /**
     * Provides middleware for the WebCompat Reporter.
     *
     * @param browserStore [BrowserStore] used to access [BrowserState].
     * @param appStore [AppStore] used to persist [WebCompatState].
     * @param scope The [CoroutineScope] used for launching coroutines.
     * @param nimbusApi A [NimbusApi] with which to get active/enrolled experiments.
     */
    fun provideMiddleware(
        browserStore: BrowserStore,
        appStore: AppStore,
        scope: CoroutineScope,
        nimbusApi: NimbusApi,
    ) =
        listOf(
            provideStorageMiddleware(appStore),
            provideSubmissionMiddleware(
                appStore = appStore,
                browserStore = browserStore,
                scope = scope,
                nimbusApi = nimbusApi,
            ),
            provideNavigationMiddleware(),
            provideTelemetryMiddleware(),
        )

    private fun provideStorageMiddleware(appStore: AppStore) = WebCompatReporterStorageMiddleware(appStore = appStore)

    private fun provideSubmissionMiddleware(
        appStore: AppStore,
        browserStore: BrowserStore,
        scope: CoroutineScope,
        nimbusApi: NimbusApi,
    ): WebCompatReporterSubmissionMiddleware {
        val webCompatReporterRetrievalService = DefaultWebCompatReporterRetrievalService(browserStore = browserStore)

        val gleanBrokenSiteReportSender = DefaultGleanBrokenSiteReportSender(browserStore = browserStore)

        return WebCompatReporterSubmissionMiddleware(
            appStore = appStore,
            webCompatReporterRetrievalService = webCompatReporterRetrievalService,
            gleanBrokenSiteReportSender = gleanBrokenSiteReportSender,
            scope = scope,
            nimbusExperimentsProvider = DefaultNimbusExperimentsProvider(nimbusApi),
        )
    }

    private fun provideNavigationMiddleware() = WebCompatReporterNavigationMiddleware()

    private fun provideTelemetryMiddleware() = WebCompatReporterTelemetryMiddleware()
}
