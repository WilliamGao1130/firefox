/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.home.store

import androidx.compose.runtime.Composable
import mozilla.components.service.nimbus.messaging.Message
import org.bluepowerrobotics.bpfox.components.appstate.AppState
import org.bluepowerrobotics.bpfox.compose.MessageCardState
import org.bluepowerrobotics.bpfox.messaging.FenixMessageSurfaceId
import org.bluepowerrobotics.bpfox.termsofuse.store.PrivacyNoticeBannerState

/**
 * State representing the text and formatting for a nimbus message card displayed on the homepage.
 *
 * @property cardState State of the message card.
 * @property message Message for callbacks.
 */
data class NimbusMessageState(val cardState: MessageCardState, val message: Message) {

    /** Companion object for building [NimbusMessageState]. */
    companion object {

        /**
         * Builds a new [NimbusMessageState] from the current [AppState].
         *
         * @param appState State to build the [NimbusMessageState] from.
         * @param privacyNoticeBannerState State of the privacy notice banner. If the privacy notice banner is visible,
         *   we should not show the nimbus message banner.
         */
        @Composable
        internal fun build(
            appState: AppState,
            privacyNoticeBannerState: PrivacyNoticeBannerState,
        ): NimbusMessageState? {
            if (privacyNoticeBannerState.visible) {
                return null
            }
            return appState.messaging.messageToShow[FenixMessageSurfaceId.HOMESCREEN]?.let {
                NimbusMessageState(
                    cardState =
                        MessageCardState.build(
                            message = it,
                            wallpaperState = appState.wallpaperState,
                        ),
                    message = it,
                )
            }
        }
    }
}
