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
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSiteSettingsExceptionsSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSiteSettingsSelectors

class SettingsSiteSettingsExceptionsPage(composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>) :
    BasePage(composeRule) {
    override val pageName = "SettingsSiteSettingsExceptionsPage"

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
                    NavigationStep.Swipe(SettingsSiteSettingsSelectors.EXCEPTIONS_BUTTON),
                    NavigationStep.Click(SettingsSiteSettingsSelectors.EXCEPTIONS_BUTTON),
                ),
        )
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return SettingsSiteSettingsExceptionsSelectors.all.filter { it.groups.contains(group) }
    }
}
