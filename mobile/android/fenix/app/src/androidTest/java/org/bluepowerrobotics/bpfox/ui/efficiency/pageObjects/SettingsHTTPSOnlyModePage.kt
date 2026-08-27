/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.pageObjects

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.helpers.HomeActivityIntentTestRule
import org.bluepowerrobotics.bpfox.helpers.TestHelper.appContext
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.BasePage
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.Selector
import org.bluepowerrobotics.bpfox.ui.efficiency.navigation.NavigationRegistry
import org.bluepowerrobotics.bpfox.ui.efficiency.navigation.NavigationStep
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.HomeSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.MainMenuSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsHTTPSOnlyModeSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSelectors

class SettingsHTTPSOnlyModePage(composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>) :
    BasePage(composeRule) {
    override val pageName = "SettingsHTTPSOnlyModePage"

    init {
        NavigationRegistry.register(
            from = "HomePage",
            to = pageName,
            steps =
                listOf(
                    NavigationStep.Click(HomeSelectors.MAIN_MENU_BUTTON),
                    NavigationStep.Click(MainMenuSelectors.SETTINGS_BUTTON),
                    NavigationStep.Swipe(SettingsSelectors.HTTPS_ONLY_MODE_BUTTON),
                    NavigationStep.Click(SettingsSelectors.HTTPS_ONLY_MODE_BUTTON),
                ),
        )

        NavigationRegistry.register(
            from = pageName,
            to = "SettingsPage",
            steps = listOf(NavigationStep.Click(SettingsSelectors.GO_BACK_BUTTON)),
        )
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return SettingsHTTPSOnlyModeSelectors.all.filter { it.groups.contains(group) }
    }

    override fun navigateToPage(url: String, forceNavigation: Boolean): SettingsHTTPSOnlyModePage {
        super.navigateToPage(url, forceNavigation)
        return this
    }

    fun enableHttpsOnlyMode(): SettingsHTTPSOnlyModePage {
        if (!appContext.components.settings.shouldUseHttpsOnly) {
            mozClick(SettingsHTTPSOnlyModeSelectors.HTTPS_ONLY_MODE_TOGGLE)
        }
        return this
    }

    fun verifyHttpsOnlyAllTabsSelected(): SettingsHTTPSOnlyModePage {
        mozVerifyElementIsChecked(SettingsHTTPSOnlyModeSelectors.HTTPS_ONLY_ALL_TABS_OPTION)
        mozVerifyElementIsNotChecked(SettingsHTTPSOnlyModeSelectors.HTTPS_ONLY_PRIVATE_TABS_OPTION)
        return this
    }
}
