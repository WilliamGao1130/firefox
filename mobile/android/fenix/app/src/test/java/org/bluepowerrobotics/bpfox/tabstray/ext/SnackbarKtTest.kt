/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.tabstray.ext

import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertIs
import mozilla.components.support.test.robolectric.testContext
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.components.SnackbarBehavior
import org.bluepowerrobotics.bpfox.components.toolbar.ToolbarPosition
import org.bluepowerrobotics.bpfox.compose.snackbar.Snackbar
import org.bluepowerrobotics.bpfox.compose.snackbar.SnackbarState
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.helpers.MockkRetryTestRule
import org.bluepowerrobotics.bpfox.utils.Settings
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SnackbarKtTest {

    @get:Rule val mockkRule = MockkRetryTestRule()

    @Test
    fun `GIVEN the snackbar is a child of dynamic container WHEN it is shown THEN enable the dynamic behavior`() {
        val container =
            FrameLayout(testContext).apply {
                id = R.id.dynamicSnackbarContainer
                layoutParams = CoordinatorLayout.LayoutParams(0, 0)
            }
        val settings: Settings =
            mockk(relaxed = true) {
                every { toolbarPosition } returns ToolbarPosition.BOTTOM
            }

        every { testContext.components.settings } returns settings

        Snackbar.make(
            snackBarParentView = container,
            snackbarState = SnackbarState(message = "test"),
        )

        val behavior = (container.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior
        assertIs<SnackbarBehavior<*>>(behavior)
        assertEquals(ToolbarPosition.BOTTOM, behavior.toolbarPosition)
    }
}
