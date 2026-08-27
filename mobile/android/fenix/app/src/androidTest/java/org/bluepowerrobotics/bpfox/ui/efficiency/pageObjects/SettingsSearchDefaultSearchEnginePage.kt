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
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSearchDefaultSearchEngineSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSearchSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSelectors

class SettingsSearchDefaultSearchEnginePage(composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>) :
    BasePage(composeRule) {
    override val pageName = "SettingsSearchDefaultSearchEnginePage"

    init {
        NavigationRegistry.register(
            from = "HomePage",
            to = pageName,
            steps =
                listOf(
                    NavigationStep.Click(HomeSelectors.MAIN_MENU_BUTTON),
                    NavigationStep.Click(MainMenuSelectors.SETTINGS_BUTTON),
                    NavigationStep.Click(SettingsSelectors.SEARCH_BUTTON),
                    NavigationStep.Click(SettingsSearchSelectors.DEFAULT_SEARCH_ENGINE_SETTING_OPTION),
                ),
        )

        NavigationRegistry.register(
            from = pageName,
            to = "HomePage",
            steps =
                listOf(
                    NavigationStep.Click(SettingsSelectors.GO_BACK_BUTTON),
                    NavigationStep.Click(SettingsSelectors.GO_BACK_BUTTON),
                    NavigationStep.Click(SettingsSelectors.GO_BACK_BUTTON),
                ),
        )
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return SettingsSearchDefaultSearchEngineSelectors.all.filter { it.groups.contains(group) }
    }
}
