/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.bluepowerrobotics.bpfox.ui

import androidx.compose.ui.test.junit4.v2.AndroidComposeTestRule as AndroidComposeTestRuleV2
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.customannotations.SmokeTest
import org.bluepowerrobotics.bpfox.helpers.DataGenerationHelper.getStringResource
import org.bluepowerrobotics.bpfox.helpers.FenixTestRule
import org.bluepowerrobotics.bpfox.helpers.HomeActivityIntentTestRule
import org.bluepowerrobotics.bpfox.helpers.TestAssetHelper.getGenericAsset
import org.bluepowerrobotics.bpfox.helpers.TestHelper.mDevice
import org.bluepowerrobotics.bpfox.helpers.perf.DetectMemoryLeaksRule
import org.bluepowerrobotics.bpfox.ui.robots.navigationToolbar
import org.bluepowerrobotics.bpfox.ui.robots.surveyScreen

class MicrosurveyTest {
    @get:Rule(order = 0) val fenixTestRule: FenixTestRule = FenixTestRule()

    private val mockWebServer
        get() = fenixTestRule.mockWebServer

    @get:Rule(order = 1)
    val composeTestRule =
        AndroidComposeTestRuleV2(
            HomeActivityIntentTestRule(
                skipOnboarding = true,
                isMicrosurveyEnabled = true,
            )
        ) {
            it.activity
        }

    @get:Rule(order = 2) val memoryLeaksRule = DetectMemoryLeaksRule(composeTestRule = { composeTestRule })

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/2809354
    @SmokeTest
    @Test
    fun activationOfThePrintMicrosurveyTest() {
        val testPage = mockWebServer.getGenericAsset(1)

        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(testPage.url) {}
            .openThreeDotMenu {}
            .clickShareButton {}
            .clickPrintButton(composeTestRule) {
                mDevice.waitForIdle()
                mDevice.pressBack()
            }
        surveyScreen(composeTestRule) {
            verifyThePrintSurveyPrompt(composeTestRule = composeTestRule, exists = true)
        }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/2809349
    @SmokeTest
    @Test
    fun verifyTheSurveyRemainsActivatedWhileChangingTabsTest() {
        val testPage1 = mockWebServer.getGenericAsset(1)
        val testPage2 = mockWebServer.getGenericAsset(2)

        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(testPage1.url) {}
            .openThreeDotMenu {}
            .clickShareButton {}
            .clickPrintButton(composeTestRule) {
                mDevice.waitForIdle()
                mDevice.pressBack()
            }
        surveyScreen(composeTestRule) {
                clickContinueSurveyButton(composeTestRule)
                verifyPleaseCompleteTheSurveyHeader(composeTestRule)
                selectAnswer("Very satisfied", composeTestRule)
            }
            .collapseSurveyByTappingBackButton {}
        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(testPage2.url) {
                mDevice.waitForIdle()
                surveyScreen(composeTestRule) {
                    verifyTheSurveyTitle(
                        getStringResource(R.string.microsurvey_prompt_printing_title),
                        composeTestRule,
                        true,
                    )
                }
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/2809361
    @Ignore(
        "Disabled after enabling the composable toolbar and main menu: https://bugzilla.mozilla.org/show_bug.cgi?id=2006295"
    )
    @SmokeTest
    @Test
    fun verifyTheSurveyConfirmationSheetTest() {
        val testPage = mockWebServer.getGenericAsset(1)

        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(testPage.url) {}
            .openThreeDotMenu {}
            .clickShareButton {}
            .clickPrintButton(composeTestRule) {
                mDevice.waitForIdle()
                mDevice.pressBack()
            }
        surveyScreen(composeTestRule) {
            clickContinueSurveyButton(composeTestRule)
            expandSurveySheet(composeTestRule)
            selectAnswer("Very satisfied", composeTestRule)
            clickSubmitButton(composeTestRule)
            verifySurveyCompletedScreen(composeTestRule)
        }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/2809344
    @Ignore(
        "Disabled after enabling the composable toolbar and main menu: https://bugzilla.mozilla.org/show_bug.cgi?id=2006295"
    )
    @Test
    fun dismissTheSurveyPromptTest() {
        val testPage = mockWebServer.getGenericAsset(1)

        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(testPage.url) {}
            .openThreeDotMenu {}
            .clickShareButton {}
            .clickPrintButton(composeTestRule) {
                mDevice.waitForIdle()
                mDevice.pressBack()
            }
        surveyScreen(composeTestRule) {
                verifyThePrintSurveyPrompt(composeTestRule = composeTestRule, exists = true)
                clickOutsideTheSurveyPrompt()
                verifyThePrintSurveyPrompt(composeTestRule = composeTestRule, exists = true)
            }
            .clickHomeScreenSurveyCloseButton {}
        surveyScreen(composeTestRule) {
            verifyThePrintSurveyPrompt(composeTestRule = composeTestRule, exists = false)
        }
    }
}
