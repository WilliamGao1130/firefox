/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.components.appstate.reducer

import org.bluepowerrobotics.bpfox.components.appstate.AppAction.FindInPageAction
import org.bluepowerrobotics.bpfox.components.appstate.AppState

/** A [FindInPageAction] reducer that updates [AppState.showFindInPage]. */
internal object FindInPageStateReducer {
    fun reduce(state: AppState, action: FindInPageAction): AppState =
        when (action) {
            FindInPageAction.FindInPageDismissed,
            FindInPageAction.FindInPageShown -> state.copy(showFindInPage = false)

            FindInPageAction.FindInPageStarted -> state.copy(showFindInPage = true)
        }
}
