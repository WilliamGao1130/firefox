/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.downloads.listscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import mozilla.components.lib.state.helpers.StoreProvider.Companion.fragmentStore
import mozilla.components.lib.state.helpers.StoreProvider.Companion.storeProvider
import mozilla.components.support.utils.DefaultDownloadFileUtils
import org.bluepowerrobotics.bpfox.components.appstate.AppAction
import org.bluepowerrobotics.bpfox.components.appstate.SupportedMenuNotifications
import org.bluepowerrobotics.bpfox.compose.snackbar.Snackbar
import org.bluepowerrobotics.bpfox.compose.snackbar.SnackbarState
import org.bluepowerrobotics.bpfox.downloads.getCannotOpenFileErrorMessage
import org.bluepowerrobotics.bpfox.downloads.listscreen.di.DownloadUIMiddlewareProvider
import org.bluepowerrobotics.bpfox.downloads.listscreen.store.DownloadUIState
import org.bluepowerrobotics.bpfox.downloads.listscreen.store.DownloadUIStore
import org.bluepowerrobotics.bpfox.downloads.listscreen.store.FileItem
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.ext.hideToolbar
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.settings.downloads.DownloadLocationManager
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme

/** Fragment for displaying and managing the downloads list. */
class DownloadFragment : Fragment(), SystemInsetsPaddedFragment {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext()
            .applicationContext
            .components
            .appStore
            .dispatch(AppAction.MenuNotification.RemoveMenuNotification(SupportedMenuNotifications.Downloads))
    }

    private val downloadStore by
        fragmentStore(DownloadUIState.INITIAL) {
            DownloadUIStore(
                initialState = it,
                middleware =
                    DownloadUIMiddlewareProvider.provideMiddleware(
                        coroutineScope = storeProvider.viewModelScope,
                        applicationContext = requireContext().applicationContext,
                        navController = findNavController(),
                    ),
            )
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = content {
        FirefoxTheme {
            DownloadsScreen(
                downloadsStore = downloadStore,
                onItemClick = { openItem(it) },
            )
        }
    }

    private fun openItem(item: FileItem) {
        context?.let {
            val fileUtils =
                DefaultDownloadFileUtils(
                    context = requireContext(),
                    downloadLocation = {
                        DownloadLocationManager(
                                requireComponents.settings,
                                requireContext().contentResolver,
                            )
                            .defaultLocation
                    },
                )
            val canOpenFile =
                fileUtils.openFile(
                    fileName = item.fileName,
                    directoryPath = item.directoryPath,
                    contentType = item.contentType,
                )

            val rootView = view
            if (!canOpenFile && rootView != null) {
                Snackbar.make(
                        snackBarParentView = rootView,
                        snackbarState =
                            SnackbarState(
                                message =
                                    getCannotOpenFileErrorMessage(
                                        context = it,
                                        filePath = item.filePath,
                                    )
                            ),
                    )
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
    }
}
