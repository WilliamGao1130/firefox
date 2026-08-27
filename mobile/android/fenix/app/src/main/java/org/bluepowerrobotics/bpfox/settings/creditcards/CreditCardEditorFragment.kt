/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.settings.creditcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.fragment.compose.content
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mozilla.components.lib.state.helpers.StoreProvider.Companion.storeProvider
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.SecureFragment
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.hideToolbar
import org.bluepowerrobotics.bpfox.ext.redirectToReAuth
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.settings.creditcards.ui.CreditCardEditorAction
import org.bluepowerrobotics.bpfox.settings.creditcards.ui.CreditCardEditorEnvironment
import org.bluepowerrobotics.bpfox.settings.creditcards.ui.CreditCardEditorMiddleware
import org.bluepowerrobotics.bpfox.settings.creditcards.ui.CreditCardEditorScreen
import org.bluepowerrobotics.bpfox.settings.creditcards.ui.CreditCardEditorState
import org.bluepowerrobotics.bpfox.settings.creditcards.ui.CreditCardEditorStore
import org.bluepowerrobotics.bpfox.settings.creditcards.ui.DefaultCalendarDataProvider
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme

/** Display a credit card editor for adding and editing a credit card. */
class CreditCardEditorFragment : SecureFragment(), SystemInsetsPaddedFragment {

    private val args by navArgs<CreditCardEditorFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideToolbar()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val store: CreditCardEditorStore = storeProvider.get { state ->
            CreditCardEditorStore(
                initialState = state ?: CreditCardEditorState.Default.copy(inEditMode = args.creditCard != null),
                middleware =
                    listOf(
                        CreditCardEditorMiddleware(
                            CreditCardEditorEnvironment(navigateBack = { findNavController().popBackStack() }),
                            storage = requireComponents.core.autofillStorage,
                            calendarDataProvider = DefaultCalendarDataProvider(),
                            coroutineScope = lifecycleScope,
                        )
                    ),
            )
        }
        return content {
            LaunchedEffect(Unit) {
                store.dispatch(CreditCardEditorAction.Initialization.InitStarted(args.creditCard))
            }

            FirefoxTheme {
                CreditCardEditorScreen(store)
            }
        }
    }

    /**
     * Close the keyboard, any open dialogs or menus and then reauthenticate if the fragment is paused and the user is
     * not navigating to [CreditCardsManagementFragment].
     */
    override fun onPause() {
        redirectToReAuth(
            listOf(R.id.creditCardsManagementFragment),
            findNavController().currentDestination?.id,
            R.id.creditCardEditorFragment,
        )
        super.onPause()
    }
}
