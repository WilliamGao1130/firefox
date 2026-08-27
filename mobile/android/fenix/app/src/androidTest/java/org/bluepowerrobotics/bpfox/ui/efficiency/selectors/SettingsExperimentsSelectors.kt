/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.selectors

import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.Selector
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.SelectorStrategy

object SettingsExperimentsSelectors {

    val NAVIGATE_BACK_TOOLBAR_BUTTON =
        Selector(
            strategy = SelectorStrategy.ESPRESSO_BY_CONTENT_DESC,
            value = "Navigate up",
            description = "Navigate back toolbar button",
            groups = listOf("requiredForPage"),
        )

    val all = listOf(NAVIGATE_BACK_TOOLBAR_BUTTON)
}
