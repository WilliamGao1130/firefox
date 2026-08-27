/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.settings

import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.SwitchPreferenceCompat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import mozilla.components.service.pocket.PocketStoriesService
import mozilla.components.support.test.robolectric.testContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.bluepowerrobotics.bpfox.GleanMetrics.CustomizeHome
import org.bluepowerrobotics.bpfox.GleanMetrics.Events
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.components.AppStore
import org.bluepowerrobotics.bpfox.components.Components
import org.bluepowerrobotics.bpfox.components.Core
import org.bluepowerrobotics.bpfox.components.appstate.AppAction.ContentRecommendationsAction
import org.bluepowerrobotics.bpfox.ext.getPreferenceKey
import org.bluepowerrobotics.bpfox.helpers.FenixGleanTestRule
import org.bluepowerrobotics.bpfox.home.pocket.ContentRecommendationsFeatureHelper
import org.bluepowerrobotics.bpfox.utils.Settings
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class HomeSettingsFragmentTest {
    @get:Rule val gleanRule = FenixGleanTestRule(testContext)

    private lateinit var homeSettingsFragment: HomeSettingsFragment
    private lateinit var appSettings: Settings
    private lateinit var appPrefs: SharedPreferences
    private lateinit var appPrefsEditor: SharedPreferences.Editor
    private lateinit var pocketService: PocketStoriesService
    private lateinit var appStore: AppStore
    private lateinit var contentRecommendationsHelper: ContentRecommendationsFeatureHelper

    @Before
    fun setup() {
        appPrefsEditor = mockk(relaxed = true)
        appPrefs =
            mockk(relaxed = true) {
                every { edit() } returns appPrefsEditor
            }
        appSettings =
            mockk(relaxed = true) {
                every { preferences } returns appPrefs
            }
        appStore = mockk(relaxed = true)
        pocketService = mockk(relaxed = true)
        contentRecommendationsHelper = mockk(relaxed = true)
    }

    @Test
    fun `GIVEN the Pocket sponsored stories feature is disabled for the app WHEN accessing settings THEN the settings for it are not visible`() {
        every { contentRecommendationsHelper.isPocketSponsoredStoriesFeatureEnabled(any()) } returns false

        activateFragment()

        assertFalse(getSponsoredStoriesPreference().isVisible)
    }

    @Test
    fun `GIVEN the Pocket sponsored stories feature is enabled for the app WHEN accessing settings THEN the settings for it are visible`() {
        every { contentRecommendationsHelper.isPocketSponsoredStoriesFeatureEnabled(any()) } returns true

        activateFragment()

        assertTrue(getSponsoredStoriesPreference().isVisible)
    }

    @Test
    fun `GIVEN the Pocket sponsored stories preference is false WHEN accessing settings THEN the setting for it is unchecked`() {
        every { appSettings.showPocketSponsoredStories } returns false

        activateFragment()

        assertFalse(getSponsoredStoriesPreference().isChecked)
    }

    @Test
    fun `GIVEN the Pocket sponsored stories preference is true WHEN accessing settings THEN the setting for it is checked`() {
        every { appSettings.showPocketSponsoredStories } returns true

        activateFragment()

        assertTrue(getSponsoredStoriesPreference().isChecked)
    }

    @Test
    fun `GIVEN sponsored stories is disabled WHEN toggling the sponsored setting to enabled THEN start downloading sponsored stories`() {
        activateFragment()
        val result = getSponsoredStoriesPreference().callChangeListener(true)

        assertTrue(result)
        verify {
            appPrefsEditor.putBoolean(homeSettingsFragment.getString(R.string.pref_key_pocket_sponsored_stories), true)
            pocketService.startPeriodicSponsoredContentsRefresh()
        }
    }

    @Test
    fun `GIVEN sponsored stories is enabled WHEN toggling the sponsored stories setting to disabled THEN delete Pocket profile and remove sponsored contents from showing`() {
        activateFragment()
        val result = getSponsoredStoriesPreference().callChangeListener(false)

        assertTrue(result)
        verify {
            appPrefsEditor.putBoolean(homeSettingsFragment.getString(R.string.pref_key_pocket_sponsored_stories), false)
            pocketService.deleteUser()
            appStore.dispatch(ContentRecommendationsAction.SponsoredContentsChange(sponsoredContents = emptyList()))
        }
    }

    @Test
    fun `WHEN toggling the privacy report setting THEN events preference_toggled is recorded with the privacy_report key`() {
        activateFragment()

        val result = getPrivacyReportPreference().callChangeListener(true)

        assertTrue(result)
        val events = Events.preferenceToggled.testGetValue()!!
        assertEquals(1, events.size)
        assertEquals("privacy_report", events.single().extra?.get("preference_key"))
        assertEquals("true", events.single().extra?.get("enabled"))
    }

    @Test
    fun `WHEN toggling the weather setting THEN customize home preference_toggled is recorded with the weather key`() {
        every { appSettings.enableHomepageWeatherWidget } returns true

        activateFragment()

        val result = getWeatherPreference().callChangeListener(true)

        assertTrue(result)
        val events = CustomizeHome.preferenceToggled.testGetValue()!!
        assertEquals(1, events.size)
        assertEquals("weather", events.single().extra?.get("preference_key"))
        assertEquals("true", events.single().extra?.get("enabled"))
        verify {
            appPrefsEditor.putBoolean(
                homeSettingsFragment.getString(R.string.pref_key_show_homepage_weather_widget),
                true,
            )
        }
    }

    private fun activateFragment() {
        val activity = Robolectric.buildActivity(FragmentActivity::class.java).create().get()
        homeSettingsFragment = HomeSettingsFragment()

        val mockCore: Core = mockk {
            every { pocketStoriesService } returns this@HomeSettingsFragmentTest.pocketService
        }
        val mockComponents: Components =
            mockk(relaxed = true) {
                every { appStore } returns this@HomeSettingsFragmentTest.appStore
                every { core } returns mockCore
                every { settings } returns this@HomeSettingsFragmentTest.appSettings
            }

        homeSettingsFragment.fenixSettings = appSettings
        homeSettingsFragment.fenixComponents = mockComponents
        homeSettingsFragment.contentRecommendationsHelper = contentRecommendationsHelper

        activity.supportFragmentManager
            .beginTransaction()
            .add(homeSettingsFragment, "HomeSettingFragmentTest")
            .commitNow()
    }

    private fun getSponsoredStoriesPreference(): CheckBoxPreference =
        homeSettingsFragment.findPreference(
            homeSettingsFragment.getPreferenceKey(R.string.pref_key_pocket_sponsored_stories)
        )!!

    private fun getPrivacyReportPreference(): SwitchPreferenceCompat =
        homeSettingsFragment.findPreference(homeSettingsFragment.getPreferenceKey(R.string.pref_key_privacy_report))!!

    private fun getWeatherPreference(): SwitchPreferenceCompat =
        homeSettingsFragment.findPreference(
            homeSettingsFragment.getPreferenceKey(R.string.pref_key_show_homepage_weather_widget)
        )!!
}
