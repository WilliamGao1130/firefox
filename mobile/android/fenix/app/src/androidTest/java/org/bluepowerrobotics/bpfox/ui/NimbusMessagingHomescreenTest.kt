/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui

import android.content.Intent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.v2.AndroidComposeTestRule as AndroidComposeTestRuleV2
import mozilla.components.service.nimbus.messaging.FxNimbusMessaging
import mozilla.components.service.nimbus.messaging.MessageData
import mozilla.components.service.nimbus.messaging.Messaging
import mozilla.components.service.nimbus.messaging.StyleData
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mozilla.experiments.nimbus.Res
import org.bluepowerrobotics.bpfox.FenixApplication
import org.bluepowerrobotics.bpfox.components.appstate.AppAction
import org.bluepowerrobotics.bpfox.helpers.FenixTestRule
import org.bluepowerrobotics.bpfox.helpers.HomeActivityIntentTestRule
import org.bluepowerrobotics.bpfox.helpers.RetryTestRule
import org.bluepowerrobotics.bpfox.helpers.RetryableComposeTestRule
import org.bluepowerrobotics.bpfox.helpers.TestAssetHelper.waitingTime
import org.bluepowerrobotics.bpfox.helpers.perf.DetectMemoryLeaksRule
import org.bluepowerrobotics.bpfox.messaging.FenixMessageSurfaceId
import org.bluepowerrobotics.bpfox.nimbus.FxNimbus
import org.bluepowerrobotics.bpfox.nimbus.HomeScreenSection
import org.bluepowerrobotics.bpfox.nimbus.Homescreen
import org.bluepowerrobotics.bpfox.ui.robots.homeScreen

/**
 * Tests for verifying basic functionality of the Nimbus Home Screen message
 *
 * Verifies a message can be displayed with all of the correct components
 */
class NimbusMessagingHomescreenTest {
    private var messageButtonLabel = "CLICK ME"
    private var messageText = "Some Nimbus Messaging text"
    private var messageTitle = "A Nimbus title"

    @get:Rule(order = 0) val fenixTestRule: FenixTestRule = FenixTestRule()

    @get:Rule(order = 1) val retryTestRule = RetryTestRule(3)

    @get:Rule(order = 2)
    val retryableComposeTestRule = RetryableComposeTestRule {
        AndroidComposeTestRuleV2(
            HomeActivityIntentTestRule.withDefaultSettingsOverrides()
                .withIntent(
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                    }
                )
        ) {
            it.activity
        }
    }

    private val composeTestRule
        get() = retryableComposeTestRule.current

    @get:Rule(order = 3) val memoryLeaksRule = DetectMemoryLeaksRule(composeTestRule = { composeTestRule })

    @OptIn(ExperimentalTestApi::class)
    @Before
    fun setUp() {
        // Set up nimbus message
        FxNimbusMessaging.features.messaging.withInitializer { _, _ ->
            // FML generated objects.
            Messaging(
                messages =
                    mapOf(
                        "test-message" to
                            MessageData(
                                action = "TEST ACTION",
                                style = "TEST STYLE",
                                buttonLabel = Res.string(messageButtonLabel),
                                text = Res.string(messageText),
                                title = Res.string(messageTitle),
                                triggerIfAll = listOf("ALWAYS"),
                            )
                    ),
                styles = mapOf("TEST STYLE" to StyleData()),
                actions = mapOf("TEST ACTION" to "https://example.com"),
                triggers = mapOf("ALWAYS" to "true"),
            )
        }

        // Remove some homescreen features not needed for testing
        FxNimbus.features.homescreen.withInitializer { _, _ ->
            // These are FML generated objects and enums
            Homescreen(
                sectionsEnabled =
                    mapOf(
                        HomeScreenSection.JUMP_BACK_IN to false,
                        HomeScreenSection.POCKET to false,
                        HomeScreenSection.POCKET_SPONSORED_STORIES to false,
                        HomeScreenSection.RECENT_EXPLORATIONS to false,
                        HomeScreenSection.BOOKMARKS to false,
                        HomeScreenSection.TOP_SITES to false,
                    )
            )
        }
        // refresh message store
        val application = (composeTestRule.activity.application as FenixApplication)
        application.restoreMessaging()
        // restoreMessaging() dispatches Restore, which loads messages on Dispatchers.IO.
        // Wait for UpdateMessages to land before re-evaluating, so getNextMessage sees
        // the test message rather than an empty list.
        composeTestRule.waitUntil(waitingTime) {
            application.components.appStore.state.messaging.messages.any { it.id == "test-message" }
        }
        application.components.appStore.dispatch(AppAction.MessagingAction.Evaluate(FenixMessageSurfaceId.HOMESCREEN))
    }

    @Test
    fun testNimbusMessageIsDisplayed() {
        // Checks the home screen card message is displayed correctly
        homeScreen(composeTestRule) {
            verifyNimbusMessageCard(messageTitle, messageText, messageButtonLabel)
        }
    }
}
