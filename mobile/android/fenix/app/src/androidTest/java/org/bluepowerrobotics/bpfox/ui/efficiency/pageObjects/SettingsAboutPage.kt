/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.pageObjects

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import org.bluepowerrobotics.bpfox.helpers.HomeActivityIntentTestRule
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.BasePage
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.Selector
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsAboutSelectors

class SettingsAboutPage(composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>) : BasePage(composeRule) {
    override val pageName = "SettingsAboutPage"

    init {
        //        NavigationRegistry.register(
        //            from = "HomePage",
        //            to = pageName,
        //            steps = listOf(
        //                NavigationStep.Click(HomeSelectors.MAIN_MENU_BUTTON),
        //                NavigationStep.Click(MainMenuSelectors.SETTINGS_BUTTON),
        //                NavigationStep.Swipe(SettingsSelectors.DATA_COLLECTION_BUTTON),
        //                NavigationStep.Click(SettingsSelectors.DATA_COLLECTION_BUTTON),
        //            ),
        //        )
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return SettingsAboutSelectors.all.filter { it.groups.contains(group) }
    }
}
