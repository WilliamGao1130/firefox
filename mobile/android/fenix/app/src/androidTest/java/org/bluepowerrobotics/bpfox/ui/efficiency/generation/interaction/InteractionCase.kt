/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.generation.interaction

import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.BasePage
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.PageContext
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.Selector

data class InteractionCase(
    val label: String,
    val testRailId: String,
    val page: PageContext.() -> BasePage,
    val interactionSelectorName: String,
    val interactionSelector: Selector,
    val expectedSelectorNames: List<String>,
    val expectedSelectors: List<Selector>,
    val state: String,
) {
    override fun toString(): String = label
}
