/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.components

import org.bluepowerrobotics.bpfox.settings.ToolbarShortcutSettingsSearchProvider
import org.bluepowerrobotics.bpfox.settings.ai.AIControlsSearchProvider
import org.bluepowerrobotics.bpfox.settings.datachoices.DataChoicesSearchProvider
import org.bluepowerrobotics.bpfox.settings.labs.FirefoxLabsSettingsSearchProvider
import org.bluepowerrobotics.bpfox.settings.pagesummaries.PageSummariesSettingsSearchProvider
import org.bluepowerrobotics.bpfox.settings.settingssearch.SettingsSearchProvider
import org.bluepowerrobotics.bpfox.summarization.onboarding.SummarizationFeatureDiscoveryConfiguration

/**
 * Builds the list of [SettingsSearchProvider]s covering settings the XML-parsing path in
 * [org.bluepowerrobotics.bpfox.settings.settingssearch.DefaultFenixSettingsIndexer] cannot reach: Compose screens with no backing
 * preference file, and entries inside an XML screen that the parser does not emit (an empty `PreferenceCategory` whose
 * contents are built in code, for instance).
 *
 * This is the source of truth for which providers exist. It is wired into the settings indexer from [Components] and is
 * exercised by `SettingsSearchProviderRegistrationTest` so that a screen losing its provider fails a test rather than
 * silently disappearing from settings search.
 *
 * @param summarizationFeatureConfiguration Gates whether the Page Summaries screen is indexed.
 */
internal fun settingsSearchProviders(
    summarizationFeatureConfiguration: SummarizationFeatureDiscoveryConfiguration
): List<SettingsSearchProvider> =
    listOf(
        DataChoicesSearchProvider,
        AIControlsSearchProvider,
        PageSummariesSettingsSearchProvider(summarizationFeatureConfiguration),
        FirefoxLabsSettingsSearchProvider(),
        ToolbarShortcutSettingsSearchProvider,
    )
