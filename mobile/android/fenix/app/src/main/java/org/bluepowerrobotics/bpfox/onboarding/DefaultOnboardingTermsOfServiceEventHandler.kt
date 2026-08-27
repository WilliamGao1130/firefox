/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.onboarding

import mozilla.components.support.ktx.kotlin.ifNullOrEmpty
import org.bluepowerrobotics.bpfox.onboarding.view.OnboardingTermsOfServiceEventHandler
import org.bluepowerrobotics.bpfox.settings.SupportUtils
import org.bluepowerrobotics.bpfox.termsofuse.TOU_VERSION
import org.bluepowerrobotics.bpfox.utils.Settings

/** Default implementation for [OnboardingTermsOfServiceEventHandler]. */
class DefaultOnboardingTermsOfServiceEventHandler(
    private val telemetryRecorder: OnboardingTelemetryRecorder,
    private val openLink: (String) -> Unit,
    private val showManagePrivacyPreferencesDialog: () -> Unit,
    private val settings: Settings,
    private val startGlean: () -> Unit,
    private val currentTimeMillis: () -> Long = { System.currentTimeMillis() },
) : OnboardingTermsOfServiceEventHandler {

    override fun onTermsOfServiceLinkClicked(url: String) {
        telemetryRecorder.onTermsOfServiceLinkClick()
        openLink(
            url.trim().ifNullOrEmpty {
                SupportUtils.getMozillaPageUrl(SupportUtils.MozillaPage.TERMS_OF_SERVICE)
            }
        )
    }

    override fun onPrivacyNoticeLinkClicked(url: String) {
        telemetryRecorder.onTermsOfServicePrivacyNoticeLinkClick()
        openLink(
            url.trim().ifNullOrEmpty {
                SupportUtils.getMozillaPageUrl(SupportUtils.MozillaPage.PRIVACY_NOTICE)
            }
        )
    }

    override fun onManagePrivacyPreferencesLinkClicked() {
        telemetryRecorder.onTermsOfServiceManagePrivacyPreferencesLinkClick()
        showManagePrivacyPreferencesDialog()
    }

    override fun onAcceptTermsButtonClicked() {
        telemetryRecorder.onTermsOfServiceManagerAcceptTermsButtonClick()
        settings.hasAcceptedTermsOfService = true
        settings.termsOfUseAcceptedVersion = TOU_VERSION
        settings.termsOfUseAcceptedTimeInMillis = currentTimeMillis()
        startGlean()
    }
}
