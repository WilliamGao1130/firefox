/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.pageObjects

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import org.bluepowerrobotics.bpfox.helpers.HomeActivityIntentTestRule
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.BasePage
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.Selector
import org.bluepowerrobotics.bpfox.ui.efficiency.navigation.NavigationRegistry
import org.bluepowerrobotics.bpfox.ui.efficiency.navigation.NavigationStep
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.HomeSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.MainMenuSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsDeleteBrowsingDataOnQuitSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSelectors

class SettingsDeleteBrowsingDataOnQuitPage(composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>) :
    BasePage(composeRule) {
    override val pageName = "SettingsDeleteBrowsingDataOnQuitPage"

    init {
        NavigationRegistry.register(
            from = "HomePage",
            to = pageName,
            steps =
                listOf(
                    NavigationStep.Click(HomeSelectors.MAIN_MENU_BUTTON),
                    NavigationStep.Click(MainMenuSelectors.SETTINGS_BUTTON),
                    NavigationStep.Swipe(SettingsSelectors.DELETE_BROWSING_DATA_ON_QUIT_BUTTON),
                    NavigationStep.Click(SettingsSelectors.DELETE_BROWSING_DATA_ON_QUIT_BUTTON),
                ),
        )

        NavigationRegistry.register(
            from = pageName,
            to = "HomePage",
            steps = listOf(NavigationStep.PressBackUntilGone(SettingsSelectors.NAVIGATION_TOOLBAR)),
        )
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return SettingsDeleteBrowsingDataOnQuitSelectors.all.filter { it.groups.contains(group) }
    }
}
