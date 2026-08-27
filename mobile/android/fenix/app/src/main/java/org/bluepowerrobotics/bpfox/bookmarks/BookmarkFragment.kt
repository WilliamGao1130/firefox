/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.bookmarks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.NavHostController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.MutableSharedFlow
import mozilla.components.compose.browser.toolbar.store.BrowserToolbarState
import mozilla.components.compose.browser.toolbar.store.BrowserToolbarStore
import mozilla.components.compose.browser.toolbar.store.Mode
import mozilla.components.lib.state.helpers.StoreProvider.Companion.fragmentStore
import mozilla.components.support.base.feature.ViewBoundFeatureWrapper
import org.bluepowerrobotics.bpfox.HomeActivity
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.bookmarks.importer.FenixImporterEvent
import org.bluepowerrobotics.bpfox.components.LensFeature
import org.bluepowerrobotics.bpfox.components.QrScanFenixFeature
import org.bluepowerrobotics.bpfox.components.VoiceSearchFeature
import org.bluepowerrobotics.bpfox.components.accounts.FenixFxAEntryPoint
import org.bluepowerrobotics.bpfox.components.appstate.AppAction
import org.bluepowerrobotics.bpfox.components.share.ShareSheetChooserAction
import org.bluepowerrobotics.bpfox.components.share.ShareSource
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.bookmarkStorage
import org.bluepowerrobotics.bpfox.ext.hideToolbar
import org.bluepowerrobotics.bpfox.ext.nav
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.pbmlock.registerForVerification
import org.bluepowerrobotics.bpfox.pbmlock.verifyUser
import org.bluepowerrobotics.bpfox.search.BrowserToolbarSearchMiddleware
import org.bluepowerrobotics.bpfox.search.BrowserToolbarSearchStatusSyncMiddleware
import org.bluepowerrobotics.bpfox.tabstray.redux.state.Page
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme

/** The screen that displays the user's bookmark list in their Library. */
@Suppress("TooManyFunctions", "LargeClass")
class BookmarkFragment : Fragment(), SystemInsetsPaddedFragment {

