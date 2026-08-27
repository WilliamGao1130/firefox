/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui

import androidx.compose.ui.test.junit4.v2.AndroidComposeTestRule as AndroidComposeTestRuleV2
import androidx.test.uiautomator.UiSelector
import org.junit.Rule
import org.junit.Test
import org.bluepowerrobotics.bpfox.Config
import org.bluepowerrobotics.bpfox.customannotations.SkipLeaks
import org.bluepowerrobotics.bpfox.helpers.AppAndSystemHelper.runWithCondition
import org.bluepowerrobotics.bpfox.helpers.FenixTestRule
import org.bluepowerrobotics.bpfox.helpers.HomeActivityIntentTestRule
import org.bluepowerrobotics.bpfox.helpers.TestHelper.mDevice
import org.bluepowerrobotics.bpfox.helpers.perf.DetectMemoryLeaksRule
import org.bluepowerrobotics.bpfox.ui.robots.clickRateButtonGooglePlay
import org.bluepowerrobotics.bpfox.ui.robots.homeScreen

/** Tests for verifying the main three dot menu options */
class SettingsAboutTest {
    @get:Rule(order = 0) val fenixTestRule: FenixTestRule = FenixTestRule()

    @get:Rule(order = 1)
    val composeTestRule =
        AndroidComposeTestRuleV2(HomeActivityIntentTestRule.withDefaultSettingsOverrides()) { it.activity }

    @get:Rule(order = 2) val memoryLeaksRule = DetectMemoryLeaksRule(composeTestRule = { composeTestRule })

    // Walks through the About settings menu to ensure all items are present
    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/2092700
    @SkipLeaks(reasons = ["https://bugzilla.mozilla.org/show_bug.cgi?id=2011974"])
    @Test
    fun verifyAboutSettingsItemsTest() {
        homeScreen(composeTestRule) {}
            .openThreeDotMenu {}
            .clickSettingsButton {
                verifyAboutHeading()
                verifyRateOnGooglePlay()
                verifyAboutFirefoxPreview()
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/246966
    @SkipLeaks(reasons = ["https://bugzilla.mozilla.org/show_bug.cgi?id=2011974"])
    @Test
    fun verifyRateOnGooglePlayButtonTest() {
        homeScreen(composeTestRule) {}
            .openThreeDotMenu {}
            .clickSettingsButton {
                clickRateButtonGooglePlay()
                verifyGooglePlayRedirect(composeTestRule)
                // press back to return to the app, or accept ToS if still visible
                mDevice.pressBack()
                dismissGooglePlayToS()
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/3132646
    @SkipLeaks(reasons = ["https://bugzilla.mozilla.org/show_bug.cgi?id=2011974"])
    @Test
    fun verifyLibrariesListInReleaseBuildsTest() {
        runWithCondition(Config.channel.isReleased) {
            homeScreen(composeTestRule) {}
                .openThreeDotMenu {}
                .clickSettingsButton {}
                .openAboutFirefoxPreview {
                    verifyLibrariesUsedLink()
                    verifyTheLibrariesListNotEmpty()
                }
        }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/3132639
    @SkipLeaks(reasons = ["https://bugzilla.mozilla.org/show_bug.cgi?id=2011974"])
    @Test
    fun verifyAboutFirefoxMenuAppDetailsItemTest() {
        homeScreen(composeTestRule) {}
            .openThreeDotMenu {}
            .clickSettingsButton {}
            .openAboutFirefoxPreview {
                verifyAboutToolbar()
                verifyAboutFirefoxPreviewInfo()
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/3132640
    @SkipLeaks(reasons = ["https://bugzilla.mozilla.org/show_bug.cgi?id=2011974"])
    @Test
    fun verifyAboutFirefoxMenuWhatsNewInFirefoxItemTest() {
        homeScreen(composeTestRule) {}
            .openThreeDotMenu {}
            .clickSettingsButton {}
            .openAboutFirefoxPreview {
                verifyAboutToolbar()
                verifyWhatIsNewInFirefoxLink(composeTestRule)
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/3132641
    @SkipLeaks(reasons = ["https://bugzilla.mozilla.org/show_bug.cgi?id=2011974"])
    @Test
    fun verifyAboutFirefoxMenuSupportItemTest() {
        homeScreen(composeTestRule) {}
            .openThreeDotMenu {}
            .clickSettingsButton {}
            .openAboutFirefoxPreview {
                verifyAboutToolbar()
                verifySupportLink(composeTestRule)
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/3132642
    @SkipLeaks(reasons = ["https://bugzilla.mozilla.org/show_bug.cgi?id=2011974"])
    @Test
    fun verifyAboutFirefoxMenuCrashesItemTest() {
        homeScreen(composeTestRule) {}
            .openThreeDotMenu {}
            .clickSettingsButton {}
            .openAboutFirefoxPreview {
                verifyAboutToolbar()
                verifyCrashesLink()
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/3132643
    @SkipLeaks(reasons = ["https://bugzilla.mozilla.org/show_bug.cgi?id=2011974"])
    @Test
    fun verifyAboutFirefoxMenuPrivacyNoticeItemTest() {
        homeScreen(composeTestRule) {}
            .openThreeDotMenu {}
            .clickSettingsButton {}
            .openAboutFirefoxPreview {
                verifyAboutToolbar()
                verifyPrivacyNoticeLink(composeTestRule)
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/3132644
    @SkipLeaks(reasons = ["https://bugzilla.mozilla.org/show_bug.cgi?id=2011974"])
    @Test
    fun verifyAboutFirefoxMenuKnowYourRightsItemTest() {
        homeScreen(composeTestRule) {}
            .openThreeDotMenu {}
            .clickSettingsButton {}
            .openAboutFirefoxPreview {
                verifyAboutToolbar()
                verifyKnowYourRightsLink(composeTestRule)
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/3132645
    @SkipLeaks(reasons = ["https://bugzilla.mozilla.org/show_bug.cgi?id=2011974"])
    @Test
    fun verifyAboutFirefoxMenuLicensingInformationItemTest() {
        homeScreen(composeTestRule) {}
            .openThreeDotMenu {}
            .clickSettingsButton {}
            .openAboutFirefoxPreview {
                verifyAboutToolbar()
                verifyLicensingInformationLink(composeTestRule)
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/3132646
    @SkipLeaks(reasons = ["https://bugzilla.mozilla.org/show_bug.cgi?id=2011974"])
    @Test
    fun verifyAboutFirefoxMenuLibrariesThatWeUseItemTest() {
        homeScreen(composeTestRule) {}
            .openThreeDotMenu {}
            .clickSettingsButton {}
            .openAboutFirefoxPreview {
                verifyAboutToolbar()
                verifyLibrariesUsedLink()
            }
    }
}

private fun dismissGooglePlayToS() {
    if (mDevice.findObject(UiSelector().textContains("Terms of Service")).exists()) {
        mDevice.findObject(UiSelector().textContains("ACCEPT")).click()
    }
}
