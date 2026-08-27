/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.settings.logins.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.fragment.compose.content
import androidx.navigation.NavHostController
import androidx.navigation.fragment.findNavController
import mozilla.components.concept.engine.EngineSession
import mozilla.components.feature.password.importer.PasswordsImporterResult
import mozilla.components.lib.state.helpers.StoreProvider.Companion.fragmentStore
import org.bluepowerrobotics.bpfox.Config
import org.bluepowerrobotics.bpfox.SecureFragment
import org.bluepowerrobotics.bpfox.components.LogMiddleware
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.ext.hideToolbar
import org.bluepowerrobotics.bpfox.ext.openToBrowser
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.settings.logins.ImportPasswordsDialogFragment
import org.bluepowerrobotics.bpfox.settings.logins.ui.DefaultSavedLoginsStorage
import org.bluepowerrobotics.bpfox.settings.logins.ui.LoginsListAppeared
import org.bluepowerrobotics.bpfox.settings.logins.ui.LoginsMiddleware
import org.bluepowerrobotics.bpfox.settings.logins.ui.LoginsSortOrder
import org.bluepowerrobotics.bpfox.settings.logins.ui.LoginsState
import org.bluepowerrobotics.bpfox.settings.logins.ui.LoginsStore
import org.bluepowerrobotics.bpfox.settings.logins.ui.LoginsTelemetryMiddleware
import org.bluepowerrobotics.bpfox.settings.logins.ui.SavedLoginsScreen
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme

/** Defines the fragment containing the saved logins. */
class SavedLoginsFragment : SecureFragment(), SystemInsetsPaddedFragment {

    private var loginsStore: LoginsStore? = null

    override fun onResume() {
        super.onResume()
        hideToolbar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.setFragmentResultListener(
            ImportPasswordsDialogFragment.REQUEST_KEY,
            viewLifecycleOwner,
        ) { _, bundle ->
            if (ImportPasswordsDialogFragment.decodeResult(bundle) is PasswordsImporterResult.Success) {
                loginsStore?.dispatch(LoginsListAppeared)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = content {
        val buildStore = { composeNavController: NavHostController ->
            val navController = findNavController()

            val store by
                fragmentStore(
                    LoginsState.default.copy(
                        showPasswordsImport = requireComponents.settings.importPasswordsFeatureFlagEnabled,
                        sortOrder =
                            LoginsSortOrder.fromString(
                                value = requireComponents.settings.loginsListSortOrder,
                                default = LoginsSortOrder.Alphabetical,
                            ),
                    )
                ) {
                    LoginsStore(
                        initialState = it,
                        middleware =
                            listOf(
                                LogMiddleware(
                                    tag = "LoginsStore",
                                    shouldIncludeDetailedData = { Config.channel.isDebug },
                                ),
                                LoginsTelemetryMiddleware(),
                                LoginsMiddleware(
                                    loginsStorage = requireContext().components.core.passwordsStorage,
                                    getNavController = { composeNavController },
                                    exitLogins = { navController.popBackStack() },
                                    persistLoginsSortOrder = { sortOrder ->
                                        DefaultSavedLoginsStorage(requireComponents.settings).savedLoginsSortOrder =
                                            sortOrder
                                    },
                                    navigateToImportDialog = {
                                        ImportPasswordsDialogFragment()
                                            .show(
                                                childFragmentManager,
                                                ImportPasswordsDialogFragment.TAG,
                                            )
                                    },
                                    openTab = { url, openInNewTab ->
                                        findNavController().openToBrowser()
                                        requireComponents.useCases.fenixBrowserUseCases.loadUrlOrSearch(
                                            searchTermOrURL = url,
                                            newTab = openInNewTab,
                                            flags =
                                                EngineSession.LoadUrlFlags.select(
                                                    EngineSession.LoadUrlFlags.ALLOW_JAVASCRIPT_URL
                                                ),
                                        )
                                    },
                                    clipboardManager = requireContext().getSystemService(),
                                ),
                            ),
                    )
                }
            loginsStore = store
            store
        }

        FirefoxTheme {
            SavedLoginsScreen(
                buildStore = buildStore,
                exitLogins = {
                    findNavController().popBackStack()
                },
            )
        }
    }
}
