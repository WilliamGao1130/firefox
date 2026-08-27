/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui

import androidx.compose.ui.test.junit4.v2.AndroidComposeTestRule as AndroidComposeTestRuleV2
import org.junit.Rule
import org.junit.Test
import org.bluepowerrobotics.bpfox.customannotations.Converted
import org.bluepowerrobotics.bpfox.customannotations.SmokeTest
import org.bluepowerrobotics.bpfox.helpers.FenixTestRule
import org.bluepowerrobotics.bpfox.helpers.HomeActivityIntentTestRule
import org.bluepowerrobotics.bpfox.helpers.TestAssetHelper.articleSummaryAsset
import org.bluepowerrobotics.bpfox.helpers.TestAssetHelper.loremIpsumAsset
import org.bluepowerrobotics.bpfox.helpers.perf.DetectMemoryLeaksRule
import org.bluepowerrobotics.bpfox.ui.robots.homeScreen
import org.bluepowerrobotics.bpfox.ui.robots.navigationToolbar

class SettingsPageSummariesTest {
    @get:Rule(order = 0) val fenixTestRule: FenixTestRule = FenixTestRule()

    private val mockWebServer
        get() = fenixTestRule.mockWebServer

    @get:Rule(order = 1)
    val composeTestRule =
        AndroidComposeTestRuleV2(
            HomeActivityIntentTestRule(
                skipOnboarding = true,
                shakeToSummarizeFeatureFlagEnabled = true,
                hasSeenShakeToSummarizeToolbarCfr = false,
            )
        ) {
            it.activity
        }

    @get:Rule(order = 2) val memoryLeaksRule = DetectMemoryLeaksRule(composeTestRule = { composeTestRule })

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/4036042
    @Converted(
        replacedBy = ["org.bluepowerrobotics.bpfox.ui.efficiency.tests.SettingsPageSummariesTest#verifyPageSummariesUITest"],
        bug = 2062914,
        since = "2026-08",
    )
    @SmokeTest
    @Test
    fun verifyPageSummariesUITest() {
        homeScreen(composeTestRule) {}
            .openThreeDotMenu {}
            .clickSettingsButton {
                verifyPageSummariesButton()
            }
            .openPageSummariesSubMenu(composeTestRule) {
                verifyPageSummariesView()
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/4036045
    @Test
    fun verifyTheSummarizePagesToggleBehaviourTest() {
        val articlePage = mockWebServer.articleSummaryAsset
        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(articlePage.url) {
                waitForPageToLoad()
                clickTheDismissButtonOnSummarizeCFR()
            }
            .openThreeDotMenu {}
            .clickSettingsButton {}
            .openPageSummariesSubMenu(composeTestRule) {
                verifySummarizePagesToggle(true)
                clickSummarizePagesToggle()
                verifySummarizePagesToggle(false)
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/4035976
    @Test
    fun verifyTheShakeToSummarizeCFRTest() {
        val articlePage = mockWebServer.articleSummaryAsset
        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(articlePage.url) {
                waitForPageToLoad()
                verifyTheSummarizeCFR(true)
                clickTheDismissButtonOnSummarizeCFR()
                verifyTheSummarizeCFR(false)
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/4035977
    @Test
    fun verifyTheShakeToSummarizeCFRIsOnlyDisplayedOnceTest() {
        val firstWebsite = mockWebServer.articleSummaryAsset
        val secondWebsite = mockWebServer.loremIpsumAsset
        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(firstWebsite.url) {
                waitForPageToLoad()
                verifyTheSummarizeCFR(true)
                clickTheDismissButtonOnSummarizeCFR()
            }
        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(secondWebsite.url) {
                waitForPageToLoad()
                verifyTheSummarizeCFR(false)
            }
    }
}
