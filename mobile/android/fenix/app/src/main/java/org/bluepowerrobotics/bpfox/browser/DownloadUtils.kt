/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.browser

import mozilla.components.browser.state.state.content.DownloadState
import mozilla.components.browser.state.state.content.DownloadState.Status
import mozilla.components.concept.toolbar.ScrollableToolbar
import mozilla.components.support.utils.DownloadFileUtils
import org.bluepowerrobotics.bpfox.components.AppStore
import org.bluepowerrobotics.bpfox.components.appstate.AppAction

internal fun BaseBrowserFragment.handleOnDownloadFinished(
    appStore: AppStore,
    downloadState: DownloadState,
    downloadJobStatus: Status,
    browserToolbars: List<ScrollableToolbar>,
    downloadFileUtils: DownloadFileUtils,
) {
    // If the download is just paused, don't show any in-app notification
    if (shouldShowCompletedDownloadDialog(downloadState, downloadJobStatus)) {
        if (downloadState.openInApp && downloadJobStatus == Status.COMPLETED) {
            val fileWasOpened =
                downloadFileUtils.openFile(
                    fileName = downloadState.fileName,
                    directoryPath = downloadState.directoryPath,
                    contentType = downloadState.contentType,
                )
            if (!fileWasOpened) {
                appStore.dispatch(AppAction.DownloadAction.CannotOpenFile(downloadState = downloadState))
            }
        } else {
            if (downloadJobStatus == Status.FAILED) {
                appStore.dispatch(AppAction.DownloadAction.DownloadFailed(downloadState.fileName))
            } else {
                appStore.dispatch(AppAction.DownloadAction.DownloadCompleted(downloadState))
            }

            browserToolbars.forEach { it.expand() }
        }
    }
}
