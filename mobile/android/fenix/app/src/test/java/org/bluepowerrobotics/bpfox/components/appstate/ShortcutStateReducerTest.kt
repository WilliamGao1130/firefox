/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.components.appstate

import org.junit.Assert.assertEquals
import org.junit.Test
import org.bluepowerrobotics.bpfox.components.appstate.AppAction.ShortcutAction
import org.bluepowerrobotics.bpfox.components.appstate.snackbar.SnackbarState
import org.bluepowerrobotics.bpfox.home.topsites.AddShortcutEntryPoint
import org.bluepowerrobotics.bpfox.home.topsites.AddShortcutSource

class ShortcutStateReducerTest {

    @Test
    fun `WHEN shortcut added action is dispatched THEN state is updated`() {
        val initialState = AppState()
        assertEquals(SnackbarState.None(), initialState.snackbarState)

        val finalState =
            AppStoreReducer.reduce(
                initialState,
                ShortcutAction.ShortcutAdded(
                    source = AddShortcutSource.MANUAL,
                    entryPoint = AddShortcutEntryPoint.PAGE_MENU,
                ),
            )

        assertEquals(SnackbarState.ShortcutAdded, finalState.snackbarState)
    }
}
