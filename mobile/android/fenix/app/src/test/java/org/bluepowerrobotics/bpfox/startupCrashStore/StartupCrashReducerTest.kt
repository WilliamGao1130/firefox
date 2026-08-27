/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.startupCrashStore

import org.junit.Assert.assertEquals
import org.junit.Test
import org.bluepowerrobotics.bpfox.startupCrash.CrashReportCompleted
import org.bluepowerrobotics.bpfox.startupCrash.NoTapped
import org.bluepowerrobotics.bpfox.startupCrash.ReopenTapped
import org.bluepowerrobotics.bpfox.startupCrash.ReportTapped
import org.bluepowerrobotics.bpfox.startupCrash.StartupCrashState
import org.bluepowerrobotics.bpfox.startupCrash.UiState
import org.bluepowerrobotics.bpfox.startupCrash.startupCrashReducer

class StartupCrashReducerTest {

    private val defaultState = StartupCrashState(uiState = UiState.Idle)

    @Test
    fun `when No is tapped then uiState is set to Loading`() {
        val before = defaultState.copy(uiState = UiState.Idle)
        val after = startupCrashReducer(before, NoTapped)
        assertEquals(UiState.Finished, after.uiState)
    }

    @Test
    fun `when Report is tapped then uiState is set to Loading`() {
        val before = defaultState.copy(uiState = UiState.Idle)
        val after = startupCrashReducer(before, ReportTapped)
        assertEquals(UiState.Loading, after.uiState)
    }

    @Test
    fun `when the crash report is sent then uiState is set to Finished`() {
        val before = defaultState.copy(uiState = UiState.Loading)
        val after = startupCrashReducer(before, CrashReportCompleted)
        assertEquals(UiState.Finished, after.uiState)
    }

    @Test
    fun `when Reopen is tapped then the state remains unchanged`() {
        val before = defaultState.copy(uiState = UiState.Finished)
        val after = startupCrashReducer(before, ReopenTapped)
        assertEquals(before, after)
    }
}
