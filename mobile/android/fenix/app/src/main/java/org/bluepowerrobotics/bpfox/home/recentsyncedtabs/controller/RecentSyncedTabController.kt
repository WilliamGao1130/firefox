/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.home.recentsyncedtabs.controller

import androidx.navigation.NavController
import mozilla.components.feature.tabs.TabsUseCases
import org.bluepowerrobotics.bpfox.GleanMetrics.RecentSyncedTabs
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.components.AppStore
import org.bluepowerrobotics.bpfox.components.appstate.AppAction
import org.bluepowerrobotics.bpfox.components.usecases.FenixBrowserUseCases
import org.bluepowerrobotics.bpfox.home.HomeFragment
import org.bluepowerrobotics.bpfox.home.HomeFragmentDirections
import org.bluepowerrobotics.bpfox.home.recentsyncedtabs.RecentSyncedTab
import org.bluepowerrobotics.bpfox.home.recentsyncedtabs.interactor.RecentSyncedTabInteractor
import org.bluepowerrobotics.bpfox.tabstray.redux.state.Page
import org.bluepowerrobotics.bpfox.tabstray.ui.AccessPoint
import org.bluepowerrobotics.bpfox.utils.Settings

/** An interface that handles the view manipulation of the recent synced tabs in the Home screen. */
interface RecentSyncedTabController {
    /** @see [RecentSyncedTabInteractor.onRecentSyncedTabClicked] */
    fun handleRecentSyncedTabClick(tab: RecentSyncedTab)

    /** @see [RecentSyncedTabInteractor.onRecentSyncedTabClicked] */
    fun handleSyncedTabShowAllClicked()

    /**
     * Handle removing the synced tab from the homescreen.
     *
     * @param tab The recent synced tab to be removed.
     */
    fun handleRecentSyncedTabRemoved(tab: RecentSyncedTab)
}

/**
 * The default implementation of [RecentSyncedTabController].
 *
 * @param fenixBrowserUseCases [FenixBrowserUseCases] used to open the synced tab when clicked.
 * @param tabsUseCase Use cases to open the synced tab when clicked.
 * @param navController [NavController] to navigate to synced tabs tray.
 * @param accessPoint The action or screen that was used to navigate to the tabs tray.
 * @param appStore The [AppStore] that holds the state of the [HomeFragment].
 * @param settings [Settings] used to check the application shared preferences.
 */
class DefaultRecentSyncedTabController(
    private val fenixBrowserUseCases: FenixBrowserUseCases,
    private val tabsUseCase: TabsUseCases,
    private val navController: NavController,
    private val accessPoint: AccessPoint,
    private val appStore: AppStore,
    private val settings: Settings,
) : RecentSyncedTabController {
    override fun handleRecentSyncedTabClick(tab: RecentSyncedTab) {
        RecentSyncedTabs.recentSyncedTabOpened[tab.deviceType.name.lowercase()].add()

        if (settings.enableHomepageAsNewTab) {
            fenixBrowserUseCases.loadUrlOrSearch(
                searchTermOrURL = tab.url,
                newTab = false,
                private = false,
            )
        } else {
            tabsUseCase.selectOrAddTab(tab.url)
        }

        navController.navigate(R.id.browserFragment)
    }

    override fun handleSyncedTabShowAllClicked() {
        RecentSyncedTabs.showAllSyncedTabsClicked.add()
        navController.navigate(
            HomeFragmentDirections.actionGlobalTabManagementFragment(
                page = Page.SyncedTabs,
                accessPoint = accessPoint,
            )
        )
    }

    override fun handleRecentSyncedTabRemoved(tab: RecentSyncedTab) {
        appStore.dispatch(AppAction.RemoveRecentSyncedTab(tab))
    }
}
