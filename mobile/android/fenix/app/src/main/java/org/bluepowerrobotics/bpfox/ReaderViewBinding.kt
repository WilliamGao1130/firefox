/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import mozilla.components.lib.state.helpers.AbstractBinding
import org.bluepowerrobotics.bpfox.browser.readermode.ReaderModeController
import org.bluepowerrobotics.bpfox.components.AppStore
import org.bluepowerrobotics.bpfox.components.appstate.AppAction
import org.bluepowerrobotics.bpfox.components.appstate.AppState
import org.bluepowerrobotics.bpfox.components.appstate.readerview.ReaderViewState.Active
import org.bluepowerrobotics.bpfox.components.appstate.readerview.ReaderViewState.Dismiss
import org.bluepowerrobotics.bpfox.components.appstate.readerview.ReaderViewState.None
import org.bluepowerrobotics.bpfox.components.appstate.readerview.ReaderViewState.ShowControls

/**
 * A binding for observing [AppState.readerViewState] in the [AppStore] and toggling the reader view feature and
 * controls.
 *
 * @param appStore The [AppStore] used to observe [AppState.readerViewState].
 * @param readerMenuController The [ReaderModeController] that will used for toggling the reader view feature and
 *   controls.
 * @param mainDispatcher The [CoroutineDispatcher] on which the state observation and updates will occur. Defaults to
 *   [Dispatchers.Main].
 */
class ReaderViewBinding(
    private val appStore: AppStore,
    private val readerMenuController: ReaderModeController,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
) : AbstractBinding<AppState>(appStore, mainDispatcher) {

    override suspend fun onState(flow: Flow<AppState>) {
        flow
            .map { state -> state.readerViewState }
            .distinctUntilChanged()
            .collect { state ->
                when (state) {
                    Active -> {
                        readerMenuController.showReaderView()
                        appStore.dispatch(AppAction.ReaderViewAction.Reset)
                    }

                    Dismiss -> {
                        readerMenuController.hideReaderView()
                        appStore.dispatch(AppAction.ReaderViewAction.Reset)
                    }

                    ShowControls -> {
                        readerMenuController.showControls()
                        appStore.dispatch(AppAction.ReaderViewAction.Reset)
                    }

                    None -> Unit
                }
            }
    }
}
