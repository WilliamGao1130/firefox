/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.settings.wallpaper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import mozilla.components.lib.state.ext.observeAsComposableState
import mozilla.telemetry.glean.private.NoExtras
import org.bluepowerrobotics.bpfox.GleanMetrics.Wallpapers
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.compose.core.Action
import org.bluepowerrobotics.bpfox.compose.snackbar.Snackbar
import org.bluepowerrobotics.bpfox.compose.snackbar.SnackbarState
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.ext.openToBrowser
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.ext.showToolbar
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme
import org.bluepowerrobotics.bpfox.wallpapers.Wallpaper

/** Settings screen allowing users to choose what wallpaper the application should use. */
class WallpaperSettingsFragment : Fragment(), SystemInsetsPaddedFragment {
    private val appStore by lazy {
        requireComponents.appStore
    }

    private val wallpaperUseCases by lazy {
        requireComponents.useCases.wallpaperUseCases
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Wallpapers.wallpaperSettingsOpened.record(NoExtras())
        val wallpaperSettings =
            ComposeView(requireContext()).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    FirefoxTheme {
                        val wallpapers =
                            appStore
                                .observeAsComposableState { state ->
                                    state.wallpaperState.availableWallpapers
                                }
                                .value
                        val currentWallpaper =
                            appStore
                                .observeAsComposableState { state ->
                                    state.wallpaperState.currentWallpaper
                                }
                                .value

                        val coroutineScope = rememberCoroutineScope()

                        WallpaperSettings(
                            wallpaperGroups = wallpapers.groupByDisplayableCollection(),
                            selectedWallpaper = currentWallpaper,
                            loadWallpaperResource = { wallpaper, size ->
                                wallpaperUseCases.loadThumbnail(wallpaper, size)
                            },
                            onSelectWallpaper = {
                                if (it.name != currentWallpaper.name) {
                                    coroutineScope.launch {
                                        val result = wallpaperUseCases.selectWallpaper(it)
                                        onWallpaperSelected(it, result, requireView())
                                    }
                                }
                            },
                            onLearnMoreClick = { url, collectionName ->
                                findNavController().openToBrowser()
                                requireComponents.useCases.fenixBrowserUseCases.loadUrlOrSearch(
                                    searchTermOrURL = url,
                                    newTab = true,
                                )
                                Wallpapers.learnMoreLinkClick.record(
                                    Wallpapers.LearnMoreLinkClickExtra(
                                        url = url,
                                        collectionName = collectionName,
                                    )
                                )
                            },
                        )
                    }
                }
            }

        // Using CoordinatorLayout as a parent view for the fragment gives the benefit of hiding
        // snackbars automatically when the fragment is closed.
        return CoordinatorLayout(requireContext()).apply {
            addView(wallpaperSettings)
        }
    }

    private fun onWallpaperSelected(
        wallpaper: Wallpaper,
        result: Wallpaper.ImageFileState,
        view: View,
    ) {
        when (result) {
            Wallpaper.ImageFileState.Downloaded -> {
                Wallpapers.wallpaperSelected.record(
                    Wallpapers.WallpaperSelectedExtra(
                        name = wallpaper.name,
                        source = "settings",
                        themeCollection = wallpaper.collection.name,
                    )
                )
            }
            Wallpaper.ImageFileState.Error -> {
                Snackbar.make(
                        snackBarParentView = view,
                        snackbarState =
                            SnackbarState(
                                message = getString(R.string.wallpaper_download_error_snackbar_message),
                                action =
                                    Action(
                                        label = getString(R.string.wallpaper_download_error_snackbar_action),
                                        onClick = {
                                            viewLifecycleOwner.lifecycleScope.launch {
                                                val retryResult = wallpaperUseCases.selectWallpaper(wallpaper)
                                                onWallpaperSelected(wallpaper, retryResult, view)
                                            }
                                        },
                                    ),
                            ),
                    )
                    .show()
            }
            else -> {
                /* noop */
            }
        }

        view.context.components.settings.showWallpaperOnboarding = false
    }

    override fun onResume() {
        super.onResume()
        showToolbar(getString(R.string.customize_wallpapers))
    }
}
