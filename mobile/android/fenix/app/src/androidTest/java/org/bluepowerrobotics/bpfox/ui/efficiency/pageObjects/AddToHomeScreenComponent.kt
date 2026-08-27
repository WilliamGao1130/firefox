/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.pageObjects

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import org.bluepowerrobotics.bpfox.helpers.HomeActivityIntentTestRule
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.BasePage
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.Selector
import org.bluepowerrobotics.bpfox.ui.efficiency.navigation.NavigationRegistry
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.AddToHomeScreenSelectors

class AddToHomeScreenComponent(composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>) :
    BasePage(composeRule) {
    override val pageName = "AddToHomeScreenComponent"

    init {
        NavigationRegistry.register(
            from = pageName,
            to = "BrowserPage",
            steps = listOf(),
        )
    }

    override fun navigateToPage(url: String, forceNavigation: Boolean): AddToHomeScreenComponent {
        super.navigateToPage(url = url.ifBlank { "example.com" }, forceNavigation = forceNavigation)
        return this
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return AddToHomeScreenSelectors.all.filter { it.groups.contains(group) }
    }
}
