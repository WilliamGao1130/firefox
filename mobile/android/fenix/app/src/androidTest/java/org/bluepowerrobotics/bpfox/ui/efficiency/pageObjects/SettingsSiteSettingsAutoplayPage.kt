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
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSiteSettingsAutoplaySelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSiteSettingsSelectors

class SettingsSiteSettingsAutoplayPage(composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>) :
    BasePage(composeRule) {
    override val pageName = "SettingsSiteSettingsAutoplayPage"

    init {
        NavigationRegistry.register(
            from = "HomePage",
            to = pageName,
            steps =
                listOf(
                    NavigationStep.Click(HomeSelectors.MAIN_MENU_BUTTON),
                    NavigationStep.Click(MainMenuSelectors.SETTINGS_BUTTON),
                    NavigationStep.Swipe(SettingsSelectors.SITE_SETTINGS_BUTTON),
                    NavigationStep.Click(SettingsSelectors.SITE_SETTINGS_BUTTON),
                    NavigationStep.Click(SettingsSiteSettingsSelectors.AUTOPLAY_BUTTON),
                ),
        )

        NavigationRegistry.register(
            from = pageName,
            to = "HomePage",
            steps = listOf(NavigationStep.PressBackUntilGone(SettingsSelectors.NAVIGATION_TOOLBAR)),
        )
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return SettingsSiteSettingsAutoplaySelectors.all.filter { it.groups.contains(group) }
    }
}
