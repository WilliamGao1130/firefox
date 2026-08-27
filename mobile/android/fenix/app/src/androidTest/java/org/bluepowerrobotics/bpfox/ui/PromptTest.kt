/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui

import androidx.compose.ui.test.junit4.v2.AndroidComposeTestRule as AndroidComposeTestRuleV2
import androidx.test.espresso.Espresso.closeSoftKeyboard
import mozilla.components.feature.prompts.R as promptsR
import org.junit.Rule
import org.junit.Test
import org.bluepowerrobotics.bpfox.helpers.DataGenerationHelper.getStringResource
import org.bluepowerrobotics.bpfox.helpers.FenixTestRule
import org.bluepowerrobotics.bpfox.helpers.HomeActivityTestRule
import org.bluepowerrobotics.bpfox.helpers.MatcherHelper
import org.bluepowerrobotics.bpfox.helpers.MatcherHelper.itemContainingText
import org.bluepowerrobotics.bpfox.helpers.TestAssetHelper.getGenericAsset
import org.bluepowerrobotics.bpfox.helpers.TestAssetHelper.promptAsset
import org.bluepowerrobotics.bpfox.helpers.TestHelper.waitForAppWindowToBeUpdated
import org.bluepowerrobotics.bpfox.helpers.perf.DetectMemoryLeaksRule
import org.bluepowerrobotics.bpfox.ui.robots.clickPageObject
import org.bluepowerrobotics.bpfox.ui.robots.navigationToolbar

/**
 * Tests for verifying basic functionality of prompts
 *
 * Including:
 * - beforeunload prompt
 */
class PromptTest {
    @get:Rule(order = 0) val fenixTestRule: FenixTestRule = FenixTestRule()

    private val mockWebServer
        get() = fenixTestRule.mockWebServer

    @get:Rule(order = 1)
    val composeTestRule = AndroidComposeTestRuleV2(HomeActivityTestRule.withDefaultSettingsOverrides()) { it.activity }

    @get:Rule(order = 2) val memoryLeaksRule = DetectMemoryLeaksRule(composeTestRule = { composeTestRule })

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/4025097
    @Test
    fun verifyBeforeUnloadPrompt() {
        val defaultWebPage = mockWebServer.getGenericAsset(1)
        val promptWebPage = mockWebServer.promptAsset

        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(promptWebPage.url) {
                clickPageObject(composeTestRule, MatcherHelper.itemWithResId("nameInput"))
            }

        navigationToolbar(composeTestRule) {
                closeSoftKeyboard()
                waitForAppWindowToBeUpdated()
            }
            .enterURLAndEnterToBrowser(defaultWebPage.url) {
                verifyBeforeUnloadPromptExists()
            }
    }
}

private fun verifyBeforeUnloadPromptExists() =
    MatcherHelper.assertUIObjectExists(
        itemContainingText(getStringResource(promptsR.string.mozac_feature_prompt_before_unload_dialog_body))
    )
