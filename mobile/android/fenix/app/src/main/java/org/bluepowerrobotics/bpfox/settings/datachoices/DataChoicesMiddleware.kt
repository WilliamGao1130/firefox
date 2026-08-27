/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.settings.datachoices

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.concept.engine.Engine
import mozilla.components.lib.crash.CrashReporter
import mozilla.components.lib.crash.store.CrashReportOption
import mozilla.components.lib.state.Middleware
import mozilla.components.lib.state.Store
import mozilla.components.service.nimbus.NimbusApi
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.components.metrics.MetricController
import org.bluepowerrobotics.bpfox.components.metrics.MetricServiceType
import org.bluepowerrobotics.bpfox.crashes.SettingsCrashReportCache
import org.bluepowerrobotics.bpfox.debugsettings.gleandebugtools.DefaultGleanDebugToolsStorage
import org.bluepowerrobotics.bpfox.ext.nav
import org.bluepowerrobotics.bpfox.settings.SupportUtils
import org.bluepowerrobotics.bpfox.utils.Settings

@Suppress("LongParameterList")
internal class DataChoicesMiddleware(
    private val settings: Settings,
    private val nimbusSdk: NimbusApi,
    private val engine: Engine,
    private val metrics: MetricController,
    private val crashReporter: CrashReporter,
    private val learnMoreClicked: (sumoTopic: SupportUtils.SumoTopic) -> Unit,
    private val navController: NavController?,
    private val crashReportCache: SettingsCrashReportCache = SettingsCrashReportCache(settings),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) : Middleware<DataChoicesState, DataChoicesAction> {

    override fun invoke(
        store: Store<DataChoicesState, DataChoicesAction>,
        next: (DataChoicesAction) -> Unit,
        action: DataChoicesAction,
    ) {
        next(action)

        when (action) {
            is ViewCreated ->
                scope.launch {
                    store.dispatch(
                        SettingsLoaded(
                            telemetryEnabled = settings.isTelemetryEnabled,
                            usagePingEnabled = settings.isDailyUsagePingEnabled,
                            studiesEnabled = settings.isExperimentationEnabled,
                            showMeasurementDataSection = settings.hasMadeMarketingTelemetrySelection,
                            measurementDataEnabled = settings.isMarketingTelemetryEnabled,
                            crashReportOption = crashReportCache.getReportOption(),
                        )
                    )
                }
            is ChoiceAction.TelemetryClicked -> {
                updateTelemetryChoice()
                store.dispatch(StudiesLoaded(settings.isExperimentationEnabled))
            }
            is ChoiceAction.MeasurementDataClicked -> {
                updateMarketingDataChoice()
            }
            is ChoiceAction.UsagePingClicked -> {
                updateUsageChoice()
            }
            is ChoiceAction.StudiesClicked -> {
                val navAction = DataChoicesFragmentDirections.actionDataChoicesFragmentToStudiesFragment()
                navController?.nav(R.id.dataChoicesFragment, navAction)
            }
            is ChoiceAction.ReportOptionClicked ->
                scope.launch {
                    updateCrashChoice(action.reportOption)
                }

            LearnMore.MeasurementDataLearnMoreClicked -> {
                learnMoreClicked(SupportUtils.SumoTopic.MARKETING_DATA)
            }
            LearnMore.CrashLearnMoreClicked -> {
                learnMoreClicked(SupportUtils.SumoTopic.CRASH_REPORTS)
            }
            LearnMore.TelemetryLearnMoreClicked -> {
                learnMoreClicked(SupportUtils.SumoTopic.TECHNICAL_AND_INTERACTION_DATA)
            }
            LearnMore.UsagePingLearnMoreClicked -> {
                learnMoreClicked(SupportUtils.SumoTopic.USAGE_PING_SETTINGS)
            }
            is StudiesLoaded -> {}
            is SettingsLoaded -> {}
            is ChoiceAction.ScrolledToItem -> {}
        }
    }

    private fun updateMarketingDataChoice() {
        val newValue = !settings.isMarketingTelemetryEnabled
        settings.isMarketingTelemetryEnabled = newValue
        if (newValue) {
            metrics.start(MetricServiceType.Marketing)
        } else {
            metrics.stop(MetricServiceType.Marketing)
        }
    }

    private fun updateTelemetryChoice() {
        val newValue = !settings.isTelemetryEnabled
        settings.isTelemetryEnabled = newValue
        if (newValue) {
            metrics.start(MetricServiceType.Data)
            crashReporter.setTelemetryEnabled(true)
            if (!settings.hasUserDisabledExperimentation) {
                settings.isExperimentationEnabled = true
                nimbusSdk.experimentParticipation = true
            }
            engine.notifyTelemetryPrefChanged(true)
        } else {
            metrics.stop(MetricServiceType.Data)
            crashReporter.setTelemetryEnabled(false)
            settings.isExperimentationEnabled = false
            nimbusSdk.experimentParticipation = false
            engine.notifyTelemetryPrefChanged(false)
            DefaultGleanDebugToolsStorage(settings).clearPersistedDebugViewTag()
        }
        // Reset experiment identifiers on both opt-in and opt-out; it's likely
        // that in future we will need to pass in the new telemetry client_id
        // to this method when the user opts back in.
        nimbusSdk.resetTelemetryIdentifiers()
    }

    private fun updateUsageChoice() {
        val newValue = !settings.isDailyUsagePingEnabled
        settings.isDailyUsagePingEnabled = newValue
        with(metrics) {
            if (newValue) {
                start(MetricServiceType.UsageReporting)
            } else {
                stop(MetricServiceType.UsageReporting)
            }
        }
    }

    private suspend fun updateCrashChoice(newValue: CrashReportOption) {
        crashReportCache.setReportOption(newValue)
    }
}
