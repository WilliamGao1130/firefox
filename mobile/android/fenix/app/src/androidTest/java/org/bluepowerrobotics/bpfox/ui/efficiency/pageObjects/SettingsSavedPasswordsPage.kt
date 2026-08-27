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
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.BrowserPageSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.HomeSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.MainMenuSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsPasswordsSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSavedPasswordsSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSelectors

class SettingsSavedPasswordsPage(composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>) :
    BasePage(composeRule) {
    override val pageName = "SettingsSavedPasswordsPage"

    init {
        NavigationRegistry.register(
            from = "HomePage",
            to = pageName,
            steps =
                listOf(
                    NavigationStep.Click(HomeSelectors.MAIN_MENU_BUTTON),
                    NavigationStep.Click(MainMenuSelectors.SETTINGS_BUTTON),
                    NavigationStep.Click(SettingsSelectors.PASSWORDS_BUTTON),
                    NavigationStep.Click(SettingsPasswordsSelectors.SAVED_PASSWORDS_OPTION),
                    NavigationStep.ClickIfPresent(SettingsSavedPasswordsSelectors.LOGINS_SECURITY_DIALOG_LATER_BUTTON),
                ),
        )

        NavigationRegistry.register(
            from = "HomePage",
            to = pageName,
            steps =
                listOf(
                    NavigationStep.Click(HomeSelectors.MAIN_MENU_BUTTON),
                    NavigationStep.Click(MainMenuSelectors.PASSWORDS_BUTTON),
                    NavigationStep.ClickIfPresent(SettingsSavedPasswordsSelectors.LOGINS_SECURITY_DIALOG_LATER_BUTTON),
                ),
        )

        NavigationRegistry.register(
            from = "BrowserPage",
            to = pageName,
            steps =
                listOf(
                    NavigationStep.Click(BrowserPageSelectors.MAIN_MENU_BUTTON),
                    NavigationStep.Click(MainMenuSelectors.PASSWORDS_BUTTON),
                    NavigationStep.ClickIfPresent(SettingsSavedPasswordsSelectors.LOGINS_SECURITY_DIALOG_LATER_BUTTON),
                ),
        )

        NavigationRegistry.register(
            from = pageName,
            to = "BrowserPage",
            steps =
                listOf(
                    NavigationStep.Click(SettingsSavedPasswordsSelectors.GO_BACK_BUTTON),
                    NavigationStep.ClickIfPresent(SettingsPasswordsSelectors.GO_BACK_BUTTON),
                    NavigationStep.ClickIfPresent(SettingsSelectors.GO_BACK_BUTTON),
                ),
        )
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return SettingsSavedPasswordsSelectors.all.filter { it.groups.contains(group) }
    }
}
