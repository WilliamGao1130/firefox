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
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.BookmarkSearchSelectors

class BookmarkSearchPage(composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>) : BasePage(composeRule) {
    override val pageName = "BookmarkSearchPage"

    init {
        NavigationRegistry.register(
            from = pageName,
            to = "BookmarksPage",
            steps = listOf(NavigationStep.PressBack),
        )
    }

    override fun navigateToPage(url: String, forceNavigation: Boolean): BookmarkSearchPage {
        super.navigateToPage(url, forceNavigation)
        return this
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return BookmarkSearchSelectors.all.filter { it.groups.contains(group) }
    }

    fun typeSearch(searchTerm: String): BookmarkSearchPage {
        mozClearAndEnterText(searchTerm, BookmarkSearchSelectors.SEARCH_BOX)
        return this
    }

    fun verifySearchSuggestionsAreDisplayed(vararg urls: String): BookmarkSearchPage {
        for (url in urls) {
            mozVerifyAnyContainsText(BookmarkSearchSelectors.SEARCH_ITEM, url)
        }
        return this
    }

    fun verifySuggestionsAreNotDisplayed(vararg urls: String): BookmarkSearchPage {
        for (url in urls) {
            mozVerifyNoneContainText(BookmarkSearchSelectors.SEARCH_ITEM, url)
        }
        return this
    }
}
