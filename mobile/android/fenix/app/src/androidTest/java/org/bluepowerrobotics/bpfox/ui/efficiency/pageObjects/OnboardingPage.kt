/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.pageObjects

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import org.bluepowerrobotics.bpfox.helpers.HomeActivityIntentTestRule
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.BasePage
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.Selector
import org.bluepowerrobotics.bpfox.ui.efficiency.navigation.LaunchConfig
import org.bluepowerrobotics.bpfox.ui.efficiency.navigation.NavigationRegistry
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.OnboardingSelectors

/**
 * The first-run Onboarding flow.
 *
 * Only reachable when the app launches with onboarding enabled — declare the test class as
 * BaseTest(LaunchConfig(skipOnboarding = false). The AppEntry -> OnboardingPage edge has no steps because the flow is
 * already on screen at launch; navigateToPage() confirms arrival via the `requiredForPage` selector group (the Terms of
 * Use card title).
 */
class OnboardingPage(composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>) : BasePage(composeRule) {

    override val pageName = "OnboardingPage"

    init {
        NavigationRegistry.register(
            from = "AppEntry",
            to = pageName,
            steps = listOf(),
            launch = LaunchConfig(skipOnboarding = false),
        )
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return OnboardingSelectors.all.filter { it.groups.contains(group) }
    }
}
