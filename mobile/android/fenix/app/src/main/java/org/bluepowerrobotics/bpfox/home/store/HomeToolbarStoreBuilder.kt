/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.home.store

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavController
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.compose.browser.toolbar.store.BrowserToolbarState
import mozilla.components.compose.browser.toolbar.store.BrowserToolbarStore
import mozilla.components.lib.state.helpers.StoreProvider.Companion.fragmentStore
import org.bluepowerrobotics.bpfox.browser.browsingmode.BrowsingModeManager
import org.bluepowerrobotics.bpfox.components.AppStore
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.ext.isTallWindow
import org.bluepowerrobotics.bpfox.ext.isWideWindow
import org.bluepowerrobotics.bpfox.home.toolbar.BrowserToolbarMiddleware
import org.bluepowerrobotics.bpfox.home.toolbar.BrowserToolbarTelemetryMiddleware
import org.bluepowerrobotics.bpfox.search.BrowserToolbarSearchMiddleware
import org.bluepowerrobotics.bpfox.search.BrowserToolbarSearchStatusSyncMiddleware
import org.bluepowerrobotics.bpfox.translations.TranslationsEnabledSettings

/** Delegate for building the [BrowserToolbarStore] used in the home screen. */
object HomeToolbarStoreBuilder {
    /**
     * Build the [BrowserToolbarStore] used in the home screen.
     *
     * @param context [Context] used for various system interactions.
     * @param fragment [Fragment] as a [LifecycleOwner] to used to organize lifecycle dependent operations.
     * @param navController [NavController] to use for navigating to other in-app destinations.
     * @param appStore [AppStore] to sync from.
     * @param browserStore [BrowserStore] to sync from.
     * @param browsingModeManager [BrowsingModeManager] for querying the current browsing mode.
     */
    fun build(
        context: Context,
        fragment: Fragment,
        navController: NavController,
        appStore: AppStore,
        browserStore: BrowserStore,
        browsingModeManager: BrowsingModeManager,
    ) =
        fragment.fragmentStore(BrowserToolbarState()) {
            val lifecycleScope = fragment.viewLifecycleOwner.lifecycle.coroutineScope

            BrowserToolbarStore(
                initialState = it,
                middleware =
                    listOf(
                        BrowserToolbarSearchStatusSyncMiddleware(
                            appStore = appStore,
                            browsingModeManager = browsingModeManager,
                            scope = lifecycleScope,
                        ),
                        BrowserToolbarMiddleware(
                            uiContext = context,
                            appStore = appStore,
                            browserStore = browserStore,
                            clipboard = context.components.clipboardHandler,
                            fenixBrowserUseCases = context.components.useCases.fenixBrowserUseCases,
                            navController = navController,
                            browsingModeManager = browsingModeManager,
                            settings = context.components.settings,
                            translationsFeatureSettings = TranslationsEnabledSettings.dataStore(context),
                            isWideScreen = { fragment.isWideWindow() },
                            isTallScreen = { fragment.isTallWindow() },
                            scope = lifecycleScope,
                        ),
                        BrowserToolbarSearchMiddleware(
                            uiContext = context,
                            appStore = appStore,
                            browserStore = browserStore,
                            components = context.components,
                            navController = navController,
                            browsingModeManager = browsingModeManager,
                            settings = context.components.settings,
                            scope = lifecycleScope,
                        ),
                        BrowserToolbarTelemetryMiddleware(),
                    ),
            )
        }
}
