/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui

import androidx.compose.ui.test.junit4.v2.AndroidComposeTestRule as AndroidComposeTestRuleV2
import androidx.core.net.toUri
import org.junit.Rule
import org.junit.Test
import org.bluepowerrobotics.bpfox.customannotations.Converted
import org.bluepowerrobotics.bpfox.customannotations.SmokeTest
import org.bluepowerrobotics.bpfox.helpers.AppAndSystemHelper.clickSystemHomeScreenShortcutAddButton
import org.bluepowerrobotics.bpfox.helpers.FenixTestRule
import org.bluepowerrobotics.bpfox.helpers.HomeActivityTestRule
import org.bluepowerrobotics.bpfox.helpers.MatcherHelper.itemContainingText
import org.bluepowerrobotics.bpfox.helpers.TestHelper.mDevice
import org.bluepowerrobotics.bpfox.helpers.perf.DetectMemoryLeaksRule
import org.bluepowerrobotics.bpfox.ui.robots.clickPageObject
import org.bluepowerrobotics.bpfox.ui.robots.customTabScreen
import org.bluepowerrobotics.bpfox.ui.robots.navigationToolbar
import org.bluepowerrobotics.bpfox.ui.robots.pwaScreen

class PwaTest {
    /* Updated externalLinks.html to v2.0,
      changed the hypertext reference to mozilla-mobile.github.io/testapp/downloads for "External link"
    */
    @get:Rule(order = 0) val fenixTestRule: FenixTestRule = FenixTestRule()

    private val externalLinksPWAPage = "https://mozilla-mobile.github.io/testapp/v2.0/externalLinks.html"
    private val shortcutTitle = "TEST_APP"

    @get:Rule(order = 1)
    val composeTestRule = AndroidComposeTestRuleV2(HomeActivityTestRule.withDefaultSettingsOverrides()) { it.activity }

    @get:Rule(order = 2) val memoryLeaksRule = DetectMemoryLeaksRule(composeTestRule = { composeTestRule })

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/845695
    @Test
    fun externalLinkPWATest() {
        val externalLinkURL = "https://mozilla-mobile.github.io/testapp/downloads"

        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(externalLinksPWAPage.toUri()) {
                verifyPageContent("Misc Link Types")
            }
            .openThreeDotMenu {
                clickTheMoreButton()
            }
            .clickAddAppToHomeScreenButton {
                clickSystemHomeScreenShortcutAddButton()
            }
            .openHomeScreenShortcut(shortcutTitle) {
                clickPageObject(composeTestRule, itemContainingText("External link"))
            }

        customTabScreen(composeTestRule) {
            verifyCustomTabToolbarTitle(externalLinkURL)
        }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/845694
    @Test
    fun appLikeExperiencePWATest() {
        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(externalLinksPWAPage.toUri()) {
                verifyPageContent("Misc Link Types")
            }
            .openThreeDotMenu {
                clickTheMoreButton()
            }
            .clickAddAppToHomeScreenButton {
                clickSystemHomeScreenShortcutAddButton()
            }
            .openHomeScreenShortcut(shortcutTitle) {}

        pwaScreen {
            verifyCustomTabToolbarIsNotDisplayed()
            verifyPwaActivityInCurrentTask()
        }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/834200
    @Converted(
        replacedBy = ["org.bluepowerrobotics.bpfox.ui.efficiency.tests.PwaTest#installPWAFromTheMainMenuTest"],
        bug = 2060905,
        since = "2026-08",
    )
    @SmokeTest
    @Test
    fun installPWAFromTheMainMenuTest() {
        val pwaPage = "https://mozilla-mobile.github.io/testapp/loginForm"

        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(pwaPage.toUri()) {
                waitForPageToLoad()
                verifyUrl("mozilla-mobile.github.io/testapp/loginForm")
                verifyPageContent("Login Form")
            }
            .openThreeDotMenu {
                clickTheMoreButton()
            }
            .clickAddAppToHomeScreenButton {
                clickSystemHomeScreenShortcutAddButton()
            }
            .openHomeScreenShortcut("TEST_APP") {
                mDevice.waitForIdle()
                verifyNavURLBarHidden()
            }
    }
}
