/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.tests

import org.junit.Test
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.customannotations.SmokeTest
import org.bluepowerrobotics.bpfox.helpers.DataGenerationHelper.getStringResource
import org.bluepowerrobotics.bpfox.helpers.TestAssetHelper.loremIpsumAsset
import org.bluepowerrobotics.bpfox.helpers.TestHelper.mDevice
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.BaseTest
import org.bluepowerrobotics.bpfox.ui.util.FRENCH_FOLLOW_DEVICE_LANGUAGE_OPTION
import org.bluepowerrobotics.bpfox.ui.util.FRENCH_LANGUAGE_HEADER
import org.bluepowerrobotics.bpfox.ui.util.ROMANIAN_LANGUAGE_HEADER

class SettingsGeneralTest : BaseTest() {
    private val mockWebServer
        get() = fenixTestRule.mockWebServer

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/344213
    @SmokeTest
    @Test
    fun verifyFontSizingChangeTest() {
        val webpage = mockWebServer.loremIpsumAsset.url
        val textSizePercentage = 180

        on.settingsAccessibility
            .navigateToPage()
            .verifyFontSizingMenuItems(
                isTheAutomaticFontSizingToggleChecked = true,
                isTheFontSizingSliderEnabled = false,
                isTheZoomOnAllWebsitesToggleChecked = false,
            )
            .clickAutomaticFontSizingToggle()
            .verifyFontSizingMenuItems(
                isTheAutomaticFontSizingToggleChecked = false,
                isTheFontSizingSliderEnabled = true,
                isTheZoomOnAllWebsitesToggleChecked = false,
            )
            .changeTextSizeSlider(textSizePercentage)
            .verifyTextSizePercentage(textSizePercentage)

        on.browserPage.navigateToPage(webpage.toString()).verifyTextSizeOnWebsite(textSizePercentage)
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/243583
    @SmokeTest
    @Test
    fun changeDefaultBrowserSetting() {
        on.settings
            .navigateToPage()
            .verifyDefaultBrowserToggle(false)
            .clickDefaultBrowserSwitch()
            .verifyAndroidDefaultAppsMenuAppears()

        // Dismiss the system default-apps request.
        mDevice.pressBack()
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/516079
    @SmokeTest
    @Test
    fun setAppLanguageDifferentThanSystemLanguageTest() {
        val enLanguageHeaderText = getStringResource(R.string.preferences_language)

        on.settingsLanguage.navigateToPage()
        on.settingsLanguage.selectLanguage("Romanian").verifyLanguageSettingHeaderIsTranslated(ROMANIAN_LANGUAGE_HEADER)
        on.settingsLanguage.selectLanguage("Français").verifyLanguageSettingHeaderIsTranslated(FRENCH_LANGUAGE_HEADER)
        on.settingsLanguage
            .selectLanguage(FRENCH_FOLLOW_DEVICE_LANGUAGE_OPTION)
            .verifyLanguageSettingHeaderIsTranslated(enLanguageHeaderText)
    }
}
