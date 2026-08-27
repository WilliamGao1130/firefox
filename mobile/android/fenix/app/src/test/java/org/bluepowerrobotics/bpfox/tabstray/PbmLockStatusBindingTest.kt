/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.tabstray

import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import mozilla.components.browser.state.state.createTab
import org.junit.Test
import org.bluepowerrobotics.bpfox.components.AppStore
import org.bluepowerrobotics.bpfox.components.appstate.AppAction
import org.bluepowerrobotics.bpfox.components.appstate.AppState
import org.bluepowerrobotics.bpfox.tabstray.data.TabsTrayItem
import org.bluepowerrobotics.bpfox.tabstray.redux.action.TabsTrayAction
import org.bluepowerrobotics.bpfox.tabstray.redux.state.TabsTrayState
import org.bluepowerrobotics.bpfox.tabstray.redux.store.TabsTrayStore

@OptIn(ExperimentalCoroutinesApi::class)
class PbmLockStatusBindingTest {
    private val testDispatcher = StandardTestDispatcher()
    lateinit var tabsTrayStore: TabsTrayStore
    lateinit var appStore: AppStore

    @Test
    fun `WHEN private browsing lock status updates THEN tabs tray action dispatched with new status`() =
        runTest(testDispatcher) {
            appStore = AppStore(AppState(inactiveTabsExpanded = false))

            tabsTrayStore =
                spyk(
                    TabsTrayStore(
                        TabsTrayState(
                            privateBrowsing =
                                TabsTrayState.PrivateBrowsingState(
                                    tabs = listOf(TabsTrayItem.Tab(tab = createTab("mozilla.org", id = "mozilla"))),
                                    showLockBanner = false,
                                    isLocked = false,
                                )
                        )
                    )
                )

            val binding =
                PbmLockStatusBinding(
                    appStore = appStore,
                    tabsTrayStore = tabsTrayStore,
                    mainDispatcher = testDispatcher,
                )
            binding.start()
            appStore.dispatch(AppAction.PrivateBrowsingLockAction.UpdatePrivateBrowsingLock(isLocked = true))
            testDispatcher.scheduler.advanceUntilIdle()

            verify { tabsTrayStore.dispatch(TabsTrayAction.UpdatePbmLockStatus(true)) }
        }
}
