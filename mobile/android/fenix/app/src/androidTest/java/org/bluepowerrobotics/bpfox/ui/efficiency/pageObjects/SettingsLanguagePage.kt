/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.pageObjects

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import org.bluepowerrobotics.bpfox.helpers.HomeActivityIntentTestRule
import org.bluepowerrobotics.bpfox.helpers.TestHelper.packageName
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.BasePage
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.Selector
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.SelectorStrategy
import org.bluepowerrobotics.bpfox.ui.efficiency.navigation.NavigationRegistry
import org.bluepowerrobotics.bpfox.ui.efficiency.navigation.NavigationStep
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.HomeSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.MainMenuSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsLanguageSelectors
import org.bluepowerrobotics.bpfox.ui.efficiency.selectors.SettingsSelectors

class SettingsLanguagePage(composeRule: AndroidComposeTestRule<HomeActivityIntentTestRule, *>) : BasePage(composeRule) {
    override val pageName = "SettingsLanguagePage"

    init {
        NavigationRegistry.register(
            from = "HomePage",
            to = pageName,
            steps =
                listOf(
                    NavigationStep.Click(HomeSelectors.MAIN_MENU_BUTTON),
                    NavigationStep.Click(MainMenuSelectors.SETTINGS_BUTTON),
                    NavigationStep.Swipe(SettingsSelectors.LANGUAGE_BUTTON),
                    NavigationStep.Click(SettingsSelectors.LANGUAGE_BUTTON),
                ),
        )
    }

    override fun mozGetSelectorsByGroup(group: String): List<Selector> {
        return SettingsLanguageSelectors.all.filter { it.groups.contains(group) }
    }

    fun selectLanguage(language: String): SettingsLanguagePage {
        languagesList().getChildByText(UiSelector().text(language), language).click()

        return this
    }

    fun verifyLanguageSettingHeaderIsTranslated(translatedLanguage: String): SettingsLanguagePage {
        mozVerify(
            Selector(
                strategy = SelectorStrategy.UIAUTOMATOR_WITH_TEXT_CONTAINS,
                value = translatedLanguage,
                description = "Translated language setting header",
                groups = listOf(),
            )
        )
        return this
    }

    private fun languagesList() = UiScrollable(UiSelector().resourceId("$packageName:id/locale_list").scrollable(true))
}
