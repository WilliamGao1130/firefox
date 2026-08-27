/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.home.search

import org.bluepowerrobotics.bpfox.components.AppStore
import org.bluepowerrobotics.bpfox.components.appstate.AppAction.SearchAction.SearchEnded

/** Delegate for handling all search related interactions while on the homescreen. */
interface HomeSearchController {
    /** Handle the home content being focused while a browser search is in progress. */
    fun handleHomeContentFocusedWhileSearchIsActive()
}

/**
 * Default handling of all search related interactions while on the homescreen.
 *
 * @param appStore [AppStore] to integrate search related updates with.
 */
class DefaultHomeSearchController(private val appStore: AppStore) : HomeSearchController {
    override fun handleHomeContentFocusedWhileSearchIsActive() {
        appStore.dispatch(SearchEnded)
    }
}
