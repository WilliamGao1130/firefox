/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.selectors

import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.helpers.DataGenerationHelper.getStringResource
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.Selector
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.SelectorStrategy

object MicrosurveysSelectors {

    val CONTINUE_SURVEY_BUTTON =
        Selector(
            strategy = SelectorStrategy.COMPOSE_BY_TEXT,
            value = getStringResource(R.string.micro_survey_continue_button_label),
            description = "Survey Continue button",
            // Will see what groups we'll have once e start converting UI tests
            groups = listOf("browserSurvey"),
        )

    val all = listOf(CONTINUE_SURVEY_BUTTON)
}
