/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.selectors

import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.helpers.DataGenerationHelper.getStringResource
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.Selector
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.SelectorStrategy

object SettingsOpenLinksInAppsSelectors {

    val TOOLBAR_TITLE =
        Selector(
            strategy = SelectorStrategy.UIAUTOMATOR_WITH_TEXT_CONTAINS,
            value = getStringResource(R.string.preferences_open_links_in_apps),
            description = "Open link in apps toolbar title",
            groups = listOf("requiredForPage"),
        )

    val all = listOf(TOOLBAR_TITLE)
}
