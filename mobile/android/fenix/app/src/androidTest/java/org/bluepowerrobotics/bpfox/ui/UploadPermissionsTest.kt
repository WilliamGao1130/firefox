/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui

import android.os.Build
import androidx.compose.ui.test.junit4.v2.AndroidComposeTestRule as AndroidComposeTestRuleV2
import androidx.test.filters.SdkSuppress
import mozilla.components.support.ktx.util.PromptAbuserDetector
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.bluepowerrobotics.bpfox.customannotations.Converted
import org.bluepowerrobotics.bpfox.customannotations.SmokeTest
import org.bluepowerrobotics.bpfox.helpers.AppAndSystemHelper.assertExternalAppOpens
import org.bluepowerrobotics.bpfox.helpers.AppAndSystemHelper.closeSystemPhotoAndVideoPicker
import org.bluepowerrobotics.bpfox.helpers.AppAndSystemHelper.denyPermission
import org.bluepowerrobotics.bpfox.helpers.AppAndSystemHelper.grantSystemPermission
import org.bluepowerrobotics.bpfox.helpers.AppAndSystemHelper.verifySystemPhotoAndVideoPickerExists
import org.bluepowerrobotics.bpfox.helpers.FenixTestRule
import org.bluepowerrobotics.bpfox.helpers.HomeActivityIntentTestRule
import org.bluepowerrobotics.bpfox.helpers.MatcherHelper.itemWithResId
import org.bluepowerrobotics.bpfox.helpers.TestAssetHelper.htmlControlsFormAsset
import org.bluepowerrobotics.bpfox.helpers.perf.DetectMemoryLeaksRule
import org.bluepowerrobotics.bpfox.ui.robots.clickPageObject
import org.bluepowerrobotics.bpfox.ui.robots.navigationToolbar

class UploadPermissionsTest {

    @get:Rule(order = 0) val fenixTestRule: FenixTestRule = FenixTestRule()

    private val mockWebServer
        get() = fenixTestRule.mockWebServer

    @get:Rule(order = 1)
    val composeTestRule =
        AndroidComposeTestRuleV2(HomeActivityIntentTestRule.withDefaultSettingsOverrides()) { it.activity }

    @get:Rule(order = 2) val memoryLeaksRule = DetectMemoryLeaksRule(composeTestRule = { composeTestRule })

    @Before
    fun setUp() {
        PromptAbuserDetector.validationsEnabled = false
    }

    @After
    fun tearDown() {
        PromptAbuserDetector.validationsEnabled = true
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/2121537
    @Converted(
        replacedBy = ["org.bluepowerrobotics.bpfox.ui.efficiency.tests.UploadPermissionsTest#fileUploadPermissionTest"],
        bug = 2063263,
        since = "2026-08",
    )
    @SmokeTest
    @Test
    fun fileUploadPermissionTest() {
        val testPage = mockWebServer.htmlControlsFormAsset

        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(testPage.url) {
                clickPageObject(composeTestRule, itemWithResId("upload_file"))
                // Grant app permission to access storage
                grantSystemPermission()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    assertExternalAppOpens("com.google.android.documentsui")
                } else {
                    assertExternalAppOpens("com.android.documentsui")
                }
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/2751914
    @Test
    fun uploadSelectedAudioFilesWhileNoPermissionGrantedTest() {
        val testPage = mockWebServer.htmlControlsFormAsset

        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(testPage.url) {
                clickPageObject(composeTestRule, itemWithResId("audioFileUpload"))
                // Deny app access to voice recording
                denyPermission()
                // Deny app access to audio files storage
                denyPermission()
                verifyPageContent("Choose audio file to upload")
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/2779525
    @Test
    fun uploadSelectedAudioFilesWhenStoragePermissionGrantedTest() {
        val testPage = mockWebServer.htmlControlsFormAsset

        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(testPage.url) {
                clickPageObject(composeTestRule, itemWithResId("audioFileUpload"))
                // Deny app access to voice recording
                denyPermission()
                // Grant app access to audio files storage
                grantSystemPermission()
                assertExternalAppOpens("com.google.android.documentsui")
            }
    }

    // TestRail link: https://mozilla.testrail.io/index.php?/cases/view/2751915
    // The photo picker is only available on devices with API level 33 (TIRAMISU) or higher
    @SdkSuppress(minSdkVersion = 33)
    @Test
    fun uploadSelectedVideoOrImageFilesWhenStoragePermissionGrantedTest() {
        val testPage = mockWebServer.htmlControlsFormAsset

        navigationToolbar(composeTestRule) {}
            .enterURLAndEnterToBrowser(testPage.url) {
                clickPageObject(composeTestRule, itemWithResId("photosUpload"))
                // Deny app access to pictures and video recordings
                denyPermission()
                verifySystemPhotoAndVideoPickerExists()
                closeSystemPhotoAndVideoPicker()
            }
    }
}
