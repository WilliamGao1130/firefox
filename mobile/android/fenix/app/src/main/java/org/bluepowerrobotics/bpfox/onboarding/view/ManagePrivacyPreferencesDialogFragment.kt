/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.onboarding.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.compose.content
import mozilla.components.lib.state.helpers.StoreProvider.Companion.fragmentStore
import org.bluepowerrobotics.bpfox.components.metrics.installSourcePackage
import org.bluepowerrobotics.bpfox.ext.application
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.onboarding.ManagePrivacyPreferencesDialog
import org.bluepowerrobotics.bpfox.onboarding.store.DefaultPrivacyPreferencesRepository
import org.bluepowerrobotics.bpfox.onboarding.store.PreferenceType
import org.bluepowerrobotics.bpfox.onboarding.store.PrivacyPreferencesAction
import org.bluepowerrobotics.bpfox.onboarding.store.PrivacyPreferencesMiddleware
import org.bluepowerrobotics.bpfox.onboarding.store.PrivacyPreferencesState
import org.bluepowerrobotics.bpfox.onboarding.store.PrivacyPreferencesStore
import org.bluepowerrobotics.bpfox.onboarding.store.PrivacyPreferencesTelemetryMiddleware
import org.bluepowerrobotics.bpfox.settings.SupportUtils
import org.bluepowerrobotics.bpfox.settings.SupportUtils.launchSandboxCustomTab
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme

/** Dialog fragment for managing privacy preferences. */
class ManagePrivacyPreferencesDialogFragment : DialogFragment() {

    private val crashReportingUrl by lazy { sumoUrlFor(SupportUtils.SumoTopic.CRASH_REPORTS) }
    private val usageDataUrl by lazy { sumoUrlFor(SupportUtils.SumoTopic.TECHNICAL_AND_INTERACTION_DATA) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val repository =
            DefaultPrivacyPreferencesRepository(
                settings = requireComponents.settings,
                nimbusSdk = requireComponents.nimbus.sdk,
                crashReporter = requireComponents.analytics.crashReporter,
            )
        val store by
            fragmentStore(
                PrivacyPreferencesState(
                    crashReportingEnabled = repository.getPreference(PreferenceType.CrashReporting),
                    usageDataEnabled = repository.getPreference(PreferenceType.UsageData),
                )
            ) {
                PrivacyPreferencesStore(
                    initialState = it,
                    middlewares =
                        listOf(
                            PrivacyPreferencesMiddleware(repository),
                            PrivacyPreferencesTelemetryMiddleware(
                                installSource =
                                    installSourcePackage(
                                        packageManager = requireContext().application.packageManager,
                                        packageName = requireContext().application.packageName,
                                    )
                            ),
                        ),
                )
            }

        return content {
            FirefoxTheme {
                ManagePrivacyPreferencesDialog(
                    store = store,
                    onDismissRequest = { dismiss() },
                    onCrashReportingLinkClick = {
                        store.dispatch(PrivacyPreferencesAction.CrashReportingLearnMore)
                        launchSandboxCustomTab(requireContext(), crashReportingUrl)
                    },
                    onUsageDataLinkClick = {
                        store.dispatch(PrivacyPreferencesAction.UsageDataUserLearnMore)
                        launchSandboxCustomTab(requireContext(), usageDataUrl)
                    },
                )
            }
        }
    }

    private fun sumoUrlFor(topic: SupportUtils.SumoTopic) = SupportUtils.getSumoURLForTopic(requireContext(), topic)

    /** Companion object for [ManagePrivacyPreferencesDialogFragment]. */
    companion object {
        /** Tag for the [ManagePrivacyPreferencesDialogFragment]. */
        const val TAG = "Privacy preferences dialog"
    }
}
