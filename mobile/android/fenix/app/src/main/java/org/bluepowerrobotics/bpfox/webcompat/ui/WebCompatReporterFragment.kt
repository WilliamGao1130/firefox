/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.webcompat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import mozilla.components.lib.state.helpers.StoreProvider.Companion.storeProvider
import mozilla.components.support.ktx.android.view.hideKeyboard
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.openToBrowser
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme
import org.bluepowerrobotics.bpfox.webcompat.GOOGLE_SAFE_BROWSING_REPORT_URL
import org.bluepowerrobotics.bpfox.webcompat.WEB_COMPAT_REPORTER_SUMO_URL
import org.bluepowerrobotics.bpfox.webcompat.di.WebCompatReporterMiddlewareProvider
import org.bluepowerrobotics.bpfox.webcompat.store.WebCompatReporterAction
import org.bluepowerrobotics.bpfox.webcompat.store.WebCompatReporterState
import org.bluepowerrobotics.bpfox.webcompat.store.WebCompatReporterStore

/** [Fragment] for displaying the WebCompat Reporter. */
class WebCompatReporterFragment : Fragment(), SystemInsetsPaddedFragment {

    private val args by navArgs<WebCompatReporterFragmentArgs>()

    private lateinit var webCompatReporterStore: WebCompatReporterStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        webCompatReporterStore = storeProvider.get { restoredState ->
            WebCompatReporterStore(
                initialState =
                    restoredState
                        ?: WebCompatReporterState(
                            tabUrl = args.tabUrl,
                            enteredUrl = args.tabUrl,
                        ),
                middleware =
                    WebCompatReporterMiddlewareProvider.provideMiddleware(
                        browserStore = requireComponents.core.store,
                        appStore = requireComponents.appStore,
                        scope = storeProvider.viewModelScope,
                        nimbusApi = requireComponents.nimbus.sdk,
                    ),
            )
        }

        return content {
            FirefoxTheme {
                WebCompatReporter(store = webCompatReporterStore)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigationEvents()
    }

    private fun observeNavigationEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                webCompatReporterStore.navEvents.collect { navEvent ->
                    when (navEvent) {
                        is WebCompatReporterAction.LearnMoreClicked -> {
                            findNavController().openToBrowser()
                            requireComponents.useCases.fenixBrowserUseCases.loadUrlOrSearch(
                                searchTermOrURL = WEB_COMPAT_REPORTER_SUMO_URL,
                                newTab = true,
                            )
                        }
                        is WebCompatReporterAction.DeceptiveSiteReportSelected -> {
                            requireView().hideKeyboard()
                            findNavController().openToBrowser()
                            requireComponents.useCases.fenixBrowserUseCases.loadUrlOrSearch(
                                searchTermOrURL =
                                    "$GOOGLE_SAFE_BROWSING_REPORT_URL${webCompatReporterStore.state.enteredUrl}",
                                newTab = true,
                            )
                        }
                        is WebCompatReporterAction.ReportSubmitted -> {
                            requireView().hideKeyboard()
                            val directions = WebCompatReporterFragmentDirections.actionGlobalBrowser()
                            findNavController().navigate(directions)
                        }
                        is WebCompatReporterAction.CancelClicked -> {
                            val directions = WebCompatReporterFragmentDirections.actionGlobalBrowser()
                            findNavController().navigate(directions)
                        }
                    }
                }
            }
        }
    }
}