    private val verificationResultLauncher = registerForVerification()
    private var qrScanFenixFeature: ViewBoundFeatureWrapper<QrScanFenixFeature>? =
        ViewBoundFeatureWrapper<QrScanFenixFeature>()
    private val qrScanLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            qrScanFenixFeature?.get()?.handleToolbarQrScanResults(result.resultCode, result.data)
        }
    private var voiceSearchFeature: ViewBoundFeatureWrapper<VoiceSearchFeature>? =
        ViewBoundFeatureWrapper<VoiceSearchFeature>()
    private val voiceSearchLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            voiceSearchFeature?.get()?.handleVoiceSearchResult(result.resultCode, result.data)
        }
    private var lensFeature: ViewBoundFeatureWrapper<LensFeature>? = ViewBoundFeatureWrapper<LensFeature>()
    private val lensLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            lensFeature
                ?.get()
                ?.handleCameraActivityResult(
                    result.resultCode,
                    result.data,
                    qrScanFenixFeature?.get(),
                )
        }
    private val lensCameraPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            lensFeature?.get()?.onCameraPermissionResult(isGranted)
        }

    private val importResultFlow = MutableSharedFlow<FenixImporterEvent>(extraBufferCapacity = 1)

    @Suppress("LongMethod")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val toolbarStore = buildToolbarStore()
        val buildStore = { composeNavController: NavHostController ->
            val appStore = requireComponents.appStore
            val navController = this@BookmarkFragment.findNavController()

            val store by
                fragmentStore(
                    BookmarksState.default.copy(
                        showBookmarksImport = requireComponents.settings.importBookmarksFeatureFlagEnabled,
                        sortOrder =
                            BookmarksListSortOrder.fromString(
                                value = requireComponents.settings.bookmarkListSortOrder,
                                default = BookmarksListSortOrder.Alphabetical(true),
                            ),
                    )
                ) {
                    BookmarksStore(
                        initialState = it,
                        middleware =
                            listOf(
                                // NB: Order matters — this middleware must be first to intercept actions
                                // related to private mode and trigger verification before any other middleware runs.
                                PrivateBrowsingLockMiddleware(
                                    appStore = requireComponents.appStore,
                                    requireAuth = {
                                        verifyUser(fallbackVerification = verificationResultLauncher)
                                    },
                                ),
                                BookmarksTelemetryMiddleware(),
                                BookmarksSyncMiddleware(
                                    requireComponents.backgroundServices.syncStore,
                                    lifecycleScope,
                                ),
                                BrowserToolbarSyncToBookmarksMiddleware(toolbarStore, lifecycleScope),
                                BookmarksMiddleware(
                                    lifecycleScope = lifecycleScope,
                                    bookmarksStorage = requireContext().bookmarkStorage,
                                    addNewTabUseCase = requireComponents.useCases.tabsUseCases.addTab,
                                    fenixBrowserUseCases = requireComponents.useCases.fenixBrowserUseCases,
                                    openBookmarksInNewTab =
                                        if (requireComponents.settings.enableHomepageAsNewTab) {
                                            false
                                        } else {
                                            navController.previousBackStackEntry?.destination?.id == R.id.homeFragment
                                        },
                                    getNavController = { composeNavController },
                                    exitBookmarks = { navController.popBackStack() },
                                    navigateToBrowser = {
                                        navController.navigate(R.id.browserFragment)
                                    },
                                    navigateToSignIntoSync = {
                                        navController.navigate(
                                            BookmarkFragmentDirections.actionGlobalTurnOnSync(
                                                entrypoint = FenixFxAEntryPoint.BookmarkView
                                            )
                                        )
                                    },
                                    navigateToImportDialog = {
                                        ImportBookmarksDialogFragment()
                                            .show(
                                                childFragmentManager,
                                                ImportBookmarksDialogFragment.TAG,
                                            )
                                    },
                                    shareBookmarks = { bookmarks ->
                                        val shareItems =
                                            bookmarks.asShareDataArray(appStore.state.mode.isPrivate).toList()
                                        requireComponents.useCases.shareUseCases.shareItems(
                                            items = shareItems,
                                            source = ShareSource.BOOKMARKS,
                                            chooserActions =
                                                if (shareItems.size == 1) {
                                                    listOf(
                                                        ShareSheetChooserAction.SEND_TO_DEVICES,
                                                        ShareSheetChooserAction.QR_CODE,
                                                    )
                                                } else {
                                                    listOf(ShareSheetChooserAction.SEND_TO_DEVICES)
                                                },
                                            navigateToShareFragment = {
                                                navController.nav(
                                                    R.id.bookmarkFragment,
                                                    BookmarkFragmentDirections.actionGlobalShareFragment(
                                                        data = bookmarks.asShareDataArray(appStore.state.mode.isPrivate)
                                                    ),
                                                )
                                            },
                                        )
                                    },
                                    showTabsTray = ::showTabTray,
                                    resolveFolderTitle = {
                                        friendlyRootTitle(
                                            context = requireContext(),
                                            node = it,
                                            rootTitles = composeRootTitles(requireContext()),
                                        ) ?: ""
                                    },
                                    getBrowsingMode = {
                                        appStore.state.mode
                                    },
                                    saveBookmarkSortOrder = {
                                        requireComponents.settings.bookmarkListSortOrder = it.asString
                                    },
                                    editBookmarkUseCase = requireComponents.useCases.bookmarksUseCases.editBookmark,
                                    reportResultGlobally = {
                                        requireComponents.appStore.dispatch(
                                            AppAction.BookmarkAction.BookmarkOperationResultReported(it)
                                        )
                                    },
                                    importEvents = { importResultFlow },
                                ),
                            ),
                    )
                }

            store
        }
        return content {
            FirefoxTheme {
                BookmarksScreen(
                    buildStore = buildStore,
                    appStore = requireComponents.appStore,
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qrScanFenixFeature = QrScanFenixFeature.register(this, qrScanLauncher)
        voiceSearchFeature = VoiceSearchFeature.register(this, voiceSearchLauncher)
        lensFeature = LensFeature.register(this, lensLauncher, lensCameraPermissionLauncher)

        childFragmentManager.setFragmentResultListener(
            ImportBookmarksDialogFragment.REQUEST_KEY,
            viewLifecycleOwner,
        ) { _, bundle ->
            ImportBookmarksDialogFragment.decodeResult(bundle)?.let {
                importResultFlow.tryEmit(it)
            }
        }
    }

    private fun buildToolbarStore() =
        fragmentStore(BrowserToolbarState(mode = Mode.EDIT)) {
                val lifecycleScope = viewLifecycleOwner.lifecycle.coroutineScope

                BrowserToolbarStore(
                    initialState = it,
                    middleware =
                        listOf(
                            BrowserToolbarSearchStatusSyncMiddleware(
                                appStore = requireComponents.appStore,
                                browsingModeManager = (requireActivity() as HomeActivity).browsingModeManager,
                                scope = lifecycleScope,
                            ),
                            BrowserToolbarSearchMiddleware(
                                uiContext = requireActivity(),
                                appStore = requireComponents.appStore,
                                browserStore = requireComponents.core.store,
                                components = requireComponents,
                                navController = findNavController(),
                                browsingModeManager = (requireActivity() as HomeActivity).browsingModeManager,
                                settings = requireComponents.settings,
                                scope = lifecycleScope,
                            ),
                        ),
                )
            }
            .value

    override fun onResume() {
        super.onResume()
        hideToolbar()
    }

    private fun showTabTray(openInPrivate: Boolean = false) {
        val directions =
            BookmarkFragmentDirections.actionGlobalTabManagementFragment(
                page =
                    if (openInPrivate) {
                        Page.PrivateTabs
                    } else {
                        Page.NormalTabs
                    }
            )
        navigateToBookmarkFragment(directions = directions)
    }

    private fun navigateToBookmarkFragment(directions: NavDirections) {
        findNavController()
            .nav(
                R.id.bookmarkFragment,
                directions,
            )
    }
}
