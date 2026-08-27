/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.tests

import org.junit.Test
import org.bluepowerrobotics.bpfox.customannotations.SmokeTest
import org.bluepowerrobotics.bpfox.helpers.TestAssetHelper.getGenericAsset
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.BaseTest
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.BrowserPageSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.HomeSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.MainMenuSelectors

class ShortcutsTest : BaseTest() {

    private val mockWebServer
        get() = fenixTestRule.mockWebServer

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/532598
    @SmokeTest
    @Test
    fun addAWebsiteAsATopSiteTest() {
        val defaultWebPage = mockWebServer.getGenericAsset(1)

        on.home.navigateToPage().mozVerifyElementsByGroup("topSitesCompose")
        on.browserPage.navigateToPage(defaultWebPage.url.toString()).verifyPageContent(defaultWebPage.content)
        on.mainMenu
            .navigateToPage()
            .mozClick(MainMenuSelectors.MORE_BUTTON)
            .mozVerify(MainMenuSelectors.ADD_TO_SHORTCUTS_BUTTON)
            .mozClick(MainMenuSelectors.ADD_TO_SHORTCUTS_BUTTON)
        on.browserPage.navigateToPage().mozVerify(BrowserPageSelectors.ADDED_TO_SHORTCUTS_SNACKBAR_TEXT)
        on.home
            .navigateToPage()
            .mozVerifyElementsByGroup("topSitesCompose")
            .mozVerify(HomeSelectors.TOP_SITE_ITEM(defaultWebPage.title))
    }
}
