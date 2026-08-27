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
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsEnhancedTrackingProtectionExceptionsSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsEnhancedTrackingProtectionSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSelectors

class SettingsEnhancedTrackingProtectionExceptionsPage(
    composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>
) : BasePage(composeRule) {
    override val pageName = "SettingsEnhancedTrackingProtectionExceptionsPage"

    init {
        NavigationRegistry.register(
            from = "HomePage",
            to = pageName,
            steps =
                listOf(
                    NavigationStep.Click(HomeSelectors.MAIN_MENU_BUTTON),
                    NavigationStep.Click(MainMenuSelectors.SETTINGS_BUTTON),
                    NavigationStep.Swipe(SettingsSelectors.ENHANCED_TRACKING_PROTECTION_BUTTON),
                    NavigationStep.Click(SettingsSelectors.ENHANCED_TRACKING_PROTECTION_BUTTON),
                    NavigationStep.Click(
                        SettingsEnhancedTrackingProtectionSelectors.ENHANCED_TRACKING_PROTECTION_EXCEPTIONS_BUTTON
                    ),
                ),
        )
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return SettingsEnhancedTrackingProtectionExceptionsSelectors.all.filter { it.groups.contains(group) }
    }
}
