/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.browser

import android.view.View
import android.view.ViewGroup
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.bluepowerrobotics.bpfox.components.AppStore
import org.bluepowerrobotics.bpfox.components.appstate.AppAction
import org.bluepowerrobotics.bpfox.compose.snackbar.Snackbar
import org.bluepowerrobotics.bpfox.compose.snackbar.SnackbarFactory

class StandardSnackbarErrorBindingTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var snackbarContainer: ViewGroup
    private lateinit var snackbar: Snackbar
    private lateinit var snackbarFactory: SnackbarFactory
    private lateinit var rootView: View

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        snackbarContainer = mockk()
        snackbar = mockk(relaxed = true)
        snackbarFactory = mockk()
        every {
            snackbarFactory.make(
                snackBarParentView = any(),
                snackbarState = any(),
            )
        } returns snackbar

        rootView = mockk<ViewGroup>(relaxed = true)
    }

    @Test
    fun `WHEN show standard snackbar error action dispatched THEN snackbar should appear`() =
        runTest(testDispatcher) {
            val appStore = AppStore()
            val standardSnackbarError =
                StandardSnackbarErrorBinding(
                    snackbarContainer,
                    appStore,
                    snackbarFactory,
                    "Dismiss",
                    testDispatcher,
                )

            standardSnackbarError.start()
            appStore.dispatch(
                AppAction.UpdateStandardSnackbarErrorAction(StandardSnackbarError("Unable to generate PDF"))
            )
            testDispatcher.scheduler.advanceUntilIdle()

            verify { snackbar.show() }
        }

    @Test
    fun `WHEN show standard snackbar error action dispatched and binding is stopped THEN snackbar should appear when binding is again started`() =
        runTest(testDispatcher) {
            val appStore = AppStore()
            val standardSnackbarError =
                StandardSnackbarErrorBinding(
                    snackbarContainer,
                    appStore,
                    snackbarFactory,
                    "Dismiss",
                    testDispatcher,
                )

            standardSnackbarError.start()
            appStore.dispatch(
                AppAction.UpdateStandardSnackbarErrorAction(StandardSnackbarError("Unable to generate PDF"))
            )
            testDispatcher.scheduler.advanceUntilIdle()

            standardSnackbarError.stop()

            standardSnackbarError.start()

            verify { snackbar.show() }
        }
}
