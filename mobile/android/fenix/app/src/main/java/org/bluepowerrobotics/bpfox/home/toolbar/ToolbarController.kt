/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.home.toolbar

import org.bluepowerrobotics.bpfox.GleanMetrics.Events
import org.bluepowerrobotics.bpfox.components.AppStore
import org.bluepowerrobotics.bpfox.components.appstate.AppAction.SearchAction.SearchStarted

/** An interface that handles the view manipulation of the home screen toolbar. */
interface ToolbarController {
    /** @see [ToolbarInteractor.onNavigateSearch] */
    fun handleNavigateSearch()
}

/** The default implementation of [ToolbarController]. */
class DefaultToolbarController(private val appStore: AppStore) : ToolbarController {
    override fun handleNavigateSearch() {
        appStore.dispatch(SearchStarted())
        Events.searchBarTapped.record(Events.SearchBarTappedExtra("HOME"))
    }
}
