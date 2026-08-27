/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.downloads.listscreen.middleware

import android.content.Context
import mozilla.components.lib.state.Middleware
import mozilla.components.lib.state.Store
import mozilla.components.support.ktx.android.content.share
import mozilla.components.support.ktx.android.content.shareFile
import mozilla.components.support.utils.DefaultDownloadFileUtils
import mozilla.components.support.utils.DownloadFileUtils
import org.bluepowerrobotics.bpfox.downloads.listscreen.store.DownloadUIAction
import org.bluepowerrobotics.bpfox.downloads.listscreen.store.DownloadUIState
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.settings.downloads.DownloadLocationManager

/**
 * Middleware for sharing the Download item's URL.
 *
 * @param applicationContext A [Context] used to share the URL.
 * @param downloadFileUtils [DownloadFileUtils] used for file-related operations.
 */
class DownloadUIShareMiddleware(
    private val applicationContext: Context,
    private val downloadFileUtils: DownloadFileUtils =
        DefaultDownloadFileUtils(
            context = applicationContext,
            downloadLocation = {
                DownloadLocationManager(
                        applicationContext.components.settings,
                        applicationContext.contentResolver,
                    )
                    .defaultLocation
            },
        ),
) : Middleware<DownloadUIState, DownloadUIAction> {

    override fun invoke(
        store: Store<DownloadUIState, DownloadUIAction>,
        next: (DownloadUIAction) -> Unit,
        action: DownloadUIAction,
    ) {
        next(action)
        when (action) {
            is DownloadUIAction.ShareUrlClicked -> applicationContext.share(action.url)
            is DownloadUIAction.ShareFileClicked ->
                shareFile(
                    directoryPath = action.directoryPath,
                    fileName = action.fileName,
                    contentType = action.contentType,
                )

            else -> {
                // no - op
            }
        }
    }

    private fun shareFile(directoryPath: String, fileName: String?, contentType: String?) {
        val downloadFileUri =
            downloadFileUtils.findShareableDownloadFileUri(
                fileName = fileName,
                directoryPath = directoryPath,
            )
        downloadFileUri?.let {
            applicationContext.shareFile(
                contentUri = it,
                contentType = contentType,
            )
        }
    }
}
