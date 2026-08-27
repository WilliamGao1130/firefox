/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.components.appstate.webcompat

import org.bluepowerrobotics.bpfox.components.appstate.AppAction.WebCompatAction
import org.bluepowerrobotics.bpfox.components.appstate.AppState
import org.bluepowerrobotics.bpfox.components.appstate.snackbar.SnackbarState

/** Helper object which reduces [WebCompatAction]. */
internal object WebCompatReducer {

    /**
     * Reduces [WebCompatAction]s and performs any necessary state mutations.
     *
     * @param state The current snapshot of [AppState].
     * @param action The [WebCompatAction] being reduced.
     * @return The resulting [AppState] after [action] has been reduced.
     */
    fun reduce(state: AppState, action: WebCompatAction): AppState =
        when (action) {
            is WebCompatAction.WebCompatStateUpdated -> state.copy(webCompatState = action.newState)

            WebCompatAction.WebCompatStateReset -> state.copy(webCompatState = null)

            WebCompatAction.WebCompatReportSent ->
                state.copy(
                    snackbarState = SnackbarState.WebCompatReportSent,
                    webCompatState = null,
                )
        }
}
