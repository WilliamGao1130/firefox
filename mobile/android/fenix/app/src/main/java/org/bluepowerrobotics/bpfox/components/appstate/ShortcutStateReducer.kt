/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.components.appstate

import org.bluepowerrobotics.bpfox.components.appstate.AppAction.ShortcutAction
import org.bluepowerrobotics.bpfox.components.appstate.snackbar.SnackbarState

/** A [ShortcutAction] reducer that updates [AppState.snackbarState]. */
internal object ShortcutStateReducer {
    fun reduce(state: AppState, action: ShortcutAction): AppState =
        when (action) {
            is ShortcutAction.ShortcutAdded -> state.copy(snackbarState = SnackbarState.ShortcutAdded)

            is ShortcutAction.AddShortcutSheetShown,
            is ShortcutAction.AddWebsiteDialogShown,
            is ShortcutAction.FrecencyTopSitePromoted -> state
        }
}
